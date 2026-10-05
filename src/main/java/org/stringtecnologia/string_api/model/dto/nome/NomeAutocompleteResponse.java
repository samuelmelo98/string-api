package org.stringtecnologia.string_api.model.dto.nome;

import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;

public record NomeAutocompleteResponse(

        String nome,

        TipoNomeSugestao tipo

) {
}
