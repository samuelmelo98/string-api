package org.stringtecnologia.string_api.model.enums;

public enum StatusOrdemServico {

    ABERTA,
    EM_ANALISE,
    AGUARDANDO_APROVACAO,
    APROVADA,
    REPROVADA,
    EM_EXECUCAO,
    CONCLUIDA,
    ENTREGUE,
    CANCELADA;

    public String getCodigo() {
        return name();
    }
}