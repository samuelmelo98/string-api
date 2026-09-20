package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoOrcamentoRequestDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoOrcamentoResponseDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.DominioSistema;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.model.entities.OrdemServicoOrcamento;
import org.stringtecnologia.string_api.model.enums.StatusOrcamento;
import org.stringtecnologia.string_api.model.enums.StatusOrdemServico;
import org.stringtecnologia.string_api.repository.OrdemServicoOrcamentoRepository;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;
import org.stringtecnologia.string_api.util.CategoriaDominio;
import org.stringtecnologia.string_api.util.StatusAparelho;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdemServicoOrcamentoService {

    private final OrdemServicoRepository ordemServicoRepository;

    private final OrdemServicoOrcamentoRepository ordemServicoOrcamentoRepository;

    private final DominioSistemaService dominioSistemaService;


    @Transactional
    public OrdemServicoOrcamentoResponseDTO salvarRascunho(
            Long ordemServicoId,
            OrdemServicoOrcamentoRequestDTO request
    ) {

        OrdemServico ordem =
                ordemServicoRepository
                        .findById(ordemServicoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Ordem de serviço não encontrada: "
                                                + ordemServicoId
                                )
                        );

        validarOrdemEmAnalise(ordem);

        BigDecimal valorMaoObra =
                valorOuZero(
                        request.valorMaoObra()
                );

        BigDecimal valorPecas =
                valorOuZero(
                        request.valorPecas()
                );

        BigDecimal desconto =
                valorOuZero(
                        request.desconto()
                );

        BigDecimal valorTotal =
                valorMaoObra
                        .add(valorPecas)
                        .subtract(desconto);

        if (valorTotal.signum() < 0) {

            throw new IllegalArgumentException(
                    "O desconto não pode ser maior que o total do orçamento."
            );
        }


        /*
         * Se a última versão ainda for RASCUNHO,
         * atualizamos o mesmo orçamento.
         *
         * Caso contrário, criamos uma nova versão.
         */
        OrdemServicoOrcamento orcamento =
                ordemServicoOrcamentoRepository
                        .findFirstByOrdemServicoOrdemServicoIdOrderByVersaoDesc(
                                ordemServicoId
                        )
                        .filter(item ->
                                item.getStatus()
                                        == StatusOrcamento.RASCUNHO
                        )
                        .orElseGet(() -> {

                            OrdemServicoOrcamento novo =
                                    new OrdemServicoOrcamento();

                            novo.setOrdemServico(
                                    ordem
                            );

                            novo.setVersao(
                                    obterProximaVersao(
                                            ordemServicoId
                                    )
                            );

                            novo.setStatus(
                                    StatusOrcamento.RASCUNHO
                            );

                            return novo;
                        });


        orcamento.setServicoProposto(
                request.servicoProposto()
        );

        orcamento.setValorMaoObra(
                valorMaoObra
        );

        orcamento.setValorPecas(
                valorPecas
        );

        orcamento.setDesconto(
                desconto
        );

        orcamento.setValorTotal(
                valorTotal
        );

        orcamento.setObservacao(
                request.observacao()
        );


        OrdemServicoOrcamento salvo =
                ordemServicoOrcamentoRepository
                        .saveAndFlush(
                                orcamento
                        );

        return toResponse(
                salvo
        );
    }


    private void validarOrdemEmAnalise(
            OrdemServico ordem
    ) {

        if (ordem.getStatus() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui status."
            );
        }

        String statusAtual =
                ordem.getStatus()
                        .getCodigo();

        if (!StatusOrdemServico
                .EM_ANALISE
                .getCodigo()
                .equals(statusAtual)) {

            throw new IllegalStateException(
                    "Só é possível criar orçamento para uma ordem em análise."
            );
        }
    }


    private Integer obterProximaVersao(
            Long ordemServicoId
    ) {

        return ordemServicoOrcamentoRepository
                .findFirstByOrdemServicoOrdemServicoIdOrderByVersaoDesc(
                        ordemServicoId
                )
                .map(orcamento ->
                        orcamento.getVersao() + 1
                )
                .orElse(1);
    }


    private BigDecimal valorOuZero(
            BigDecimal valor
    ) {

        return valor != null
                ? valor
                : BigDecimal.ZERO;
    }


    private OrdemServicoOrcamentoResponseDTO toResponse(
            OrdemServicoOrcamento orcamento
    ) {

        return new OrdemServicoOrcamentoResponseDTO(

                orcamento.getOrdemServicoOrcamentoId(),

                orcamento
                        .getOrdemServico()
                        .getOrdemServicoId(),

                orcamento.getVersao(),

                orcamento.getServicoProposto(),

                orcamento.getValorMaoObra(),

                orcamento.getValorPecas(),

                orcamento.getDesconto(),

                orcamento.getValorTotal(),

                orcamento.getObservacao(),

                orcamento.getStatus(),

                orcamento.getDataCriacao(),

                orcamento.getDataAtualizacao(),

                orcamento.getDataEnvio(),

                orcamento.getDataAprovacao(),

                orcamento.getDataReprovacao()
        );
    }

    @Transactional(readOnly = true)
    public Optional<OrdemServicoOrcamentoResponseDTO> buscarAtual(
            Long ordemServicoId
    ) {

        /*
         * Primeiro validamos se a OS existe.
         */
        if (!ordemServicoRepository.existsById(ordemServicoId)) {

            throw new IllegalArgumentException(
                    "Ordem de serviço não encontrada: "
                            + ordemServicoId
            );
        }

        return ordemServicoOrcamentoRepository
                .findFirstByOrdemServicoOrdemServicoIdOrderByVersaoDesc(
                        ordemServicoId
                )
                .map(this::toResponse);
    }

    @Transactional
    public OrdemServicoOrcamentoResponseDTO enviarParaAprovacao(
            Long ordemServicoId,
            Long orcamentoId
    ) {

        OrdemServico ordem =
                ordemServicoRepository
                        .findById(ordemServicoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Ordem de serviço não encontrada: "
                                                + ordemServicoId
                                )
                        );

        validarOrdemEmAnalise(
                ordem
        );

        OrdemServicoOrcamento orcamento =
                ordemServicoOrcamentoRepository
                        .findById(orcamentoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Orçamento não encontrado: "
                                                + orcamentoId
                                )
                        );

        /*
         * Impede enviar orçamento de outra OS.
         */
        if (!ordemServicoId.equals(
                orcamento
                        .getOrdemServico()
                        .getOrdemServicoId()
        )) {

            throw new IllegalArgumentException(
                    "O orçamento não pertence à ordem de serviço informada."
            );
        }

        /*
         * Somente rascunho pode ser enviado.
         */
        if (orcamento.getStatus()
                != StatusOrcamento.RASCUNHO) {

            throw new IllegalStateException(
                    "Somente um orçamento em rascunho pode ser enviado para aprovação."
            );
        }

        if (orcamento.getValorTotal() == null
                || orcamento.getValorTotal().signum() < 0) {

            throw new IllegalStateException(
                    "O orçamento possui valor total inválido."
            );
        }

        orcamento.setStatus(
                StatusOrcamento.AGUARDANDO_APROVACAO
        );

        orcamento.setDataEnvio(
                LocalDateTime.now()
        );

        DominioSistema statusAguardando =
                dominioSistemaService
                        .buscarDominio(
                                CategoriaDominio
                                        .STATUS_ORDEM_SERVICO
                                        .getCodigo(),

                                StatusOrdemServico
                                        .AGUARDANDO_APROVACAO
                                        .getCodigo()
                        );

        ordem.setStatus(
                statusAguardando
        );

        Aparelho aparelho =
                ordem.getAparelho();

        if (aparelho == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui aparelho vinculado."
            );
        }

        aparelho.setStatusAparelho(
                dominioSistemaService.buscar(
                        StatusAparelho.PARA_ORCAMENTO
                )
        );

        ordemServicoOrcamentoRepository
                .saveAndFlush(
                        orcamento
                );

        return toResponse(
                orcamento
        );
    }

    @Transactional
    public OrdemServicoOrcamentoResponseDTO aprovar(
            Long ordemServicoId,
            Long orcamentoId
    ) {

        OrdemServico ordem =
                ordemServicoRepository
                        .findById(ordemServicoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Ordem de serviço não encontrada: "
                                                + ordemServicoId
                                )
                        );

        validarAguardandoAprovacao(ordem);

        OrdemServicoOrcamento orcamento =
                buscarOrcamentoDaOrdem(
                        ordemServicoId,
                        orcamentoId
                );

        if (orcamento.getStatus()
                != StatusOrcamento.AGUARDANDO_APROVACAO) {

            throw new IllegalStateException(
                    "O orçamento não está aguardando aprovação."
            );
        }

        LocalDateTime agora =
                LocalDateTime.now();

        orcamento.setStatus(
                StatusOrcamento.APROVADO
        );

        orcamento.setDataAprovacao(
                agora
        );

        ordem.setStatus(
                buscarStatusOS(
                        StatusOrdemServico.APROVADA
                )
        );

        Aparelho aparelho =
                ordem.getAparelho();

        if (aparelho == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui aparelho vinculado."
            );
        }

        aparelho.setStatusAparelho(
                dominioSistemaService.buscar(
                        StatusAparelho.AUTORIZADO
                )
        );

        ordem.setValorOrcamento(
                orcamento.getValorTotal()
        );

        ordem.setDataAprovacao(
                agora
        );

        ordemServicoOrcamentoRepository
                .saveAndFlush(
                        orcamento
                );

        return toResponse(
                orcamento
        );
    }

    @Transactional
    public OrdemServicoOrcamentoResponseDTO reprovar(
            Long ordemServicoId,
            Long orcamentoId
    ) {

        OrdemServico ordem =
                ordemServicoRepository
                        .findById(ordemServicoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Ordem de serviço não encontrada: "
                                                + ordemServicoId
                                )
                        );

        validarAguardandoAprovacao(
                ordem
        );

        OrdemServicoOrcamento orcamento =
                buscarOrcamentoDaOrdem(
                        ordemServicoId,
                        orcamentoId
                );

        if (orcamento.getStatus()
                != StatusOrcamento.AGUARDANDO_APROVACAO) {

            throw new IllegalStateException(
                    "O orçamento não está aguardando aprovação."
            );
        }

        orcamento.setStatus(
                StatusOrcamento.REPROVADO
        );

        orcamento.setDataReprovacao(
                LocalDateTime.now()
        );

        ordem.setStatus(
                buscarStatusOS(
                        StatusOrdemServico.REPROVADA
                )
        );

        Aparelho aparelho =
                ordem.getAparelho();

        if (aparelho == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui aparelho vinculado."
            );
        }

        aparelho.setStatusAparelho(
                dominioSistemaService.buscar(
                        StatusAparelho.NAO_AUTORIZADO
                )
        );

        ordemServicoOrcamentoRepository
                .saveAndFlush(
                        orcamento
                );

        return toResponse(
                orcamento
        );
    }

    private OrdemServicoOrcamento buscarOrcamentoDaOrdem(
            Long ordemServicoId,
            Long orcamentoId
    ) {

        OrdemServicoOrcamento orcamento =
                ordemServicoOrcamentoRepository
                        .findById(orcamentoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Orçamento não encontrado: "
                                                + orcamentoId
                                )
                        );

        if (!ordemServicoId.equals(
                orcamento
                        .getOrdemServico()
                        .getOrdemServicoId()
        )) {

            throw new IllegalArgumentException(
                    "O orçamento não pertence à ordem de serviço informada."
            );
        }

        return orcamento;
    }


    private void validarAguardandoAprovacao(
            OrdemServico ordem
    ) {

        if (ordem.getStatus() == null) {
            throw new IllegalStateException(
                    "A ordem de serviço não possui status."
            );
        }

        if (!StatusOrdemServico
                .AGUARDANDO_APROVACAO
                .getCodigo()
                .equals(
                        ordem.getStatus().getCodigo()
                )) {

            throw new IllegalStateException(
                    "A ordem de serviço não está aguardando aprovação."
            );
        }
    }


    private DominioSistema buscarStatusOS(
            StatusOrdemServico status
    ) {

        return dominioSistemaService
                .buscarDominio(
                        CategoriaDominio
                                .STATUS_ORDEM_SERVICO
                                .getCodigo(),
                        status.getCodigo()
                );
    }
}
