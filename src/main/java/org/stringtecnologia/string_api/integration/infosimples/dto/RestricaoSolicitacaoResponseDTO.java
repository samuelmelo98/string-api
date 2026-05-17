package org.stringtecnologia.string_api.integration.infosimples.dto;


public record RestricaoSolicitacaoResponseDTO(
        Long id,
        String nome,
        String matricula, // No caso da Infosimples, aqui será injetado o CPF do retorno
        Boolean ativo,
        boolean checked
) {}
