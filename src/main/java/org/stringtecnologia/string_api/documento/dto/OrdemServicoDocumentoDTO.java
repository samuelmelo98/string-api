package org.stringtecnologia.string_api.documento.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrdemServicoDocumentoDTO(
        Long numeroOs,
        String clienteNome,
        String clienteDocumento,
        String descricaoServico,
        String equipamento,
        BigDecimal valorServico,
        LocalDate dataEmissao,
        LocalDate dataEmissao2
)
{}
