package org.stringtecnologia.string_api.integration.infosimples.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record InfosimplesCpfPageResponse(
        int code,
        @JsonProperty("code_message") String codeMessage,
        @JsonProperty("data_count") int dataCount,
        List<String> errors,
        List<CpfResultResponseDTO> data
) {}
