package org.stringtecnologia.string_api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import org.stringtecnologia.string_api.model.NomeSugestaoRedisKeys;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoCacheEntry;
import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;

import java.time.Duration;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class NomeSugestaoRedisRepository {

    private static final double LEX_SCORE =
            0D;

    private static final DefaultRedisScript<Long>
            RELEASE_LOCK_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    if redis.call(
                        'get',
                        KEYS[1]
                    ) == ARGV[1] then

                        return redis.call(
                            'del',
                            KEYS[1]
                        )

                    end

                    return 0
                    """,
                    Long.class
            );

    private final StringRedisTemplate
            redisTemplate;

    public Optional<String> obterVersaoAtiva() {

        String versao =
                redisTemplate
                        .opsForValue()
                        .get(
                                NomeSugestaoRedisKeys
                                        .ACTIVE_VERSION
                        );

        return Optional
                .ofNullable(
                        versao
                )
                .filter(
                        valor ->
                                !valor.isBlank()
                );
    }

    public String ativarVersao(
            String novaVersao
    ) {

        return redisTemplate
                .opsForValue()
                .getAndSet(
                        NomeSugestaoRedisKeys
                                .ACTIVE_VERSION,
                        novaVersao
                );
    }

    public boolean adquirirLock(
            String token,
            Duration ttl
    ) {

        Boolean adquirido =
                redisTemplate
                        .opsForValue()
                        .setIfAbsent(
                                NomeSugestaoRedisKeys
                                        .REBUILD_LOCK,
                                token,
                                ttl
                        );

        return Boolean.TRUE
                .equals(
                        adquirido
                );
    }

    public void liberarLock(
            String token
    ) {

        redisTemplate.execute(
                RELEASE_LOCK_SCRIPT,
                List.of(
                        NomeSugestaoRedisKeys
                                .REBUILD_LOCK
                ),
                token
        );
    }

    public void carregarVersao(
            String versao,
            Collection<NomeSugestaoCacheEntry> entradas
    ) {

        Map<
                TipoNomeSugestao,
                Set<ZSetOperations.TypedTuple<String>>
                > agrupado =
                new LinkedHashMap<>();

        for (
                NomeSugestaoCacheEntry entrada
                : entradas
        ) {

            if (!entrada.ativo()) {
                continue;
            }

            agrupado
                    .computeIfAbsent(
                            entrada.tipo(),
                            ignorado ->
                                    new LinkedHashSet<>()
                    )
                    .add(
                            new DefaultTypedTuple<>(
                                    membro(
                                            entrada
                                    ),
                                    LEX_SCORE
                            )
                    );
        }

        agrupado.forEach(
                (tipo, membros) -> {

                    String key =
                            NomeSugestaoRedisKeys
                                    .zset(
                                            versao,
                                            tipo
                                    );

                    redisTemplate
                            .opsForZSet()
                            .add(
                                    key,
                                    membros
                            );
                }
        );
    }

    public Optional<
            List<NomeSugestaoCacheEntry>
            >
    buscarNaVersaoAtiva(
            Collection<TipoNomeSugestao> tipos,
            String prefixoNormalizado,
            int limite
    ) {

        Optional<String> versao =
                obterVersaoAtiva();

        if (versao.isEmpty()) {

            return Optional.empty();
        }

        return Optional.of(
                buscar(
                        versao.get(),
                        tipos,
                        prefixoNormalizado,
                        limite
                )
        );
    }

    public List<NomeSugestaoCacheEntry> buscar(
            String versao,
            Collection<TipoNomeSugestao> tipos,
            String prefixoNormalizado,
            int limite
    ) {

        Map<
                String,
                NomeSugestaoCacheEntry
                > unicos =
                new LinkedHashMap<>();

        for (
                TipoNomeSugestao tipo
                : tipos
        ) {

            String key =
                    NomeSugestaoRedisKeys
                            .zset(
                                    versao,
                                    tipo
                            );

            Range<String> range =
                    Range.closed(
                            prefixoNormalizado,
                            prefixoNormalizado
                                    + "\uffff"
                    );

            Set<String> encontrados =
                    redisTemplate
                            .opsForZSet()
                            .rangeByLex(
                                    key,
                                    range,
                                    Limit
                                            .limit()
                                            .count(
                                                    limite
                                            )
                            );

            if (encontrados == null) {
                continue;
            }

            encontrados
                    .stream()
                    .map(
                            valor ->
                                    parse(
                                            valor,
                                            tipo
                                    )
                    )
                    .forEach(
                            entrada ->
                                    unicos
                                            .putIfAbsent(
                                                    entrada.tipo()
                                                            + ":"
                                                            + entrada.nomeNormalizado(),
                                                    entrada
                                            )
                    );
        }

        return unicos
                .values()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        NomeSugestaoCacheEntry::nome,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .limit(
                        limite
                )
                .toList();
    }

    public void sincronizar(
            String versao,
            NomeSugestaoCacheEntry anterior,
            NomeSugestaoCacheEntry atual
    ) {

        if (anterior != null) {

            remover(
                    versao,
                    anterior.tipo(),
                    anterior.nomeNormalizado()
            );
        }

        if (atual == null) {
            return;
        }

        if (atual.ativo()) {

            adicionar(
                    versao,
                    atual
            );

            return;
        }

        remover(
                versao,
                atual.tipo(),
                atual.nomeNormalizado()
        );
    }

    public void adicionar(
            String versao,
            NomeSugestaoCacheEntry entrada
    ) {

        /*
         * Garante que não exista uma representação
         * antiga do mesmo nome normalizado.
         */
        remover(
                versao,
                entrada.tipo(),
                entrada.nomeNormalizado()
        );

        redisTemplate
                .opsForZSet()
                .add(
                        NomeSugestaoRedisKeys
                                .zset(
                                        versao,
                                        entrada.tipo()
                                ),
                        membro(
                                entrada
                        ),
                        LEX_SCORE
                );
    }

    public void remover(
            String versao,
            TipoNomeSugestao tipo,
            String nomeNormalizado
    ) {

        String key =
                NomeSugestaoRedisKeys
                        .zset(
                                versao,
                                tipo
                        );

        Range<String> range =
                Range.closed(
                        nomeNormalizado
                                + "|",
                        nomeNormalizado
                                + "|\uffff"
                );

        Set<String> encontrados =
                redisTemplate
                        .opsForZSet()
                        .rangeByLex(
                                key,
                                range
                        );

        if (
                encontrados == null
                        || encontrados.isEmpty()
        ) {
            return;
        }

        redisTemplate
                .opsForZSet()
                .remove(
                        key,
                        encontrados.toArray()
                );
    }

    public void expirarVersao(
            String versao,
            Duration ttl
    ) {

        NomeSugestaoRedisKeys
                .chavesVersao(
                        versao
                )
                .forEach(
                        key ->
                                redisTemplate
                                        .expire(
                                                key,
                                                ttl
                                        )
                );
    }

    public void excluirVersao(
            String versao
    ) {

        redisTemplate.delete(
                NomeSugestaoRedisKeys
                        .chavesVersao(
                                versao
                        )
        );
    }

    private String membro(
            NomeSugestaoCacheEntry entrada
    ) {

        return entrada
                .nomeNormalizado()
                + "|"
                + entrada.nome();
    }

    private NomeSugestaoCacheEntry parse(
            String valor,
            TipoNomeSugestao tipo
    ) {

        int separador =
                valor.indexOf('|');

        if (separador < 0) {

            return new NomeSugestaoCacheEntry(
                    null,
                    valor,
                    valor,
                    tipo,
                    null,
                    0L
            );
        }

        String normalizado =
                valor.substring(
                        0,
                        separador
                );

        String nome =
                valor.substring(
                        separador + 1
                );

        return new NomeSugestaoCacheEntry(
                null,
                nome,
                normalizado,
                tipo,
                null,
                0L
        );
    }
}