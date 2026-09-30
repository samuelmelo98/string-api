package org.stringtecnologia.string_api.model.dto.venda;

public record DashboardVendaDTO(
        MetricaVendaDTO diaria,
        MetricaVendaDTO semanal,
        MetricaVendaDTO mensal,
        MetricaVendaDTO anual
) {
}