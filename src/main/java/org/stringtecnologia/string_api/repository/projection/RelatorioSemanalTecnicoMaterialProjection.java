package org.stringtecnologia.string_api.repository.projection;


import java.math.BigDecimal;

public interface RelatorioSemanalTecnicoMaterialProjection {

    Long getOrdemServicoId();

    BigDecimal getValorMaterial();
}
