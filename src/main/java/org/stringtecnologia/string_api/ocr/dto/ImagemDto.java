package org.stringtecnologia.string_api.ocr.dto;

import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrStatus;

import java.time.Instant;

public record ImagemDto(Long id, String nomeArquivo, OcrStatus status, Instant criadoEm, Instant processadoEm) {
    public static ImagemDto from(OcrImagem i) {
        return new ImagemDto(i.id, i.nomeArquivo, i.status, i.criadoEm, i.processadoEm);
    }
}
