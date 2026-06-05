package org.stringtecnologia.string_api.model.dto.documento;

public record DocumentoDownloadDTO(
        String nome,
        String mimeType,
        String tipoDocumento,
        byte[] dados
) {}
