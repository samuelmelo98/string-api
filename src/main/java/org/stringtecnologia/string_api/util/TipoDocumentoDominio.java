package org.stringtecnologia.string_api.util;

public enum TipoDocumentoDominio implements DominioEnum {
    TERMO_ADIANTAMENTO_PARA_ASSINAR,
    TERMO_ADIANTAMENTO_ASSINADO,
    ORDEM_SERVICO,
    ANEXO;


    @Override
    public CategoriaDominio getCategoria() {
        return CategoriaDominio.TIPO_DOCUMENTO;

    }

    @Override
    public String getCodigo() {
        return this.name();
    }
}
