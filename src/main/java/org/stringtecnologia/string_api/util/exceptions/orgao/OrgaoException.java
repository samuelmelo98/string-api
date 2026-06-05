package org.stringtecnologia.string_api.util.exceptions.orgao;

import org.stringtecnologia.string_api.util.exceptions.CodeError;
import org.stringtecnologia.string_api.util.exceptions.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Exceção genérica para erros de negócio relacionados ao domínio de Órgão.
 *
 * <p>Utiliza o padrão de <b>Factory Methods</b> para criar exceções
 * específicas de forma centralizada.</p>
 *
 * <p>As exceções lançadas são tratadas pelo {@code ApiExceptionHandler}
 * e convertidas para respostas HTTP seguindo o padrão
 * <b>Problem Details for HTTP APIs</b> (RFC 7807 / RFC 9457).</p>
 *
 * <p>Exemplo de uso:</p>
 *
 * <pre>
 * if (orgaoRepository.existsByCnpj(cnpj)) {
 *     throw OrgaoException.cnpjDuplicado(cnpj);
 * }
 * </pre>
 */
public class OrgaoException extends DomainException {

    private OrgaoException(CodeError errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }

//    public static OrgaoException cnpjDuplicado(String cnpj) {
//        return new OrgaoException(
//                CodeError.CNPJ_DUPLICADO,
//                "Já existe um órgão cadastrado com o CNPJ: " + cnpj
//        );
//    }

    public static OrgaoException nomeDuplicado(String nome) {
        return new OrgaoException(
                CodeError.NOME_DUPLICADO,
                "Já existe um órgão cadastrado com o nome: " + nome
        );
    }
}