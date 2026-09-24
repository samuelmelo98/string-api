package org.stringtecnologia.string_api.model.dto.dashboard;

import java.math.BigDecimal;

public record MetricaEntregasTecnicoDTO(
        Long tecnicoId,
        String tecnicoNome,
        long quantidadeEntregues,
        BigDecimal valorTotal,
        BigDecimal valorMaterial
) {
}