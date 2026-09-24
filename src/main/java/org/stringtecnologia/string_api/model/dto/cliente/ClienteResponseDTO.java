package org.stringtecnologia.string_api.model.dto.cliente;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClienteResponseDTO(

        Long clienteId,

        String nome,

        String cpf,

        String email,

        String telefone,

        String endereco,

        String cidade,

        String estado,

        String cep,

        LocalDate dataNascimento,

        Boolean ativo,

        LocalDateTime dataCadastro

) {
}