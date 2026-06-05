package org.stringtecnologia.string_api.util.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Base class para exceções de domínio da aplicação.
 *
 * <p>Exceções de domínio representam violações de regras de negócio
 * e são lançadas na camada de serviço (Service Layer).</p>
 *
 * <p>Essas exceções são tratadas globalmente pelo {@code ApiExceptionHandler}
 * e convertidas para respostas HTTP seguindo o padrão
 * <b>Problem Details for HTTP APIs</b>.</p>
 *
 * <p><b>Especificação:</b></p>
 * <ul>
 *     <li>RFC 7807 — Problem Details for HTTP APIs</li>
 *     <li>RFC 9457 — Atualização da RFC 7807</li>
 * </ul>
 *
 * <p>Campos importantes:</p>
 * <ul>
 *     <li>message → descrição do erro</li>
 *     <li>errorCode → código interno da aplicação</li>
 * </ul>
 *
 * <p>Exemplo de uso:</p>
 *
 * <pre>
 * throw new CnpjJaCadastradoException(cnpj);
 * </pre>
 *
 * @author SICAD
 */
public abstract class DomainException extends RuntimeException {

    private final CodeError errorCode;
    private final HttpStatus status;

    protected DomainException(CodeError errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public CodeError getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}