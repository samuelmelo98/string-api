package org.stringtecnologia.string_api.model.dto.ordemServico;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AtribuirTecnicoRequestDTO(

        @NotNull(
                message = "O técnico é obrigatório."
        )
        @Positive(
                message = "O identificador do técnico é inválido."
        )
        Long tecnicoId

) {
}