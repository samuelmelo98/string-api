package org.stringtecnologia.string_api.model.dto.teste;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioTesteDTO(

        String titulo,
        String responsavel,
        LocalDate dataEmissao,
        BigDecimal valorTotal,
        Boolean aprovado,

        String site,

        ClienteDTO cliente,

        List<ItemDTO> itens

) {
}




