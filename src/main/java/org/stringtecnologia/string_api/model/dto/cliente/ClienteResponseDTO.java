package org.stringtecnologia.string_api.model.dto.cliente;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpfCnpj,
        String email,
        String telefone
) {
}
