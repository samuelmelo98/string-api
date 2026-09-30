package org.stringtecnologia.string_api.model.dto.venda;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VendaItemResponseDTO {

    private Long vendaItemId;

    private String descricao;

    private BigDecimal quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal desconto;

    private BigDecimal valorTotal;

}
