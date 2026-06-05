package org.stringtecnologia.string_api.model.dto.documento;

public record ArquivoSalvoDTO(
        String caminho,
        String nomeOriginal,
        String mimeType,
        Long tamanho,
        String hashSha256,
        String algoritmoHash
) {

}
