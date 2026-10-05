package org.stringtecnologia.string_api.model.dto.nome;


import org.stringtecnologia.string_api.model.NomeSugestao;
import org.stringtecnologia.string_api.model.enums.StatusNomeSugestao;
import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;

public record NomeSugestaoCacheEntry(

        Long id,

        String nome,

        String nomeNormalizado,

        TipoNomeSugestao tipo,

        StatusNomeSugestao status,

        Long frequencia

) {

    public static NomeSugestaoCacheEntry from(
            NomeSugestao entidade
    ) {

        return new NomeSugestaoCacheEntry(
                entidade.getNomeSugestaoId(),
                entidade.getNome(),
                entidade.getNomeNormalizado(),
                entidade.getTipo(),
                entidade.getStatus(),
                entidade.getFrequencia()
        );
    }

    public boolean ativo() {

        return status ==
                StatusNomeSugestao.ATIVO;
    }
}
