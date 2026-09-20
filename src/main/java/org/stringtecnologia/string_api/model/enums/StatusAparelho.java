package org.stringtecnologia.string_api.model.enums;

public enum StatusAparelho {

    PARA_ORCAMENTO,
    EM_MANUTENCAO,
    AGUARDANDO_PECA,
    PRONTO,
    ENTREGUE;

    public String getCodigo() {
        return name();
    }
}
