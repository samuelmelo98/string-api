package org.stringtecnologia.string_api.model.dto.venda;


import lombok.Getter;
import lombok.Setter;
import org.stringtecnologia.string_api.model.enums.StatusVenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class VendaResponseDTO {

    private Long vendaId;

    private String numero;

    private Long clienteId;

    private String clienteNome;

    private LocalDateTime dataVenda;

    private StatusVenda status;

    private BigDecimal valorSubtotal;

    private BigDecimal descontoItens;

    private BigDecimal desconto;

    private BigDecimal valorTotal;

    private Integer quantidadeItens;

}