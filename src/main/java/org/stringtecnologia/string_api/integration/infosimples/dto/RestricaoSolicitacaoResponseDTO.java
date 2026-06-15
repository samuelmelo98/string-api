package org.stringtecnologia.string_api.integration.infosimples.dto;


import org.stringtecnologia.string_api.model.entities.Cliente;

import java.time.LocalDate;

public record RestricaoSolicitacaoResponseDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String matricula,
        Boolean ativo,
        boolean checked
) {

    public RestricaoSolicitacaoResponseDTO(Cliente cliente) {
        this(
                cliente.getClienteId(),
                cliente.getNome(),
                cliente.getDataNascimento(),
                cliente.getCpf(),
                true,
                false
        );
    }
}