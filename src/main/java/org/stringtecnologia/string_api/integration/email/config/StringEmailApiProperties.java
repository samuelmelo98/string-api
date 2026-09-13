package org.stringtecnologia.string_api.integration.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "api.string-email")
public record StringEmailApiProperties(
        String apiBaseUrl,
        String tokenUrl,
        String clientId,
        String clientSecret,
        Duration connectTimeout,
        Duration readTimeout,
        Duration tokenRefreshSkew
) {
}
