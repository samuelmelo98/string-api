package org.stringtecnologia.string_api.model.dto.venda;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetricaVendaDTO(
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate inicio,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate fim,
        long quantidade,
        BigDecimal valorTotal,
        long quantidadeCanceladas,
        BigDecimal valorCancelado
) {
}
