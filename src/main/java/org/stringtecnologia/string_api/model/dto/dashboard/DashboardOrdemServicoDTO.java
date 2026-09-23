package org.stringtecnologia.string_api.model.dto.dashboard;

public record DashboardOrdemServicoDTO(
        MetricaOrdemServicoDTO semana,
        MetricaOrdemServicoDTO entreguesSeisDias,
        MetricaOrdemServicoDTO trintaDias,
        MetricaOrdemServicoDTO ano
) {
}