package org.stringtecnologia.string_api.model.dto.aparelho;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AparelhoRequestDTO(

        @NotNull(message = "Informe o cliente.")
        @Positive(message = "Informe um cliente válido.")
        Long clienteId,

        @NotNull(message = "Informe a marca.")
        @Positive(message = "Informe uma marca válida.")
        Long marcaId,

        @NotBlank(message = "Informe o modelo.")
        String modelo,

        String modeloComercial,

        @NotBlank(message = "Informe o número de série.")
        String numeroSerie,

        String descricao,

        @NotNull(message = "Informe o tipo de aparelho.")
        @Positive(message = "Informe um tipo de aparelho válido.")
        Long tipoAparelhoId,

        @NotBlank(message = "Informe o defeito.")
        String defeito,

        String observacao,

        LocalDateTime fimGarantia
) {
}