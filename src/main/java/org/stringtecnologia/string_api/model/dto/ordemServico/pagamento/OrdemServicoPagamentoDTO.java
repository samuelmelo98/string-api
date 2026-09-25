package org.stringtecnologia.string_api.model.dto.ordemServico.pagamento;
import org.stringtecnologia.string_api.model.enums.FormaPagamento;
import org.stringtecnologia.string_api.model.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemServicoPagamentoDTO(

        Long pagamentoId,

        Long ordemServicoId,

        FormaPagamento formaPagamento,

        BigDecimal valor,

        Integer parcelas,

        String observacao,

        StatusPagamento status,

        LocalDateTime dataPagamento,

        LocalDateTime dataCancelamento

) {
}