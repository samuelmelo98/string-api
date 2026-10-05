package org.stringtecnologia.string_api.model.dto.nome;

import org.stringtecnologia.string_api.model.NomeSugestao;
import org.stringtecnologia.string_api.model.enums.OrigemNomeSugestao;
import org.stringtecnologia.string_api.model.enums.StatusNomeSugestao;
import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;

import java.time.Instant;
public record NomeSugestaoResponse(

        Long id,

        String nome,

        String nomeNormalizado,

        TipoNomeSugestao tipo,

        OrigemNomeSugestao origem,

        StatusNomeSugestao status,

        Long frequencia,

        Instant dataCadastro,

        Instant dataAtualizacao

) {

    public static NomeSugestaoResponse from(
            NomeSugestao entidade
    ) {

        return new NomeSugestaoResponse(
                entidade.getNomeSugestaoId(),
                entidade.getNome(),
                entidade.getNomeNormalizado(),
                entidade.getTipo(),
                entidade.getOrigem(),
                entidade.getStatus(),
                entidade.getFrequencia(),
                entidade.getDataCadastro(),
                entidade.getDataAtualizacao()
        );
    }
}
