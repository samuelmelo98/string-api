package org.stringtecnologia.string_api.util.exceptions.hermes;

import org.stringtecnologia.string_api.util.exceptions.CodeError;
import org.stringtecnologia.string_api.util.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class HermesException extends DomainException {

    private HermesException(
            CodeError errorCode,
            String message,
            HttpStatus status
    ) {

        super(
                errorCode,
                message,
                status
        );
    }

    public static HermesException integracao() {

        return new HermesException(
                CodeError.ERRO_INTEGRACAO_HERMES,
                "Erro ao integrar com API Hermes.",
                HttpStatus.BAD_GATEWAY
        );
    }

    public static HermesException timeout() {

        return new HermesException(
                CodeError.TIMEOUT_HERMES,
                "Timeout ao acessar API Hermes.",
                HttpStatus.GATEWAY_TIMEOUT
        );
    }

    public static HermesException tokenInvalido() {

        return new HermesException(
                CodeError.TOKEN_HERMES_INVALIDO,
                "Token OAuth2 Hermes inválido.",
                HttpStatus.UNAUTHORIZED
        );
    }

    public static HermesException uploadExcedido() {

        return new HermesException(
                CodeError.UPLOAD_HERMES_EXCEDIDO,
                "Arquivo excede limite permitido pela API Hermes.",
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    public static HermesException destinatarioInvalido(
            String email
    ) {

        return new HermesException(
                CodeError.EMAIL_HERMES_INVALIDO,
                "Destinatário inválido: " + email,
                HttpStatus.BAD_REQUEST
        );
    }
}
