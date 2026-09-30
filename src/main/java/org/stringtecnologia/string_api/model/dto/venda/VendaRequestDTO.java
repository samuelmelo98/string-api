package org.stringtecnologia.string_api.model.dto.venda;


import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

}