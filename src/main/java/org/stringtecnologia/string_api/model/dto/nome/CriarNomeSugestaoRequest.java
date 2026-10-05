package org.stringtecnologia.string_api.model.dto.nome;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.stringtecnologia.string_api.model.enums.OrigemNomeSugestao;
import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;

public record CriarNomeSugestaoRequest(

        @NotBlank
        @Size(max = 120)
        String nome,

        @NotNull
        TipoNomeSugestao tipo,

        OrigemNomeSugestao origem

) {
}
