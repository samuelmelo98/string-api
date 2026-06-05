package org.stringtecnologia.string_api.documento.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdiantamentoDocumentoDTO(

        Long adiantamentoId,

        String usuario,

        BigDecimal valor,

        LocalDate data

       // String brasaoSrc

) {
}
