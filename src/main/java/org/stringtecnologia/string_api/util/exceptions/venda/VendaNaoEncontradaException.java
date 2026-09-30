package org.stringtecnologia.string_api.util.exceptions.venda;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VendaNaoEncontradaException
        extends RuntimeException {

    public VendaNaoEncontradaException(Long vendaId) {

        super(
                "Venda não encontrada. ID: "
                        + vendaId
        );

    }

}
