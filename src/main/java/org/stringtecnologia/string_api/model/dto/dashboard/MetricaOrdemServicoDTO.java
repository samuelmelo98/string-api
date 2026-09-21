package org.stringtecnologia.string_api.model.dto.dashboard;


import java.time.LocalDate;

public record MetricaOrdemServicoDTO(

        LocalDate inicio,
        LocalDate fim,

        long total,

        long abertas,
        long autorizadas,
        long entregues,
        long naoAutorizadas,

        long outros

) {
}
