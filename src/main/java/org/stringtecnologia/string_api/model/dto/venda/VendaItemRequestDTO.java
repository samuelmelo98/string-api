package org.stringtecnologia.string_api.model.dto.venda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.stringtecnologia.string_api.model.enums.FormaPagamento;

import java.math.BigDecimal;

@Getter
@Setter
public class VendaItemRequestDTO {

    @NotBlank
    @Size(max = 255)
    private String descricao;

    @NotNull
    @Positive
    private BigDecimal quantidade;

    @NotNull
    @DecimalMin(
            value = "0.00",
            inclusive = true
    )
    private BigDecimal valorUnitario;

    @DecimalMin(
            value = "0.00",
            inclusive = true
    )
    private BigDecimal desconto = BigDecimal.ZERO;
}
