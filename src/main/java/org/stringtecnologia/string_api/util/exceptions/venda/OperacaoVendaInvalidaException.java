package org.stringtecnologia.string_api.util.exceptions.venda;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OperacaoVendaInvalidaException
        extends RuntimeException {

  public OperacaoVendaInvalidaException(
          String mensagem
  ) {

    super(mensagem);

  }

}
