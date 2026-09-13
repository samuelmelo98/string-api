package org.stringtecnologia.string_api.resources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.integration.email.adapter.StringEmailAdapter;
import org.stringtecnologia.string_api.integration.email.dto.EmailEnvioResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/test/email")
@ConditionalOnProperty(
        name = "app.email-test.enabled",
        havingValue = "true"
)
public class EmailTestController {

    private final StringEmailAdapter stringEmailAdapter;
    private final String destinatario;

    public EmailTestController(
            StringEmailAdapter stringEmailAdapter,
            @Value("${app.email-test.to:}") String destinatario) {

        this.stringEmailAdapter = stringEmailAdapter;
        this.destinatario = destinatario;
    }

    @PostMapping
    public EmailEnvioResponseDTO testar(Authentication authentication) {

        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalStateException(
                    "APP_EMAIL_TEST_TO nao foi configurado."
            );
        }

        String usuario = authentication != null
                ? authentication.getName()
                : "usuario-nao-identificado";

        return stringEmailAdapter.enviarTexto(
                List.of(destinatario),
                "Teste string-api -> string-emails",
                """
                Teste de integracao de e-mail em producao.

                Origem: string-api
                Servico: string-emails
                Autenticacao M2M: Keycloak
                Usuario autenticado: %s
                """.formatted(usuario)
        );
    }
}