package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.dto.relatorio.RelatorioSemanalTecnicoDTO;
import org.stringtecnologia.string_api.model.dto.relatorio.RelatorioSemanalTecnicoItemDTO;
import org.stringtecnologia.string_api.model.dto.relatorio.RelatorioSemanalTecnicoPagamentoDTO;
import org.stringtecnologia.string_api.model.enums.StatusOrcamento;
import org.stringtecnologia.string_api.model.enums.StatusPagamento;
import org.stringtecnologia.string_api.repository.OrdemServicoOrcamentoRepository;
import org.stringtecnologia.string_api.repository.OrdemServicoPagamentoRepository;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;
import org.stringtecnologia.string_api.repository.projection.RelatorioSemanalTecnicoMaterialProjection;
import org.stringtecnologia.string_api.repository.projection.RelatorioSemanalTecnicoOrdemProjection;
import org.stringtecnologia.string_api.repository.projection.RelatorioSemanalTecnicoPagamentoProjection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioSemanalTecnicoService {

    private static final ZoneId ZONE_ID =
            ZoneId.of("America/Sao_Paulo");

    private static final BigDecimal DOIS =
            BigDecimal.valueOf(2);

    private final OrdemServicoRepository
            ordemServicoRepository;

    private final OrdemServicoOrcamentoRepository
            ordemServicoOrcamentoRepository;

    private final OrdemServicoPagamentoRepository
            ordemServicoPagamentoRepository;


    public List<RelatorioSemanalTecnicoDTO>
    gerarRelatorioSemanaAnterior(
            Long tecnicoId
    ) {

        LocalDate hoje =
                LocalDate.now(ZONE_ID);

        PeriodoSemana periodo =
                calcularUltimaSemanaCompleta(
                        hoje
                );

        return gerarRelatorioPorPeriodo(
                periodo.inicio(),
                periodo.fim(),
                tecnicoId
        );
    }

    private List<RelatorioSemanalTecnicoDTO>
    montarRelatorios(
            List<RelatorioSemanalTecnicoOrdemProjection> ordens,
            Map<Long, BigDecimal> materiaisPorOrdem,
            Map<Long, List<RelatorioSemanalTecnicoPagamentoDTO>>
                    pagamentosPorOrdem,
            PeriodoSemana periodo
    ) {

        Map<Long, RelatorioSemanalTecnicoDTO> relatorios =
                new LinkedHashMap<>();

        for (
                RelatorioSemanalTecnicoOrdemProjection ordem
                : ordens
        ) {

            RelatorioSemanalTecnicoDTO relatorio =
                    relatorios.computeIfAbsent(
                            ordem.getTecnicoId(),
                            tecnicoId ->
                                    criarRelatorio(
                                            ordem,
                                            periodo
                                    )
                    );

            BigDecimal valorOrdemServico =
                    obterValorOrdemServico(
                            ordem
                    );

            BigDecimal valorMaterial =
                    materiaisPorOrdem
                            .getOrDefault(
                                    ordem.getOrdemServicoId(),
                                    BigDecimal.ZERO
                            );

            List<RelatorioSemanalTecnicoPagamentoDTO>
                    pagamentos =
                    pagamentosPorOrdem
                            .getOrDefault(
                                    ordem.getOrdemServicoId(),
                                    List.of()
                            );

            BigDecimal totalPago =
                    pagamentos.stream()
                            .map(
                                    RelatorioSemanalTecnicoPagamentoDTO
                                            ::getValor
                            )
                            .map(
                                    this::valorSeguro
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            RelatorioSemanalTecnicoItemDTO item =
                    RelatorioSemanalTecnicoItemDTO.builder()
                            .ordemServicoId(
                                    ordem.getOrdemServicoId()
                            )
                            .numeroOrdemServico(
                                    ordem.getNumeroOrdemServico()
                            )
                            .aparelhoId(
                                    ordem.getAparelhoId()
                            )
                            .modelo(
                                    ordem.getModelo()
                            )
                            .modeloComercial(
                                    ordem.getModeloComercial()
                            )
                            .numeroSerie(
                                    ordem.getNumeroSerie()
                            )
                            .clienteId(
                                    ordem.getClienteId()
                            )
                            .clienteNome(
                                    ordem.getClienteNome()
                            )
                            .dataEntrega(
                                    ordem.getDataEntrega()
                            )
                            .valorOrdemServico(
                                    valorOrdemServico
                            )
                            .valorMaterial(
                                    valorMaterial
                            )
                            .totalPago(
                                    totalPago
                            )
                            .pagamentos(
                                    new ArrayList<>(
                                            pagamentos
                                    )
                            )
                            .build();

            relatorio
                    .getAparelhos()
                    .add(item);

            relatorio.setTotalOrdensServico(
                    relatorio
                            .getTotalOrdensServico()
                            .add(
                                    valorOrdemServico
                            )
            );

            relatorio.setTotalMateriais(
                    relatorio
                            .getTotalMateriais()
                            .add(
                                    valorMaterial
                            )
            );
        }

        relatorios.values()
                .forEach(
                        this::calcularTotais
                );

        return new ArrayList<>(
                relatorios.values()
        );
    }


    private RelatorioSemanalTecnicoDTO criarRelatorio(
            RelatorioSemanalTecnicoOrdemProjection ordem,
            PeriodoSemana periodo
    ) {

        return RelatorioSemanalTecnicoDTO.builder()
                .tecnicoId(
                        ordem.getTecnicoId()
                )
                .tecnicoNome(
                        ordem.getTecnicoNome()
                )
                .tecnicoEmail(
                        ordem.getTecnicoEmail()
                )
                .inicio(
                        periodo.inicio()
                )
                .fim(
                        periodo.fim()
                )
                .aparelhos(
                        new ArrayList<>()
                )
                .totalOrdensServico(
                        BigDecimal.ZERO
                )
                .totalMateriais(
                        BigDecimal.ZERO
                )
                .valorLiquido(
                        BigDecimal.ZERO
                )
                .valorTecnico(
                        BigDecimal.ZERO
                )
                .build();
    }


    private Map<Long, BigDecimal>
    buscarMateriaisPorOrdem(
            List<Long> ordemServicoIds
    ) {

        return ordemServicoOrcamentoRepository
                .buscarMateriaisPorOrdensServico(
                        ordemServicoIds,
                        StatusOrcamento.APROVADO
                )
                .stream()
                .collect(
                        Collectors.toMap(
                                RelatorioSemanalTecnicoMaterialProjection
                                        ::getOrdemServicoId,

                                material ->
                                        valorSeguro(
                                                material
                                                        .getValorMaterial()
                                        ),

                                (valorAtual, valorNovo) ->
                                        valorNovo,

                                LinkedHashMap::new
                        )
                );
    }


    private Map<Long,
            List<RelatorioSemanalTecnicoPagamentoDTO>>
    buscarPagamentosPorOrdem(
            List<Long> ordemServicoIds
    ) {

        List<RelatorioSemanalTecnicoPagamentoProjection>
                pagamentos =
                ordemServicoPagamentoRepository
                        .buscarPagamentosPorOrdensServico(
                                ordemServicoIds,
                                StatusPagamento.ATIVO
                        );

        return pagamentos.stream()
                .collect(
                        Collectors.groupingBy(
                                RelatorioSemanalTecnicoPagamentoProjection
                                        ::getOrdemServicoId,

                                LinkedHashMap::new,

                                Collectors.mapping(
                                        this::converterPagamento,
                                        Collectors.toList()
                                )
                        )
                );
    }


    private RelatorioSemanalTecnicoPagamentoDTO
    converterPagamento(
            RelatorioSemanalTecnicoPagamentoProjection pagamento
    ) {

        return RelatorioSemanalTecnicoPagamentoDTO.builder()
                .formaPagamento(
                        pagamento.getFormaPagamento()
                )
                .valor(
                        valorSeguro(
                                pagamento.getValor()
                        )
                )
                .parcelas(
                        pagamento.getParcelas()
                )
                .build();
    }


    private BigDecimal obterValorOrdemServico(
            RelatorioSemanalTecnicoOrdemProjection ordem
    ) {

        if (ordem.getValorFinal() != null) {
            return valorSeguro(
                    ordem.getValorFinal()
            );
        }

        if (ordem.getValorOrcamento() != null) {
            return valorSeguro(
                    ordem.getValorOrcamento()
            );
        }

        return BigDecimal.ZERO;
    }


    private void calcularTotais(
            RelatorioSemanalTecnicoDTO relatorio
    ) {

        BigDecimal totalOrdens =
                valorSeguro(
                        relatorio
                                .getTotalOrdensServico()
                );

        BigDecimal totalMateriais =
                valorSeguro(
                        relatorio
                                .getTotalMateriais()
                );

        BigDecimal valorLiquido =
                totalOrdens.subtract(
                        totalMateriais
                );

        BigDecimal valorTecnico =
                valorLiquido.divide(
                        DOIS,
                        2,
                        RoundingMode.HALF_UP
                );

        relatorio.setValorLiquido(
                valorLiquido
        );

        relatorio.setValorTecnico(
                valorTecnico
        );
    }


    private PeriodoSemana
    calcularUltimaSemanaCompleta(
            LocalDate hoje
    ) {

        LocalDate ultimoSabado =
                hoje.with(
                        TemporalAdjusters.previous(
                                DayOfWeek.SATURDAY
                        )
                );

        LocalDate segunda =
                ultimoSabado.minusDays(5);

        return new PeriodoSemana(
                segunda,
                ultimoSabado
        );
    }


    private BigDecimal valorSeguro(
            BigDecimal valor
    ) {

        return valor != null
                ? valor
                : BigDecimal.ZERO;
    }


    private record PeriodoSemana(
            LocalDate inicio,
            LocalDate fim
    ) {
    }

    public List<RelatorioSemanalTecnicoDTO>
    gerarRelatorioPorPeriodo(
            LocalDate inicio,
            LocalDate fim,
            Long tecnicoId
    ) {

        if (inicio == null || fim == null) {
            throw new IllegalArgumentException(
                    "O período inicial e final é obrigatório"
            );
        }

        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial"
            );
        }

        LocalDateTime inicioPeriodo =
                inicio.atStartOfDay();

        LocalDateTime fimExclusivo =
                fim.plusDays(1)
                        .atStartOfDay();

        List<RelatorioSemanalTecnicoOrdemProjection> ordens =
                ordemServicoRepository
                        .buscarOrdensEntreguesPorPeriodo(
                                inicioPeriodo,
                                fimExclusivo,
                                tecnicoId
                        );

        if (ordens.isEmpty()) {
            return List.of();
        }

        List<Long> ordemServicoIds =
                ordens.stream()
                        .map(
                                RelatorioSemanalTecnicoOrdemProjection
                                        ::getOrdemServicoId
                        )
                        .toList();

        Map<Long, BigDecimal> materiaisPorOrdem =
                buscarMateriaisPorOrdem(
                        ordemServicoIds
                );

        Map<Long, List<RelatorioSemanalTecnicoPagamentoDTO>>
                pagamentosPorOrdem =
                buscarPagamentosPorOrdem(
                        ordemServicoIds
                );

        return montarRelatorios(
                ordens,
                materiaisPorOrdem,
                pagamentosPorOrdem,
                new PeriodoSemana(
                        inicio,
                        fim
                )
        );
    }
}