package org.stringtecnologia.string_api.model.dto.aparelho;

import java.time.LocalDateTime;

public record AparelhoResponseDTO(

        Long aparelhoId,

        Long marcaId,
        String marca,

        Long tipoAparelhoId,
        String tipoAparelho,

        String modelo,
        String modeloComercial,
        String numeroSerie,

        Long statusAparelhoId,
        String statusAparelho,

        String numeroOrdemServico,

        LocalDateTime dataEntradaAparelho,

        String observacao

) {
}