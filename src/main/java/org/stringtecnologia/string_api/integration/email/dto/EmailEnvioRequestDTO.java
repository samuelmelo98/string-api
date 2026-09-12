package org.stringtecnologia.string_api.integration.email.dto;

import java.util.List;

public record EmailEnvioRequestDTO(
        List<String> destinatarios,
        String assunto,
        String mensagem,
        boolean html
) {
    public EmailEnvioRequestDTO {
        destinatarios = destinatarios == null ? List.of() : List.copyOf(destinatarios);
    }
}
