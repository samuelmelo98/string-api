package org.stringtecnologia.string_api.util;

public enum StatusAparelho implements DominioEnum{
    PRONTO,
    NAO_AUTORIZADO,

    DEVOLVIDO,

    AUTORIZADO,

    PARA_ORCAMENTO
    ;


    @Override
    public CategoriaDominio getCategoria() {
        return CategoriaDominio.STATUS_APARELHO;
    }
}
