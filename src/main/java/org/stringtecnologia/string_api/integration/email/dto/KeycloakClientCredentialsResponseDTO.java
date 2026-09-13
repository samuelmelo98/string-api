package org.stringtecnologia.string_api.integration.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakClientCredentialsResponseDTO(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("token_type") String tokenType
) {
}
