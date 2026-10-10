package org.stringtecnologia.string_api.model.dto.venda;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.stringtecnologia.string_api.model.enums.FormaPagamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VendaRequestDTO {

    @Positive
    private Long clienteId;

    @DecimalMin(
            value = "0.00",
            inclusive = true
    )
    private BigDecimal desconto = BigDecimal.ZERO;

    @Size(max = 2000)
    private String observacao;

    @Valid
    @NotEmpty
    private List<VendaItemRequestDTO> itens = new ArrayList<>();

    @NotNull(message = "Informe a forma de pagamento")
    private FormaPagamento formaPagamento;

}