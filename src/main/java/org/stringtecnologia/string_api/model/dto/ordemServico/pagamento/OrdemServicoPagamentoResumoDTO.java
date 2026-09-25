package org.stringtecnologia.string_api.model.dto.ordemServico.pagamento;

import java.math.BigDecimal;
import java.util.List;

public record OrdemServicoPagamentoResumoDTO(

        Long ordemServicoId,

        BigDecimal valorOrdemServico,

        BigDecimal totalPago,

        BigDecimal saldoPendente,

        boolean quitado,

        List<OrdemServicoPagamentoDTO> pagamentos

) {
}
