package org.stringtecnologia.string_api.integration.email.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.stringtecnologia.string_api.integration.email.config.StringEmailApiProperties;
import org.stringtecnologia.string_api.integration.email.dto.KeycloakClientCredentialsResponseDTO;
import org.stringtecnologia.string_api.util.exceptions.email.StringEmailException;

import java.time.Duration;
import java.time.Instant;

@Component
public class StringEmailTokenProvider {

    private final RestClient tokenRestClient;
    private final StringEmailApiProperties properties;
    private final Object refreshLock = new Object();

    private volatile CachedToken cachedToken;

    public StringEmailTokenProvider(
            @Qualifier("stringEmailTokenRestClient") RestClient tokenRestClient,
            StringEmailApiProperties properties
    ) {
        this.tokenRestClient = tokenRestClient;
        this.properties = properties;
    }

    public String getAccessToken() {
        CachedToken current = cachedToken;
        Instant now = Instant.now();

        if (current != null && now.isBefore(current.refreshAt())) {
            return current.accessToken();
        }

        synchronized (refreshLock) {
            current = cachedToken;
            now = Instant.now();

            if (current != null && now.isBefore(current.refreshAt())) {
                return current.accessToken();
            }

            return renovarToken(now);
        }
    }

    public void invalidate() {
        cachedToken = null;
    }

    private String renovarToken(Instant now) {
        validarConfiguracao();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());

        KeycloakClientCredentialsResponseDTO response;
        try {
            response = tokenRestClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(KeycloakClientCredentialsResponseDTO.class);
        } catch (Exception ex) {
            throw StringEmailException.autenticacao(ex);
        }

        if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
            throw StringEmailException.autenticacao();
        }

        if (response.tokenType() != null && !response.tokenType().equalsIgnoreCase("Bearer")) {
            throw StringEmailException.autenticacao();
        }

        long expiresIn = response.expiresIn() > 0 ? response.expiresIn() : 60;
        Duration skew = properties.tokenRefreshSkew() != null
                ? properties.tokenRefreshSkew()
                : Duration.ofSeconds(30);

        long refreshInSeconds = expiresIn - Math.max(0, skew.toSeconds());
        if (refreshInSeconds <= 0) {
            refreshInSeconds = Math.max(1, expiresIn / 2);
        }

        CachedToken newToken = new CachedToken(
                response.accessToken(),
                now.plusSeconds(refreshInSeconds)
        );

        cachedToken = newToken;
        return newToken.accessToken();
    }

    private void validarConfiguracao() {
        if (properties.tokenUrl() == null || properties.tokenUrl().isBlank()
                || properties.clientId() == null || properties.clientId().isBlank()
                || properties.clientSecret() == null || properties.clientSecret().isBlank()) {
            throw StringEmailException.configuracao();
        }
    }

    private record CachedToken(String accessToken, Instant refreshAt) {
    }
}
