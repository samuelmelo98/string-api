package org.stringtecnologia.string_api.ocr.service;

import java.util.List;

public interface OcrExtractor {
    List<Achado> extrair(byte[] imagem) throws Exception;

    record Achado(String valor, String origem) {
    }
}
