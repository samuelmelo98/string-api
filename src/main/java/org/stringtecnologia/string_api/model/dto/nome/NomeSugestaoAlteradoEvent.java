package org.stringtecnologia.string_api.model.dto.nome;

public record NomeSugestaoAlteradoEvent(

        NomeSugestaoCacheEntry anterior,

        NomeSugestaoCacheEntry atual

) {
}
