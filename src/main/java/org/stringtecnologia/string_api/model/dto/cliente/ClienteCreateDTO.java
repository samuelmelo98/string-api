package org.stringtecnologia.string_api.model.dto.cliente;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteCreateDTO(

        @NotBlank
        String nome,

        @NotBlank
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