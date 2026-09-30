package org.stringtecnologia.string_api.model.dto.cliente;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClienteOpcaoDTO {

    private Long clienteId;

    private String nome;

    private String cpf;

    private String telefone;

}
