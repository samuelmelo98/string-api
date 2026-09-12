package org.stringtecnologia.string_api.util.exceptions.email;

import org.springframework.http.HttpStatus;
import org.stringtecnologia.string_api.util.exceptions.CodeError;
import org.stringtecnologia.string_api.util.exceptions.DomainException;

public class StringEmailException extends DomainException {

    private StringEmailException(
            CodeError errorCode,
            String message,
            HttpStatus status,
            Throwable cause
    ) {
        super(errorCode, message, status);
        if (cause != null) {
            initCause(cause);
        }
    }

    public static StringEmailException configuracao() {
        return new StringEmailException(
                CodeError.STRING_EMAIL_CONFIGURACAO_INVALIDA,
                "Configuração da integração com o serviço de e-mail está incompleta.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                null
        );
    }

    public static StringEmailException autenticacao() {
        return autenticacao(null);
    }

    public static StringEmailException autenticacao(Throwable cause) {
        return new StringEmailException(
                CodeError.STRING_EMAIL_AUTENTICACAO,
                "Falha ao autenticar a string-api no serviço de e-mail.",
                HttpStatus.BAD_GATEWAY,
                cause
        );
    }

    public static StringEmailException autorizacao(Throwable cause) {
        return new StringEmailException(
                CodeError.STRING_EMAIL_AUTORIZACAO,
                "A string-api não está autorizada a utilizar o serviço de e-mail.",
                HttpStatus.BAD_GATEWAY,
                cause
        );
    }

    public static StringEmailException requisicaoInvalida() {
        return requisicaoInvalida(null);
    }

    public static StringEmailException requisicaoInvalida(Throwable cause) {
        return new StringEmailException(
                CodeError.STRING_EMAIL_REQUISICAO_INVALIDA,
                "Dados para envio de e-mail são inválidos.",
                HttpStatus.BAD_REQUEST,
                cause
        );
    }

    public static StringEmailException limite(Throwable cause) {
        return new StringEmailException(
                CodeError.STRING_EMAIL_LIMITE_EXCEDIDO,
                "Limite temporário de envio de e-mails atingido.",
                HttpStatus.TOO_MANY_REQUESTS,
                cause
        );
    }

    public static StringEmailException timeout(Throwable cause) {
        return new StringEmailException(
                CodeError.STRING_EMAIL_TIMEOUT,
                "Timeout ao acessar o serviço de e-mail.",
                HttpStatus.GATEWAY_TIMEOUT,
                cause
        );
    }

    public static StringEmailException indisponivel(Throwable cause) {
        return new StringEmailException(
                CodeError.STRING_EMAIL_INDISPONIVEL,
                "Serviço de e-mail indisponível.",
                HttpStatus.SERVICE_UNAVAILABLE,
                cause
        );
    }

    public static StringEmailException integracao() {
        return integracao(null);
    }

    public static StringEmailException integracao(Throwable cause) {
        return new StringEmailException(
                CodeError.STRING_EMAIL_INTEGRACAO,
                "Erro ao integrar com o serviço de e-mail.",
                HttpStatus.BAD_GATEWAY,
                cause
        );
    }
}
