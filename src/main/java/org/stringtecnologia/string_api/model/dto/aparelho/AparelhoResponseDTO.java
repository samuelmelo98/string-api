package org.stringtecnologia.string_api.model.dto.aparelho;

public record AparelhoResponseDTO(
        Long aparelhoId,
        String marca,
        String modelo,
        String modeloComercial,
        String numeroSerie,
        Long statusAparelhoId,
        String statusAparelho,
        String observacao
) {
}