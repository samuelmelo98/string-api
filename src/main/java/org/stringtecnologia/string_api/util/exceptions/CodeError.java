package org.stringtecnologia.string_api.util.exceptions;

/**
 * Enumeração de códigos de erro de negócio da aplicação.
 *
 * <p>Os códigos são utilizados para padronizar erros retornados pela API
 * e facilitar o tratamento no frontend.</p>
 *
 * <p>Utilizado em conjunto com {@link BusinessException} e convertido
 * em respostas HTTP pelo {@code ApiExceptionHandler} seguindo o padrão
 * <b>Problem Details for HTTP APIs</b> (RFC 7807 / RFC 9457).</p>
 *
 * <p>Exemplo de uso:</p>
 *
 * <pre>
 * throw new BusinessException(
 *     CodeError.ORGAO_NAO_ENCONTRADO,
 *     "Órgão não encontrado"
 * );
 * </pre>
 */
public enum CodeError {


        // 🔹 GERAL
        DATA_INTEGRITY,
        ACESSO_NEGADO,
        ID_NAO_ENCONTRADO,

        // 🔹 DOMÍNIO
        CNPJ_DUPLICADO,
        NOME_DUPLICADO,
        DESCRICAO_INVALIDA,
        DOMINIO_NAO_ENCONTRADO,

        // 🔹 WORKFLOW
        STATUS_TRANSICAO_INVALIDA,

        // 🔹 ARQUIVO (infra)
        ARQUIVO_TIPO_NAO_PERMITIDO,
        ARQUIVO_TAMANHO_EXCEDIDO,
        ARQUIVO_VAZIO,
        ARQUIVO_EXTENSAO_INVALIDA,

        // 🔹 DOCUMENTO (negócio)
        DOCUMENTO_OBRIGATORIO,
        DOCUMENTO_NAO_ENCONTRADO,
        DOCUMENTO_DUPLICADO,
        DOCUMENTO_TIPO_INVALIDO,
        DOCUMENTO_JA_EXISTE_PARA_TIPO,
        DOCUMENTO_NAO_PERMITIDO_PARA_STATUS,
        DOCUMENTO_LIMITE_EXCEDIDO,

        //API EXTERNA

        USUARIO_NAO_ENCONTRADO,
        ERRO_INTEGRACAO,

        // 🔹 HERMES
        ERRO_INTEGRACAO_HERMES,
        TIMEOUT_HERMES,
        TOKEN_HERMES_INVALIDO,
        UPLOAD_HERMES_EXCEDIDO,
        EMAIL_HERMES_INVALIDO,





}