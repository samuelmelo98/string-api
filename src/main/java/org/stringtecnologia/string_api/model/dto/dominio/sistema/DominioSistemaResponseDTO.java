package org.stringtecnologia.string_api.model.dto.dominio.sistema;

import java.time.LocalDateTime;

public record DominioSistemaResponseDTO(
        Long dominioSistemaId,
        String categoria,
        String codigo,
        String descricao,
        LocalDateTime dataCadastro,
        Boolean ativo
) {
}
