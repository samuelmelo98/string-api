package org.stringtecnologia.string_api.model.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioSemanalTecnicoDTO {

    private Long tecnicoId;

    private String tecnicoNome;

    private String tecnicoEmail;

    private LocalDate inicio;

    private LocalDate fim;

    @Builder.Default
    private List<RelatorioSemanalTecnicoItemDTO> aparelhos =
            new ArrayList<>();

    private BigDecimal totalOrdensServico;

    private BigDecimal totalMateriais;

    private BigDecimal valorLiquido;

    private BigDecimal valorTecnico;
}