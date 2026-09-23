package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.dashboard.DashboardOrdemServicoDTO;
import org.stringtecnologia.string_api.model.dto.dashboard.MetricaOrdemServicoDTO;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;
import org.stringtecnologia.string_api.repository.projection.MetricaOrdemServicoProjection;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final ZoneId ZONE_ID =
            ZoneId.of("America/Sao_Paulo");

    private static final ZoneId DATABASE_ZONE =
            ZoneOffset.UTC;

    private final OrdemServicoRepository ordemServicoRepository;

    @Transactional(readOnly = true)
    public DashboardOrdemServicoDTO buscarMetricasOrdensServico() {

        LocalDate hoje = LocalDate.now(ZONE_ID);

        return new DashboardOrdemServicoDTO(
                buscarUltimaSemana(hoje),
                buscarEntreguesUltimos6Dias(hoje),
                buscarUltimos30Dias(hoje),
                buscarUltimos365Dias(hoje)
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

        LocalDate sabado =
                segunda.plusDays(5);

        return consultar(
                segunda,
                sabado
        );
    }

    private MetricaOrdemServicoDTO buscarUltimos30Dias(
            LocalDate hoje
    ) {

        LocalDate inicio =
                hoje.minusDays(29);

        return consultar(
                inicio,
                hoje
        );
    }

    private MetricaOrdemServicoDTO buscarUltimos365Dias(
            LocalDate hoje
    ) {

        LocalDate inicio =
                hoje.minusDays(364);

        return consultar(
                inicio,
                hoje
        );
    }

    private MetricaOrdemServicoDTO consultar(
            LocalDate inicio,
            LocalDate fim
    ) {

        LocalDateTime inicioDataHora =
                inicio
                        .atStartOfDay(ZONE_ID)
                        .withZoneSameInstant(DATABASE_ZONE)
                        .toLocalDateTime();

        LocalDateTime fimExclusivo =
                fim
                        .plusDays(1)
                        .atStartOfDay(ZONE_ID)
                        .withZoneSameInstant(DATABASE_ZONE)
                        .toLocalDateTime();


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

    private long valor(Long valor) {
        return valor != null
                ? valor
                : 0L;
    }

    private MetricaOrdemServicoDTO buscarEntreguesUltimos6Dias(
            LocalDate hoje
    ) {
        LocalDate inicio = hoje.minusDays(5);

        LocalDateTime inicioDataHora = inicio
                .atStartOfDay(ZONE_ID)
                .withZoneSameInstant(DATABASE_ZONE)
                .toLocalDateTime();

        LocalDateTime fimExclusivo = hoje
                .plusDays(1)
                .atStartOfDay(ZONE_ID)
                .withZoneSameInstant(DATABASE_ZONE)
                .toLocalDateTime();

        long entregues = ordemServicoRepository.contarEntreguesNoPeriodo(
                inicioDataHora,
                fimExclusivo
        );

        return new MetricaOrdemServicoDTO(
                inicio,
                hoje,
                entregues, // total do card: somente entregues
                0L,        // abertas
                0L,        // autorizadas
                entregues,
                0L,        // não autorizadas
                0L         // outros
        );
    }
}
