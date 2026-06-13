package org.stringtecnologia.string_api.util;

public enum CategoriaDominio {

    STATUS_ATIVO,
    STATUS_PEDIDO,
    STATUS_EXERCICIO,
    STATUS_PROCESSO,
    STATUS_ESFERA_ADMINISTRATIVO,
    STATUS_PODER_ADMINISTRATIVO,
    STATUS_NATUREZA_JURIDICA,
    STATUS_ADIANTAMENTO,
    TIPO_DOCUMENTO,

    STATUS_APARELHO;


    public String getCodigo() {
        return name();
    }
}
