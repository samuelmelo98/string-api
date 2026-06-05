package org.stringtecnologia.string_api.util.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção genérica para erros de negócio da aplicação.
 *
 * <p>Utiliza um {@link CodeError} para identificar o tipo de erro
 * de forma padronizada.</p>
 *
 * <p>Essa abordagem reduz a quantidade de classes de exceção e permite
 * centralizar os códigos de erro em um {@code enum}.</p>
 *
 * <p>As exceções são tratadas pelo {@code ApiExceptionHandler} e convertidas
 * para respostas HTTP seguindo o padrão
 * <b>Problem Details for HTTP APIs</b> (RFC 7807 / RFC 9457).</p>
 *
 * <p>Exemplo de uso:</p>
 *
 * <pre>
 * throw new BusinessException(
 *     CodeError.CNPJ_DUPLICADO,
 *     "Já existe um órgão cadastrado com este CNPJ"
 * );
 * </pre>
 */
public class BusinessException extends DomainException {

    /**
     * Construtor padrão para erros de negócio (HTTP 400).
     */
    public BusinessException(CodeError errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Construtor para casos onde o status HTTP precisa ser customizado.
     */
    public BusinessException(CodeError errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }
}