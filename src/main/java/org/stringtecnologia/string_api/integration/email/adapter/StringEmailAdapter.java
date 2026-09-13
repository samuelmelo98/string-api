package org.stringtecnologia.string_api.integration.email.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.stringtecnologia.string_api.integration.email.client.StringEmailApiClient;
import org.stringtecnologia.string_api.integration.email.dto.EmailEnvioRequestDTO;
import org.stringtecnologia.string_api.integration.email.dto.EmailEnvioResponseDTO;
import org.stringtecnologia.string_api.integration.email.security.StringEmailTokenProvider;
import org.stringtecnologia.string_api.util.exceptions.email.StringEmailException;

import java.net.SocketTimeoutException;
import java.util.List;

@Component
public class StringEmailAdapter {

    private static final Logger log = LoggerFactory.getLogger(StringEmailAdapter.class);

    private final StringEmailApiClient client;
    private final StringEmailTokenProvider tokenProvider;

    public StringEmailAdapter(
            StringEmailApiClient client,
            StringEmailTokenProvider tokenProvider
    ) {
        this.client = client;
        this.tokenProvider = tokenProvider;
    }

    public EmailEnvioResponseDTO enviar(
            List<String> destinatarios,
            String assunto,
            String mensagem,
            boolean html
    ) {
        return enviar(new EmailEnvioRequestDTO(destinatarios, assunto, mensagem, html));
    }

    public EmailEnvioResponseDTO enviarTexto(
            List<String> destinatarios,
            String assunto,
            String mensagem
    ) {
        return enviar(destinatarios, assunto, mensagem, false);
    }

    public EmailEnvioResponseDTO enviarHtml(
            List<String> destinatarios,
            String assunto,
            String mensagemHtml
    ) {
        return enviar(destinatarios, assunto, mensagemHtml, true);
    }

    public EmailEnvioResponseDTO enviar(EmailEnvioRequestDTO request) {
        validarRequest(request);

        try {
            EmailEnvioResponseDTO response = client.enviar(request);

            if (response == null) {
                throw StringEmailException.integracao();
            }

            log.info(
                    "E-mail aceito pelo string-emails. status={}, requestId={}, destinatarios={}, anexos={}",
                    response.status(),
                    response.requestId(),
                    response.destinatarios(),
                    response.anexos()
            );

            return response;
        } catch (RestClientResponseException ex) {
            HttpStatusCode status = ex.getStatusCode();

            if (status.value() == 401) {
                tokenProvider.invalidate();
                throw StringEmailException.autenticacao(ex);
            }

            if (status.value() == 403) {
                throw StringEmailException.autorizacao(ex);
            }

            if (status.value() == 429) {
                throw StringEmailException.limite(ex);
            }

            if (status.is4xxClientError()) {
                throw StringEmailException.requisicaoInvalida(ex);
            }

            throw StringEmailException.integracao(ex);
        } catch (ResourceAccessException ex) {
            if (isTimeout(ex)) {
                throw StringEmailException.timeout(ex);
            }
            throw StringEmailException.indisponivel(ex);
        }
    }

    private void validarRequest(EmailEnvioRequestDTO request) {
        if (request == null
                || request.destinatarios() == null
                || request.destinatarios().isEmpty()
                || request.assunto() == null
                || request.assunto().isBlank()
                || request.mensagem() == null
                || request.mensagem().isBlank()) {
            throw StringEmailException.requisicaoInvalida();
        }
    }

    private boolean isTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException
                    || current instanceof java.net.http.HttpTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
