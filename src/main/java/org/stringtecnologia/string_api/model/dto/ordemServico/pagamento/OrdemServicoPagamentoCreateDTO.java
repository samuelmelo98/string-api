package org.stringtecnologia.string_api.model.dto.ordemServico.pagamento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.stringtecnologia.string_api.model.enums.FormaPagamento;

import java.math.BigDecimal;

public record OrdemServicoPagamentoCreateDTO(

        @NotNull(message = "A forma de pagamento é obrigatória")
        FormaPagamento formaPagamento,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(
                value = "0.01",
                message = "O valor deve ser maior que zero"
        )
        BigDecimal valor,

        @Min(
                value = 1,
                message = "A quantidade de parcelas deve ser maior que zero"
        )
        Integer parcelas,

        String observacao

) {
}