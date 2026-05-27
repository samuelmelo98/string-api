package org.stringtecnologia.string_api.integration.infosimples.dto;


import org.stringtecnologia.string_api.model.entities.Cliente;

public record RestricaoSolicitacaoResponseDTO(
        Long id,
        String nome,
        String matricula,
        Boolean ativo,
        boolean checked
) {

    public RestricaoSolicitacaoResponseDTO(Cliente cliente) {
        this(
                cliente.getClienteId(),
                cliente.getNome(),
                cliente.getCpf(),
                true,
                false
        );
    }
}