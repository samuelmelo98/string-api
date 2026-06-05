package org.stringtecnologia.string_api.documento.dto;

public record DocumentoResponseDTO(
        Long id,
        String nome,
        String mimeType,
        String tipoDocumento,
        String caminho,
        Long tamanho,
        String hashSha256
) {}
