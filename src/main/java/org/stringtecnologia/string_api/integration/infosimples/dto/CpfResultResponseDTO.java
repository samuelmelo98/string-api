package org.stringtecnologia.string_api.integration.infosimples.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CpfResultResponseDTO(
        String cpf,
        String nome,
        @JsonProperty("data_nascimento") String dataNascimento,
        @JsonProperty("situacao_cadastral") String situacaoCadastral
) {}