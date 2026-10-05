package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.stringtecnologia.string_api.model.NomeSugestao;
import org.stringtecnologia.string_api.model.dto.nome.CriarNomeSugestaoRequest;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoAlteradoEvent;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoCacheEntry;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoResponse;
import org.stringtecnologia.string_api.model.enums.OrigemNomeSugestao;
import org.stringtecnologia.string_api.repository.NomeSugestaoRepository;
import org.stringtecnologia.string_api.util.NomeNormalizer;


@Service
@RequiredArgsConstructor
public class NomeSugestaoService {

    private final NomeSugestaoRepository
            repository;

    private final ApplicationEventPublisher
            eventPublisher;

    @Transactional
    public NomeSugestaoResponse criar(
            CriarNomeSugestaoRequest request
    ) {

        String nome =
                prepararNome(
                        request.nome()
                );

        String normalizado =
                NomeNormalizer
                        .normalizar(
                                nome
                        );

        validarDuplicidade(
                request,
                normalizado
        );

        OrigemNomeSugestao origem =
                obterOrigem(
                        request
                );

        NomeSugestao entidade =
                NomeSugestao.criar(
                        nome,
                        normalizado,
                        request.tipo(),
                        origem
                );

        salvar(
                entidade
        );

        publicarAlteracao(
                null,
                entidade
        );

        return NomeSugestaoResponse
                .from(
                        entidade
                );
    }

    @Transactional
    public NomeSugestaoResponse ativar(
            Long id
    ) {

        NomeSugestao entidade =
                buscar(
                        id
                );

        NomeSugestaoCacheEntry anterior =
                NomeSugestaoCacheEntry
                        .from(
                                entidade
                        );

        entidade.ativar();

        publicarAlteracao(
                anterior,
                entidade
        );

        return NomeSugestaoResponse
                .from(
                        entidade
                );
    }

    @Transactional
    public NomeSugestaoResponse inativar(
            Long id
    ) {

        NomeSugestao entidade =
                buscar(
                        id
                );

        NomeSugestaoCacheEntry anterior =
                NomeSugestaoCacheEntry
                        .from(
                                entidade
                        );

        entidade.inativar();

        publicarAlteracao(
                anterior,
                entidade
        );

        return NomeSugestaoResponse
                .from(
                        entidade
                );
    }

    private String prepararNome(
            String nome
    ) {

        return nome
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                );
    }

    private void validarDuplicidade(
            CriarNomeSugestaoRequest request,
            String normalizado
    ) {

        repository
                .findByTipoAndNomeNormalizado(
                        request.tipo(),
                        normalizado
                )
                .ifPresent(
                        existente -> {

                            throw new ResponseStatusException(
                                    HttpStatus.CONFLICT,
                                    "Nome já cadastrado "
                                            + "para esse tipo."
                            );
                        }
                );
    }

    private OrigemNomeSugestao obterOrigem(
            CriarNomeSugestaoRequest request
    ) {

        if (request.origem() == null) {

            return OrigemNomeSugestao.MANUAL;
        }

        return request.origem();
    }

    private void salvar(
            NomeSugestao entidade
    ) {

        try {

            repository
                    .saveAndFlush(
                            entidade
                    );

        } catch (
                DataIntegrityViolationException ex
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nome já cadastrado "
                            + "para esse tipo.",
                    ex
            );
        }
    }

    private NomeSugestao buscar(
            Long id
    ) {

        return repository
                .findById(
                        id
                )
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Nome de sugestão "
                                                + "não encontrado."
                                )
                );
    }

    private void publicarAlteracao(
            NomeSugestaoCacheEntry anterior,
            NomeSugestao atual
    ) {

        eventPublisher
                .publishEvent(
                        new NomeSugestaoAlteradoEvent(
                                anterior,
                                NomeSugestaoCacheEntry
                                        .from(
                                                atual
                                        )
                        )
                );
    }
}