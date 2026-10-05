package org.stringtecnologia.string_api.model;

import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;

import java.util.Arrays;
import java.util.List;

public final class NomeSugestaoRedisKeys {

    public static final String PREFIX =
            "string-api:nome-sugestao";

    public static final String ACTIVE_VERSION =
            PREFIX
                    + ":active-version";

    public static final String REBUILD_LOCK =
            PREFIX
                    + ":rebuild-lock";

    private NomeSugestaoRedisKeys() {
    }

    public static String zset(
            String versao,
            TipoNomeSugestao tipo
    ) {

        return PREFIX
                + ":v:"
                + versao
                + ":"
                + sufixo(tipo);
    }

    public static List<String> chavesVersao(
            String versao
    ) {

        return Arrays
                .stream(
                        TipoNomeSugestao.values()
                )
                .map(
                        tipo ->
                                zset(
                                        versao,
                                        tipo
                                )
                )
                .toList();
    }

    private static String sufixo(
            TipoNomeSugestao tipo
    ) {

        return switch (tipo) {

            case PRIMEIRO_NOME ->
                    "primeiro";

            case INTERMEDIARIO ->
                    "intermediario";

            case SOBRENOME ->
                    "sobrenome";

            case PARTICULA ->
                    "particula";
        };
    }
}