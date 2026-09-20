package org.stringtecnologia.string_api.util;

public enum StatusAparelho implements DominioEnum {

    PARA_ORCAMENTO,

    AUTORIZADO,

    EM_MANUTENCAO,

    AGUARDANDO_PECA,

    PRONTO,

    NAO_AUTORIZADO,

    DEVOLVIDO,

    ENTREGUE;

    @Override
    public CategoriaDominio getCategoria() {
        return CategoriaDominio.STATUS_APARELHO;
    }
}