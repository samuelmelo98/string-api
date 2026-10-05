package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoAlteradoEvent;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoCacheEntry;
import org.stringtecnologia.string_api.model.enums.StatusNomeSugestao;
import org.stringtecnologia.string_api.repository.NomeSugestaoRedisRepository;
import org.stringtecnologia.string_api.repository.NomeSugestaoRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NomeSugestaoCacheService {

    private static final Duration LOCK_TTL =
            Duration.ofMinutes(
                    5
            );

    private static final Duration
            VERSAO_ANTIGA_TTL =
            Duration.ofHours(
                    1
            );

    private final NomeSugestaoRepository
            repository;

    private final NomeSugestaoRedisRepository
            redisRepository;

    public void reconstruir() {

        String token =
                UUID
                        .randomUUID()
                        .toString();

        boolean lockAdquirido =
                false;

        boolean novaVersaoAtivada =
                false;

        String novaVersao =
                null;

        try {

            lockAdquirido =
                    adquirirLock(
                            token
                    );

            if (!lockAdquirido) {

                log.info(
                        "Cache de nomes já está "
                                + "sendo reconstruído "
                                + "por outra instância."
                );

                return;
            }

            Instant inicio =
                    Instant.now();

            novaVersao =
                    gerarVersao();

            List<NomeSugestaoCacheEntry>
                    nomes =
                    carregarNomesAtivos();

            redisRepository
                    .carregarVersao(
                            novaVersao,
                            nomes
                    );

            /*
             * Captura alterações ocorridas
             * enquanto a carga principal estava
             * sendo realizada.
             */
            reconciliarAlteracoes(
                    novaVersao,
                    inicio
            );

            String versaoAnterior =
                    redisRepository
                            .ativarVersao(
                                    novaVersao
                            );

            novaVersaoAtivada =
                    true;

            /*
             * Fecha a janela de concorrência
             * existente entre a primeira
             * reconciliação e a troca da versão.
             */
            reconciliarAlteracoes(
                    novaVersao,
                    inicio
            );

            expirarVersaoAnterior(
                    versaoAnterior,
                    novaVersao
            );

            log.info(
                    "Cache de nomes reconstruído. "
                            + "Versão={}, registros={}",
                    novaVersao,
                    nomes.size()
            );

        } catch (
                DataAccessException ex
        ) {

            log.warn(
                    "Redis indisponível durante "
                            + "reconstrução do cache. "
                            + "Autocomplete continuará "
                            + "usando PostgreSQL.",
                    ex
            );

            if (!novaVersaoAtivada) {

                excluirNovaVersaoSilenciosamente(
                        novaVersao
                );
            }

        } finally {

            if (lockAdquirido) {

                liberarLockSilenciosamente(
                        token
                );
            }
        }
    }

    public void sincronizar(
            NomeSugestaoAlteradoEvent event
    ) {

        try {

            redisRepository
                    .obterVersaoAtiva()
                    .ifPresent(
                            versao ->
                                    redisRepository
                                            .sincronizar(
                                                    versao,
                                                    event.anterior(),
                                                    event.atual()
                                            )
                    );

        } catch (
                DataAccessException ex
        ) {

            log.warn(
                    "Não foi possível atualizar "
                            + "o cache de nomes. "
                            + "PostgreSQL permanece "
                            + "como fonte de verdade.",
                    ex
            );
        }
    }

    private boolean adquirirLock(
            String token
    ) {

        return redisRepository
                .adquirirLock(
                        token,
                        LOCK_TTL
                );
    }

    private List<NomeSugestaoCacheEntry>
    carregarNomesAtivos() {

        return repository
                .findByStatusOrderByNomeAsc(
                        StatusNomeSugestao.ATIVO
                )
                .stream()
                .map(
                        NomeSugestaoCacheEntry::from
                )
                .toList();
    }

    private void reconciliarAlteracoes(
            String versao,
            Instant inicio
    ) {

        repository
                .buscarAlteradosDesde(
                        inicio
                )
                .stream()
                .map(
                        NomeSugestaoCacheEntry::from
                )
                .forEach(
                        atual ->
                                redisRepository
                                        .sincronizar(
                                                versao,
                                                null,
                                                atual
                                        )
                );
    }

    private void expirarVersaoAnterior(
            String anterior,
            String atual
    ) {

        if (
                anterior == null
                        || anterior.isBlank()
                        || anterior.equals(
                        atual
                )
        ) {
            return;
        }

        redisRepository
                .expirarVersao(
                        anterior,
                        VERSAO_ANTIGA_TTL
                );
    }

    private String gerarVersao() {

        return Instant
                .now()
                .toEpochMilli()
                + "-"
                + UUID
                .randomUUID()
                .toString()
                .substring(
                        0,
                        8
                );
    }

    private void excluirNovaVersaoSilenciosamente(
            String versao
    ) {

        if (versao == null) {
            return;
        }

        try {

            redisRepository
                    .excluirVersao(
                            versao
                    );

        } catch (
                RuntimeException ex
        ) {

            log.debug(
                    "Falha ao excluir versão "
                            + "Redis incompleta {}.",
                    versao,
                    ex
            );
        }
    }

    private void liberarLockSilenciosamente(
            String token
    ) {

        try {

            redisRepository
                    .liberarLock(
                            token
                    );

        } catch (
                RuntimeException ex
        ) {

            log.debug(
                    "Falha ao liberar lock "
                            + "de reconstrução.",
                    ex
            );
        }
    }
}
