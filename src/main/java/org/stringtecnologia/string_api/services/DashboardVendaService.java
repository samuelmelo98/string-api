package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.venda.DashboardVendaDTO;
import org.stringtecnologia.string_api.model.dto.venda.MetricaVendaDTO;
import org.stringtecnologia.string_api.model.enums.StatusVenda;
import org.stringtecnologia.string_api.repository.VendaRepository;
import org.stringtecnologia.string_api.repository.projection.ResumoVendaProjection;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class DashboardVendaService {

    private static final ZoneId ZONA =
            ZoneId.of(
                    "America/Sao_Paulo"
            );

    private final VendaRepository vendaRepository;


    @Transactional(readOnly = true)
    public DashboardVendaDTO buscar() {

        LocalDate hoje =
                LocalDate.now(
                        ZONA
                );

        return new DashboardVendaDTO(
                buscarDiaria(hoje),
                buscarSemanal(hoje),
                buscarMensal(hoje),
                buscarAnual(hoje)
        );
    }


    private MetricaVendaDTO buscarDiaria(
            LocalDate hoje
    ) {

        return buscarPeriodo(
                hoje,
                hoje
        );
    }


    private MetricaVendaDTO buscarSemanal(
            LocalDate hoje
    ) {

        LocalDate inicio =
                hoje.with(
                        TemporalAdjusters.previousOrSame(
                                DayOfWeek.MONDAY
                        )
                );

        return buscarPeriodo(
                inicio,
                hoje
        );
    }


    private MetricaVendaDTO buscarMensal(
            LocalDate hoje
    ) {

        LocalDate inicio =
                hoje.withDayOfMonth(
                        1
                );

        return buscarPeriodo(
                inicio,
                hoje
        );
    }


    private MetricaVendaDTO buscarAnual(
            LocalDate hoje
    ) {

        LocalDate inicio =
                hoje.withDayOfYear(
                        1
                );

        return buscarPeriodo(
                inicio,
                hoje
        );
    }


    private MetricaVendaDTO buscarPeriodo(
            LocalDate inicio,
            LocalDate fim
    ) {

        ResumoVendaProjection resumo =
                consultarResumo(
                        inicio,
                        fim
                );

        return montarMetrica(
                inicio,
                fim,
                resumo
        );
    }


    private ResumoVendaProjection consultarResumo(
            LocalDate inicio,
            LocalDate fim
    ) {

        return vendaRepository
                .resumirPeriodo(
                        inicioPeriodo(inicio),
                        fimPeriodo(fim),
                        StatusVenda.FINALIZADA,
                        StatusVenda.CANCELADA
                );
    }


    private MetricaVendaDTO montarMetrica(
            LocalDate inicio,
            LocalDate fim,
            ResumoVendaProjection resumo
    ) {

        return new MetricaVendaDTO(
                inicio,
                fim,
                valorLong(
                        resumo.getQuantidade()
                ),
                valorDecimal(
                        resumo.getValorTotal()
                ),
                valorLong(
                        resumo.getQuantidadeCanceladas()
                ),
                valorDecimal(
                        resumo.getValorCancelado()
                )
        );
    }


    private Instant inicioPeriodo(
            LocalDate data
    ) {

        return data
                .atStartOfDay(
                        ZONA
                )
                .toInstant();
    }


    private Instant fimPeriodo(
            LocalDate data
    ) {

        return data
                .plusDays(1)
                .atStartOfDay(
                        ZONA
                )
                .toInstant();
    }


    private long valorLong(
            Long valor
    ) {

        return valor != null
                ? valor
                : 0L;
    }


    private BigDecimal valorDecimal(
            BigDecimal valor
    ) {

        return valor != null
                ? valor
                : BigDecimal.ZERO;
    }



}
