package org.stringtecnologia.string_api.model.dto.ordemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaOrdemServicoResponseDTO(

        String numero,

        String clienteNome,

        String aparelhoMarca,
        String aparelhoModelo,
        String aparelhoModeloComercial,
        String aparelhoNumeroSerie,

        String statusCodigo,
        String statusDescricao,

        String defeitoRelatado,
        String diagnostico,
        String solucao,
        String observacao,

        BigDecimal valorOrcamento,
        BigDecimal valorFinal,

        LocalDateTime dataAbertura,
        LocalDateTime dataAprovacao,
        LocalDateTime dataInicioServico,
        LocalDateTime dataConclusao,
        LocalDateTime dataEntrega

) {
}
