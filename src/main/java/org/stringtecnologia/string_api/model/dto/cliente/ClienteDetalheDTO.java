package org.stringtecnologia.string_api.model.dto.cliente;

import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;

import java.util.List;

public record ClienteDetalheDTO(
        Long clienteId,
        String nome,
        String cpf,
        String email,
        String telefone,
        String endereco,
        String cidade,
        String estado,
        String cep,
        List<AparelhoResponseDTO> aparelhos
) {
}