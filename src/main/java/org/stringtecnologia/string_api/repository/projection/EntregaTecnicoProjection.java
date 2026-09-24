package org.stringtecnologia.string_api.repository.projection;

import java.math.BigDecimal;

public interface EntregaTecnicoProjection {

    Long getTecnicoId();

    String getTecnicoNome();

    Long getQuantidadeEntregues();

    BigDecimal getValorTotal();
}