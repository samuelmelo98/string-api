package org.stringtecnologia.string_api.repository.projection;

import java.time.LocalDateTime;

public interface OrdemServicoAbertaProjection {

    Long getOrdemServicoId();

    String getNumero();

    String getStatusCodigo();

    String getStatusDescricao();

    LocalDateTime getDataAbertura();


    Long getClienteId();

    String getClienteNome();

    String getClienteCpf();

    String getClienteTelefone();


    Long getAparelhoId();

    String getMarca();

    String getModelo();

    String getModeloComercial();

    String getNumeroSerie();
}
