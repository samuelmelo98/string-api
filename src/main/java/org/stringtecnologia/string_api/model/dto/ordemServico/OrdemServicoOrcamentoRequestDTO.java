package org.stringtecnologia.string_api.model.dto.ordemServico;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record OrdemServicoOrcamentoRequestDTO(

        @NotBlank(
                message = "O diagnóstico é obrigatório."
        )
        String diagnostico,

        @NotBlank(
                message = "O serviço proposto é obrigatório."
        )
        String servicoProposto,

        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "O valor da mão de obra não pode ser negativo."
        )
        BigDecimal valorMaoObra,

        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "O valor das peças não pode ser negativo."
        )
        BigDecimal valorPecas,

        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "O desconto não pode ser negativo."
        )
        BigDecimal desconto,

        String observacao

) {
}