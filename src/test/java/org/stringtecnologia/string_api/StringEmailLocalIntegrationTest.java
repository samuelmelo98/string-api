package org.stringtecnologia.string_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.stringtecnologia.string_api.integration.email.adapter.StringEmailAdapter;
import org.stringtecnologia.string_api.integration.email.client.StringEmailApiClient;
import org.stringtecnologia.string_api.integration.email.config.StringEmailApiProperties;
import org.stringtecnologia.string_api.integration.email.config.StringEmailClientConfig;
import org.stringtecnologia.string_api.integration.email.dto.EmailEnvioResponseDTO;
import org.stringtecnologia.string_api.integration.email.security.StringEmailTokenProvider;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringEmailLocalIntegrationTest {

    @Test
    void deveObterTokenEEnviarEmailPeloServicoReal() throws Exception {

        String secret = requiredEnvForIntegration("STRING_EMAIL_CLIENT_SECRET");
        String destinatario = requiredEnvForIntegration("STRING_EMAIL_TEST_TO");

        String emailUrl = envOrDefault(
                "STRING_EMAIL_URL",
                "http://127.0.0.1:18080"
        );

        String tokenUrl = envOrDefault(
                "STRING_EMAIL_TOKEN_URL",
                "https://auth.stringtecnologiadf.org/realms/stringtecnologia/protocol/openid-connect/token"
        );

        String clientId = envOrDefault(
                "STRING_EMAIL_CLIENT_ID",
                "string-api-email"
        );

        StringEmailApiProperties properties = new StringEmailApiProperties(
                emailUrl,
                tokenUrl,
                clientId,
                secret,
                Duration.ofSeconds(5),
                Duration.ofSeconds(15),
                Duration.ofSeconds(30)
        );

        StringEmailClientConfig config = new StringEmailClientConfig();

        RestClient tokenRestClient =
                config.stringEmailTokenRestClient(properties);

        StringEmailTokenProvider tokenProvider =
                new StringEmailTokenProvider(
                        tokenRestClient,
                        properties
                );

        String token = tokenProvider.getAccessToken();

        validarClaims(
                token,
                clientId
        );

        RestClient emailRestClient =
                config.stringEmailRestClient(
                        properties,
                        tokenProvider
                );

        StringEmailApiClient apiClient =
                config.stringEmailApiClient(
                        emailRestClient
                );

        StringEmailAdapter adapter =
                new StringEmailAdapter(
                        apiClient,
                        tokenProvider
                );

        EmailEnvioResponseDTO response =
                adapter.enviarTexto(
                        List.of(destinatario),
                        "Teste local string-api -> string-emails",
                        "Integração local validada antes da publicação da string-api em produção."
                );

        assertNotNull(response);

        assertEquals(
                "ACEITO_PELO_SMTP",
                response.status()
        );

        assertEquals(
                1,
                response.destinatarios()
        );

        assertNotNull(
                response.requestId()
        );

        assertFalse(
                response.requestId().isBlank()
        );

        System.out.println("STRING-EMAIL TESTE OK");
        System.out.println("status=" + response.status());
        System.out.println("requestId=" + response.requestId());
        System.out.println("destinatarios=" + response.destinatarios());
        System.out.println("anexos=" + response.anexos());
    }

    private void validarClaims(
            String token,
            String expectedClientId
    ) throws Exception {

        String[] parts = token.split("\\.");

        assertEquals(
                3,
                parts.length,
                "JWT inválido"
        );

        byte[] payload = Base64
                .getUrlDecoder()
                .decode(
                        padBase64(parts[1])
                );

        JsonNode claims = new ObjectMapper()
                .readTree(
                        new String(
                                payload,
                                StandardCharsets.UTF_8
                        )
                );

        assertEquals(
                expectedClientId,
                claims.path("azp").asText()
        );

        assertEquals(
                "Bearer",
                claims.path("typ").asText()
        );

        long iat =
                claims.path("iat").asLong();

        long exp =
                claims.path("exp").asLong();

        long lifetime =
                exp - iat;

        assertTrue(
                lifetime > 0 && lifetime <= 180,
                "Lifetime do token deve ser <= 180s, recebido: "
                        + lifetime
                        + "s"
        );

        JsonNode aud =
                claims.path("aud");

        boolean emailApiAudience =
                aud.isTextual()
                        ? "email-api".equals(
                        aud.asText()
                )
                        : aud.isArray()
                        && containsText(
                        aud,
                        "email-api"
                );

        assertTrue(
                emailApiAudience,
                "JWT sem audience email-api"
        );

        JsonNode roles =
                claims
                        .path("resource_access")
                        .path("email-api")
                        .path("roles");

        assertTrue(
                roles.isArray()
                        && containsText(
                        roles,
                        "email.send"
                ),
                "JWT sem role email.send"
        );

        System.out.println(
                "TOKEN OK: azp="
                        + claims.path("azp").asText()
                        + ", lifetime="
                        + lifetime
                        + "s, aud=email-api, role=email.send"
        );
    }

    private boolean containsText(
            JsonNode array,
            String expected
    ) {

        for (JsonNode item : array) {

            if (expected.equals(
                    item.asText()
            )) {

                return true;
            }
        }

        return false;
    }

    private String padBase64(
            String value
    ) {

        int padding =
                (4 - value.length() % 4) % 4;

        return value
                + "=".repeat(padding);
    }

    private String requiredEnvForIntegration(
            String name
    ) {

        String value =
                System.getenv(name);

        Assumptions.assumeTrue(
                value != null
                        && !value.isBlank(),
                "Teste de integração ignorado: variável "
                        + name
                        + " não configurada."
        );

        return value;
    }

    private String envOrDefault(
            String name,
            String defaultValue
    ) {

        String value =
                System.getenv(name);

        return value == null
                || value.isBlank()
                ? defaultValue
                : value;
    }
}