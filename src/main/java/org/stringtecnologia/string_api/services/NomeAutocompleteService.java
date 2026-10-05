package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.dto.nome.NomeAutocompleteResponse;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoCacheEntry;
import org.stringtecnologia.string_api.model.enums.StatusNomeSugestao;
import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;
import org.stringtecnologia.string_api.repository.NomeSugestaoRedisRepository;
import org.stringtecnologia.string_api.repository.NomeSugestaoRepository;
import org.stringtecnologia.string_api.util.NomeNormalizer;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NomeAutocompleteService {

    private static final int MIN_PREFIXO =
            2;

    private static final int LIMITE =
            10;

    private final NomeSugestaoRedisRepository
            redisRepository;

    private final NomeSugestaoRepository
            repository;

    public List<NomeAutocompleteResponse> sugerir(
            String texto
    ) {

        EntradaNome entrada =
                analisar(
                        texto
                );

        if (
                entrada
                        .prefixoNormalizado()
                        .length()
                        < MIN_PREFIXO
        ) {

            return List.of();
        }

        Collection<TipoNomeSugestao> tipos =
                tiposPara(
                        entrada
                );

        List<NomeSugestaoCacheEntry>
                encontrados =
                buscarComFallback(
                        entrada
                                .prefixoNormalizado(),
                        tipos
                );

        return montarResultado(
                entrada,
                encontrados
        );
    }

    private List<NomeSugestaoCacheEntry>
    buscarComFallback(
            String prefixo,
            Collection<TipoNomeSugestao> tipos
    ) {

        try {

            var resultadoRedis =
                    redisRepository
                            .buscarNaVersaoAtiva(
                                    tipos,
                                    prefixo,
                                    LIMITE
                            );

            if (
                    resultadoRedis.isPresent()
            ) {

                return resultadoRedis
                        .get();
            }

        } catch (
                DataAccessException ex
        ) {

            log.warn(
                    "Redis indisponível no "
                            + "autocomplete. "
                            + "Usando PostgreSQL."
            );
        }

        return buscarNoPostgres(
                prefixo,
                tipos
        );
    }

    private List<NomeSugestaoCacheEntry>
    buscarNoPostgres(
            String prefixo,
            Collection<TipoNomeSugestao> tipos
    ) {

        return repository
                .buscarPorPrefixo(
                        prefixo,
                        tipos,
                        StatusNomeSugestao.ATIVO,
                        PageRequest.of(
                                0,
                                LIMITE
                        )
                )
                .stream()
                .map(
                        NomeSugestaoCacheEntry::from
                )
                .toList();
    }

    private EntradaNome analisar(
            String texto
    ) {

        if (texto == null) {

            return EntradaNome
                    .vazia();
        }

        String valor =
                texto
                        .replaceAll(
                                "\\s+",
                                " "
                        )
                        .stripLeading();

        int ultimoEspaco =
                valor.lastIndexOf(
                        ' '
                );

        if (ultimoEspaco < 0) {

            return new EntradaNome(
                    "",
                    NomeNormalizer
                            .normalizar(
                                    valor
                            ),
                    true
            );
        }

        String parteAnterior =
                valor.substring(
                        0,
                        ultimoEspaco
                );

        String prefixo =
                valor.substring(
                        ultimoEspaco + 1
                );

        return new EntradaNome(
                parteAnterior,
                NomeNormalizer
                        .normalizar(
                                prefixo
                        ),
                false
        );
    }

    private Collection<TipoNomeSugestao>
    tiposPara(
            EntradaNome entrada
    ) {

        if (entrada.primeiroNome()) {

            return List.of(
                    TipoNomeSugestao.PRIMEIRO_NOME
            );
        }

        return List.of(
                TipoNomeSugestao.PRIMEIRO_NOME,
                TipoNomeSugestao.INTERMEDIARIO,
                TipoNomeSugestao.SOBRENOME,
                TipoNomeSugestao.PARTICULA
        );
    }

    private List<NomeAutocompleteResponse>
    montarResultado(
            EntradaNome entrada,
            List<NomeSugestaoCacheEntry> encontrados
    ) {

        Map<
                String,
                NomeAutocompleteResponse
                > unicos =
                new LinkedHashMap<>();

        encontrados.forEach(
                sugestao -> {

                    String nomeCompleto =
                            montarNome(
                                    entrada
                                            .parteAnterior(),
                                    sugestao
                                            .nome()
                            );

                    unicos.putIfAbsent(
                            nomeCompleto,
                            new NomeAutocompleteResponse(
                                    nomeCompleto,
                                    sugestao.tipo()
                            )
                    );
                }
        );

        return unicos
                .values()
                .stream()
                .limit(
                        LIMITE
                )
                .toList();
    }

    private String montarNome(
            String parteAnterior,
            String sugestao
    ) {

        if (parteAnterior.isBlank()) {

            return sugestao;
        }

        return parteAnterior
                + " "
                + sugestao;
    }

    private record EntradaNome(

            String parteAnterior,

            String prefixoNormalizado,

            boolean primeiroNome

    ) {

        private static EntradaNome vazia() {

            return new EntradaNome(
                    "",
                    "",
                    true
            );
        }
    }
}
