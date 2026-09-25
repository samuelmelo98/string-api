package org.stringtecnologia.string_api.model.dto.relatorio;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.stringtecnologia.string_api.model.enums.FormaPagamento;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioSemanalTecnicoPagamentoDTO {

    private FormaPagamento formaPagamento;

    private BigDecimal valor;

    private Integer parcelas;
}
