package org.stringtecnologia.string_api.model.dto.ordemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemServicoResponseDTO(

        Long ordemServicoId,

        String numero,

        Long clienteId,

        String clienteNome,

        Long aparelhoId,

        String marca,

        String modelo,

        String modeloComercial,

        String numeroSerie,

        String statusCodigo,

        String statusDescricao,

        String defeitoRelatado,

        String diagnostico,

        String solucao,

        String observacao,

        BigDecimal valorOrcamento,

        BigDecimal valorFinal,

        LocalDateTime dataAbertura,

        LocalDateTime dataAtualizacao,

        LocalDateTime dataAprovacao,

        LocalDateTime dataInicioServico,

        LocalDateTime dataConclusao,

        LocalDateTime dataEntrega

) {
}