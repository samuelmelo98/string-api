package org.stringtecnologia.string_api.model.dto.ordemServico;

import java.math.BigDecimal;
import java.time.Instant;

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

        Instant dataAbertura,
        Instant dataAprovacao,
        Instant dataInicioServico,
        Instant dataConclusao,
        Instant dataEntrega

) {
}
