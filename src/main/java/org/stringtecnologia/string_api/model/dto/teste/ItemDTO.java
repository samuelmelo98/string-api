package org.stringtecnologia.string_api.model.dto.teste;

import java.math.BigDecimal;

public record ItemDTO(
        Long id,
        String descricao,
        Integer quantidade,
        BigDecimal valor
) {
}