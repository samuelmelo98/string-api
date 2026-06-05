package org.stringtecnologia.string_api.util.exceptions;


import org.springframework.http.HttpStatus;
import org.stringtecnologia.string_api.util.CategoriaDominio;

public class DominioNaoEncontradoException extends DomainException {

    public DominioNaoEncontradoException() {
        super(
                CodeError.DOMINIO_NAO_ENCONTRADO,
                "Domínio não encontrado.",
                HttpStatus.NOT_FOUND
        );
    }

    public DominioNaoEncontradoException(CategoriaDominio categoria, String codigo) {
        super(
                CodeError.DOMINIO_NAO_ENCONTRADO,
                String.format(
                        "Domínio não encontrado para categoria '%s' e código '%s'.",
                        categoria,
                        codigo
                ),
                HttpStatus.NOT_FOUND
        );
    }
}
