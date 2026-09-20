package org.stringtecnologia.string_api.model.dto.ordemServico;


import org.stringtecnologia.string_api.model.enums.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemServicoOrcamentoResponseDTO(

        Long ordemServicoOrcamentoId,

        Long ordemServicoId,

        Integer versao,

        String servicoProposto,

        BigDecimal valorMaoObra,

        BigDecimal valorPecas,

        BigDecimal desconto,

        BigDecimal valorTotal,

        String observacao,

        StatusOrcamento status,

        LocalDateTime dataCriacao,

        LocalDateTime dataAtualizacao,

        LocalDateTime dataEnvio,

        LocalDateTime dataAprovacao,

        LocalDateTime dataReprovacao

) {
}
