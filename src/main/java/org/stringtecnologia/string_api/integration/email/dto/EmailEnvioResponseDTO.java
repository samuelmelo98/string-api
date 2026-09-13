package org.stringtecnologia.string_api.integration.email.dto;

public record EmailEnvioResponseDTO(
        String status,
        int destinatarios,
        int anexos,
        String requestId
) {
}
