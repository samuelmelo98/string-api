package org.stringtecnologia.string_api.model.dto.relatorio;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioSemanalTecnicoItemDTO {

    private Long ordemServicoId;

    private String numeroOrdemServico;

    private Long aparelhoId;

    private String modelo;

    private String modeloComercial;

    private String numeroSerie;

    private Long clienteId;

    private String clienteNome;

    private LocalDateTime dataEntrega;

    private BigDecimal valorOrdemServico;

    private BigDecimal valorMaterial;

    private BigDecimal totalPago;

    @Builder.Default
    private List<RelatorioSemanalTecnicoPagamentoDTO> pagamentos =
            new ArrayList<>();
}