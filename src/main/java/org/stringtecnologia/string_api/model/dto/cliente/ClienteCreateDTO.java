package org.stringtecnologia.string_api.model.dto.cliente;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteCreateDTO(

        @NotBlank
        String nome,

        @NotBlank(message = "Informe o CPF.")
        @Pattern(
                regexp = "\\d{11}",
                message = "O CPF deve conter 11 dígitos."
        )
        @CPF(message = "CPF inválido.")
        String cpf,

        @Email
        String email,

        String telefone,

        String endereco,

        String cidade,

        String estado,

        String cep

) {
}