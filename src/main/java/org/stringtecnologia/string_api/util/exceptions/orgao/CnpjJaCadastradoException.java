package org.stringtecnologia.string_api.util.exceptions.orgao;

import org.stringtecnologia.string_api.util.exceptions.CodeError;
import org.stringtecnologia.string_api.util.exceptions.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando um CNPJ já está cadastrado para um órgão.
 *
 * <p>Representa uma violação de regra de negócio no domínio de Órgão.</p>
 *
 * <p>Essa exceção é tratada pelo {@code ApiExceptionHandler} e convertida
 * para uma resposta HTTP seguindo o padrão
 * <b>Problem Details for HTTP APIs (RFC 7807 / RFC 9457)</b>.</p>
 *
 * <p>Exemplo de uso:</p>
 *
 * <pre>
 * if (orgaoRepository.existsByCnpj(cnpj)) {
 *     throw new CnpjJaCadastradoException(cnpj);
 * }
 * </pre>
 *
 * <p>Resposta esperada da API:</p>
 *
 * <pre>
 * {
 *   "title": "Erro de negócio",
 *   "detail": "Já existe um órgão cadastrado com o CNPJ: 11.111.111/1111-11",
 *   "status": 409,
 *   "errorCode": "CNPJ_DUPLICADO"
 * }
 * </pre>
 */
public class CnpjJaCadastradoException extends DomainException {

    public CnpjJaCadastradoException(String cnpj) {
        super(
                CodeError.CNPJ_DUPLICADO,
                "Já existe um órgão cadastrado com o CNPJ: " + cnpj,
                HttpStatus.CONFLICT
        );
    }
}