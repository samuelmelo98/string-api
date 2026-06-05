package org.stringtecnologia.string_api.util.exceptions;

import org.springframework.http.HttpStatus;

public class DescricaoInvalidaException extends DomainException {

    public DescricaoInvalidaException() {
        super(
                CodeError.DESCRICAO_INVALIDA,
                "Descrição não pode ser vazia",
                HttpStatus.BAD_REQUEST
        );
    }
}
