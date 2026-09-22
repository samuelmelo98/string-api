package org.stringtecnologia.string_api.model.dto.ordemServico;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConsultaOrdemServicoRequestDTO(

        @NotBlank(
                message = "Os últimos dígitos do CPF são obrigatórios."
        )
        @Pattern(
                regexp = "\\d{2}",
                message = "Informe exatamente os 2 últimos dígitos do CPF."
        )
        String ultimosDigitosCpf,

        @NotBlank(
                message = "O token de consulta é obrigatório."
        )
        @Size(
                max = 36,
                message = "Token de consulta inválido."
        )
        String token

) {
}
