package org.stringtecnologia.string_api.util;

import java.text.Normalizer;
import java.util.Locale;


public final class NomeNormalizer {

    private static final Locale PT_BR =
            Locale.forLanguageTag(
                    "pt-BR"
            );

    private NomeNormalizer() {
    }

    public static String normalizar(
            String valor
    ) {

        if (valor == null) {
            return "";
        }

        String semAcentos =
                Normalizer
                        .normalize(
                                valor.trim(),
                                Normalizer.Form.NFD
                        )
                        .replaceAll(
                                "\\p{M}",
                                ""
                        );

        return semAcentos
                .replaceAll(
                        "\\s+",
                        " "
                )
                .toLowerCase(
                        PT_BR
                );
    }
}
