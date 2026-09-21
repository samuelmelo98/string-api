package org.stringtecnologia.string_api.model.dto.dashboard;


public record DashboardOrdemServicoDTO(

        MetricaOrdemServicoDTO semana,

        MetricaOrdemServicoDTO trintaDias,

        MetricaOrdemServicoDTO ano

) {
}
