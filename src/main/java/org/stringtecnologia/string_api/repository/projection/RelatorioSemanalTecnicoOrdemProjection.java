package org.stringtecnologia.string_api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RelatorioSemanalTecnicoOrdemProjection {

    Long getOrdemServicoId();

    String getNumeroOrdemServico();

    Long getTecnicoId();

    String getTecnicoNome();

    String getTecnicoEmail();

    Long getClienteId();

    String getClienteNome();

    Long getAparelhoId();

    String getModelo();

    String getModeloComercial();

    String getNumeroSerie();

    BigDecimal getValorFinal();

    BigDecimal getValorOrcamento();

    LocalDateTime getDataEntrega();
}
