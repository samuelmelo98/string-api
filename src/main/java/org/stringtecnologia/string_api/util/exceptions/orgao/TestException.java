package org.stringtecnologia.string_api.util.exceptions.orgao;

import org.stringtecnologia.string_api.util.exceptions.CodeError;
import org.stringtecnologia.string_api.util.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class TestException extends DomainException {

    public TestException(String nome) {
        super(
                CodeError.NOME_DUPLICADO,
                "Já existe um órgão cadastrado com o NOME: " + nome,
                HttpStatus.BAD_REQUEST
        );
    }
}