package org.stringtecnologia.string_api.repository.projection;


import org.stringtecnologia.string_api.model.enums.FormaPagamento;

import java.math.BigDecimal;

public interface RelatorioSemanalTecnicoPagamentoProjection {

    Long getOrdemServicoId();

    FormaPagamento getFormaPagamento();

    BigDecimal getValor();

    Integer getParcelas();
}