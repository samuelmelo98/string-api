package org.stringtecnologia.string_api.model.dto.ordemServico;

import java.math.BigDecimal;
import java.time.Instant;

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

        Long tecnicoResponsavelId,

        String tecnicoResponsavelNome,

        Instant dataAtribuicaoTecnico,

        Instant dataAbertura,

        Instant dataAtualizacao,

        Instant dataAprovacao,

        Instant dataInicioServico,

        Instant dataConclusao,

        Instant dataEntrega

) {
}