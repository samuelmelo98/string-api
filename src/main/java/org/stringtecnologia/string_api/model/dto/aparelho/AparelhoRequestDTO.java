package org.stringtecnologia.string_api.model.dto.aparelho;

import org.stringtecnologia.string_api.model.enums.Marca;
import org.stringtecnologia.string_api.model.enums.TipoAparalho;

import java.time.LocalDateTime;

public record AparelhoRequestDTO(
        Marca marca,
        String modelo,
        String modeloComercial,
        String numeroSerie,
        String descricao,
        TipoAparalho tipo,
        String defeito,
        String observacao,
        LocalDateTime fimGarantia
) {
}