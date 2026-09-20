package org.stringtecnologia.string_api.model.factory.aparelho;

import org.springframework.stereotype.Component;

import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.DominioSistema;
import org.stringtecnologia.string_api.model.entities.Marca;
import org.stringtecnologia.string_api.model.entities.TipoAparelho;

@Component
public class AparelhoFactory {

    public Aparelho criar(
            AparelhoRequestDTO dto,
            Marca marca,
            TipoAparelho tipoAparelho,
            DominioSistema statusInicial
    ) {
        return Aparelho.builder()
                .marca(marca)
                .modelo(dto.modelo())
                .modeloComercial(dto.modeloComercial())
                .numeroSerie(dto.numeroSerie())
                .descricao(dto.descricao())
                .tipo(tipoAparelho)
                .defeito(dto.defeito())
                .observacao(dto.observacao())
                .fimGarantia(dto.fimGarantia())
                .statusAparelho(statusInicial)
                .build();
    }
}