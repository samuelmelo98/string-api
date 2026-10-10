package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.dashboard.DashboardOrdemServicoDTO;
import org.stringtecnologia.string_api.model.dto.dashboard.MetricaEntregasTecnicoDTO;
import org.stringtecnologia.string_api.model.dto.dashboard.MetricaOrdemServicoDTO;
import org.stringtecnologia.string_api.model.enums.StatusOrcamento;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;
import org.stringtecnologia.string_api.repository.projection.MetricaOrdemServicoProjection;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final ZoneId ZONE_ID =
            ZoneId.of("America/Sao_Paulo");

    private final OrdemServicoRepository ordemServicoRepository;

    @Transactional(readOnly = true)
    public DashboardOrdemServicoDTO buscarMetricasOrdensServico() {

        LocalDate hoje = LocalDate.now(ZONE_ID);

        return new DashboardOrdemServicoDTO(
                buscarUltimaSemana(hoje),
                buscarEntreguesUltimos6Dias(hoje),
                buscarUltimos30Dias(hoje),
                buscarUltimos365Dias(hoje),
                buscarEntreguesPorTecnico(hoje)
        );
    }

    /*
     * Última semana FECHADA:
     *
     * segunda até sábado.
     *
     * Se hoje for domingo:
     * pega segunda até ontem.
     *
     * Se hoje for segunda até sábado:
     * pega segunda até sábado da semana anterior.
     */
    private MetricaOrdemServicoDTO buscarUltimaSemana(
            LocalDate hoje
    ) {

        LocalDate segunda;

        if (hoje.getDayOfWeek() == DayOfWeek.SUNDAY) {

            segunda = hoje.minusDays(6);

        } else {

            LocalDate segundaSemanaAtual =
                    hoje.with(
                            TemporalAdjusters.previousOrSame(
                                    DayOfWeek.MONDAY
                            )
                    );

            segunda = segundaSemanaAtual.minusWeeks(1);
        }

        LocalDate sabado = segunda.plusDays(5);

        return consultar(
                segunda,
                sabado
        );
    }

    private MetricaOrdemServicoDTO buscarUltimos30Dias(
            LocalDate hoje
    ) {

        LocalDate inicio = hoje.minusDays(29);

        return consultar(
                inicio,
                hoje
        );
    }

    private MetricaOrdemServicoDTO buscarUltimos365Dias(
            LocalDate hoje
    ) {

        LocalDate inicio = hoje.minusDays(364);

        return consultar(
                inicio,
                hoje
        );
    }

    private MetricaOrdemServicoDTO consultar(
            LocalDate inicio,
            LocalDate fim
    ) {

        Instant inicioDataHora =
                inicioDoDia(inicio);

        Instant fimExclusivo =
                inicioDoDia(fim.plusDays(1));

        MetricaOrdemServicoProjection resultado =
                ordemServicoRepository.buscarMetricas(
                        inicioDataHora,
                        fimExclusivo
                );

        long total =
                valor(resultado.getTotal());

        long abertas =
                valor(resultado.getAbertas());

        long autorizadas =
                valor(resultado.getAutorizadas());

        long entregues =
                valor(resultado.getEntregues());

        long naoAutorizadas =
                valor(resultado.getNaoAutorizadas());

        long outros =
                Math.max(
                        0,
                        total
                                - abertas
                                - autorizadas
                                - entregues
                                - naoAutorizadas
                );

        return new MetricaOrdemServicoDTO(
                inicio,
                fim,
                total,
                abertas,
                autorizadas,
                entregues,
                naoAutorizadas,
                outros
        );
    }

    private MetricaOrdemServicoDTO buscarEntreguesUltimos6Dias(
            LocalDate hoje
    ) {

        LocalDate inicio =
                hoje.minusDays(5);

        Instant inicioDataHora =
                inicioDoDia(inicio);

        Instant fimExclusivo =
                inicioDoDia(hoje.plusDays(1));

        long entregues =
                ordemServicoRepository.contarEntreguesNoPeriodo(
                        inicioDataHora,
                        fimExclusivo
                );

        return new MetricaOrdemServicoDTO(
                inicio,
                hoje,
                entregues,
                0L,
                0L,
                entregues,
                0L,
                0L
        );
    }

    private List<MetricaEntregasTecnicoDTO> buscarEntreguesPorTecnico(
            LocalDate hoje
    ) {

        Instant inicio =
                inicioDoDia(
                        hoje.minusDays(5)
                );

        Instant fimExclusivo =
                inicioDoDia(
                        hoje.plusDays(1)
                );

        Map<Long, BigDecimal> materialPorTecnico =
                buscarMaterialPorTecnico(
                        inicio,
                        fimExclusivo
                );

        return ordemServicoRepository
                .buscarEntregasPorTecnico(
                        inicio,
                        fimExclusivo
                )
                .stream()
                .map(item -> {

                    Long tecnicoId =
                            item.getTecnicoId();

                    String tecnicoNome =
                            resolverNomeTecnico(
                                    tecnicoId,
                                    item.getTecnicoNome()
                            );

                    return new MetricaEntregasTecnicoDTO(
                            tecnicoId,
                            tecnicoNome,
                            valor(item.getQuantidadeEntregues()),
                            moedaOuZero(item.getValorTotal()),
                            materialPorTecnico.getOrDefault(
                                    tecnicoId,
                                    BigDecimal.ZERO
                            )
                    );
                })
                .toList();
    }

    private Map<Long, BigDecimal> buscarMaterialPorTecnico(
            Instant inicio,
            Instant fimExclusivo
    ) {

        Map<Long, BigDecimal> materialPorTecnico =
                new HashMap<>();

        ordemServicoRepository
                .buscarMaterialPorTecnico(
                        inicio,
                        fimExclusivo,
                        StatusOrcamento.APROVADO
                )
                .forEach(item ->
                        materialPorTecnico.put(
                                item.getTecnicoId(),
                                moedaOuZero(
                                        item.getValorMaterial()
                                )
                        )
                );

        return materialPorTecnico;
    }

    private String resolverNomeTecnico(
            Long tecnicoId,
            String tecnicoNome
    ) {

        if (tecnicoId == null) {
            return "Sem técnico responsável";
        }

        if (
                tecnicoNome == null
                        || tecnicoNome.isBlank()
        ) {
            return "Técnico #" + tecnicoId;
        }

        return tecnicoNome;
    }

    private Instant inicioDoDia(
            LocalDate data
    ) {

        return data
                .atStartOfDay(ZONE_ID)
                .toInstant();
    }

    private long valor(
            Long valor
    ) {

        return valor != null
                ? valor
                : 0L;
    }

    private BigDecimal moedaOuZero(
            BigDecimal valor
    ) {

        return valor != null
                ? valor
                : BigDecimal.ZERO;
    }
}