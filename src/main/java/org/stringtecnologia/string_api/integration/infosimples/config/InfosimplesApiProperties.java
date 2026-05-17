package org.stringtecnologia.string_api.integration.infosimples.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;


@ConfigurationProperties(prefix = "api.infosimples")
public record InfosimplesApiProperties(
        String apiBaseUrl,
        String apiKey,
        Duration timeOut
) {
}