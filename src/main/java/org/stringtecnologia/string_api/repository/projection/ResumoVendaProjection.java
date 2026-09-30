package org.stringtecnologia.string_api.repository.projection;


import java.math.BigDecimal;

public interface ResumoVendaProjection {

    Long getQuantidade();

    BigDecimal getValorTotal();

    Long getQuantidadeCanceladas();

    BigDecimal getValorCancelado();

}
