package org.stringtecnologia.string_api.ocr.service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class OcrNormalizer {
    private static final Pattern SEPARADORES = Pattern.compile("[\\s\\-]+");

    private OcrNormalizer() {
    }

    public static String normalizar(String s) {
        String t = Normalizer.normalize(s, Normalizer.Form.NFKC).toUpperCase(Locale.ROOT).trim();
        return SEPARADORES.matcher(t).replaceAll("");
    }
}
