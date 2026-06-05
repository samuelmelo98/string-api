package org.stringtecnologia.string_api.documento.dto;


public record DocumentoInternoDTO(

        String nomeArquivo,

        String contentType,

        byte[] bytes

) {
}
