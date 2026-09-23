package org.stringtecnologia.string_api.model.dto.aparelho;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AparelhoUpdateDTO(

        @NotNull(message = "Informe a marca.")
        @Positive(message = "Informe uma marca válida.")
        Long marcaId,

        @NotNull(message = "Informe o tipo de aparelho.")
        @Positive(message = "Informe um tipo de aparelho válido.")
        Long tipoAparelhoId,

        @NotBlank(message = "Informe o modelo.")
        String modelo,

        String modeloComercial,

        @NotBlank(message = "Informe o número de série.")
        String numeroSerie

) {
}