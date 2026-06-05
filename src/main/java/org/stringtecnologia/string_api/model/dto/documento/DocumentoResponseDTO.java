package org.stringtecnologia.string_api.model.dto.documento;

public record DocumentoResponseDTO(
        Long id,
        String nome,
        String mimeType,
        String tipoDocumento,
        Long tamanho,
        String hashSha256
) {}
