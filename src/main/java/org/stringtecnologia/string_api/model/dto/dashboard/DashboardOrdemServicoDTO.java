package org.stringtecnologia.string_api.model.dto.dashboard;

import java.util.List;

public record DashboardOrdemServicoDTO(
        MetricaOrdemServicoDTO semana,
        MetricaOrdemServicoDTO entreguesSeisDias,
        MetricaOrdemServicoDTO trintaDias,
        MetricaOrdemServicoDTO ano,
        List<MetricaEntregasTecnicoDTO> entreguesPorTecnico
) {
}