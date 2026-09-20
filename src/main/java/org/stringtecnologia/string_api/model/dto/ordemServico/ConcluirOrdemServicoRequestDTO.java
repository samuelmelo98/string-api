package org.stringtecnologia.string_api.model.dto.ordemServico;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConcluirOrdemServicoRequestDTO(

        @NotBlank(
                message = "A solução executada é obrigatória."
        )
        String solucao,

        @NotNull(
                message = "O valor final é obrigatório."
        )
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "O valor final não pode ser negativo."
        )
        BigDecimal valorFinal,

        String observacao

) {
}