package org.stringtecnologia.string_api.model.factory.aparelho;

import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.DominioSistema;

@Component
public class AparelhoFactory {

    public Aparelho criar(
            AparelhoRequestDTO dto,
            DominioSistema statusInicial
    ) {

        return Aparelho.builder()
                .marca(dto.marca())
                .modelo(dto.modelo())
                .modeloComercial(dto.modeloComercial())
                .numeroSerie(dto.numeroSerie())
                .descricao(dto.descricao())
                .tipo(dto.tipo())
                .defeito(dto.defeito())
                .observacao(dto.observacao())
                .fimGarantia(dto.fimGarantia())
                .statusAparelho(statusInicial)
                .build();
    }
}
