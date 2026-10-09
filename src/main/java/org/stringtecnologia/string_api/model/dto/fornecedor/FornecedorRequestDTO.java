package org.stringtecnologia.string_api.model.dto.fornecedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.stringtecnologia.string_api.model.enums.TipoPessoa;

public record FornecedorRequestDTO(
        @NotNull TipoPessoa tipoPessoa,
        @NotBlank @Size(max = 200) String razaoSocial,
        @Size(max = 200) String nomeFantasia,
        @NotBlank String cpfCnpj,
        @Size(max = 30) String inscricaoEstadual,
        @Email @Size(max = 150) String email,
        @Size(max = 20) String telefone,
        @Size(max = 20) String whatsapp,
        String cep,
        @Size(max = 200) String endereco,
        @Size(max = 20) String numero,
        @Size(max = 100) String complemento,
        @Size(max = 100) String bairro,
        @Size(max = 100) String cidade,
        @Size(max = 2) String estado,
        String observacao
) { }
