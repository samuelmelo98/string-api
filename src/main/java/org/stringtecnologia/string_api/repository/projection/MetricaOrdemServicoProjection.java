package org.stringtecnologia.string_api.repository.projection;

public interface MetricaOrdemServicoProjection {

    Long getTotal();

    Long getAbertas();

    Long getAutorizadas();

    Long getEntregues();

    Long getNaoAutorizadas();
}