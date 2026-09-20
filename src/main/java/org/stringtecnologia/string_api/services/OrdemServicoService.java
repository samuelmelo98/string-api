package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoResponseDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.DominioSistema;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.model.enums.StatusOrdemServico;
import org.stringtecnologia.string_api.repository.AparelhoRepository;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;
import org.stringtecnologia.string_api.repository.SistemaDominioRepository;
import org.stringtecnologia.string_api.util.CategoriaDominio;

import java.time.Year;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private static final Set<String> STATUS_FINAIS =
            Set.of(
                    StatusOrdemServico.ENTREGUE.getCodigo(),
                    StatusOrdemServico.CANCELADA.getCodigo()
            );

    private final OrdemServicoRepository ordemServicoRepository;
    private final AparelhoRepository aparelhoRepository;
    private final SistemaDominioRepository dominioSistemaRepository;

    @Transactional
    public OrdemServicoResponseDTO abrir(Long aparelhoId) {

        /*
         * Se já existe uma OS ainda ativa para esse aparelho,
         * devolvemos a mesma em vez de criar outra.
         */
        var existente =
                ordemServicoRepository
                        .findFirstByAparelhoAparelhoIdAndStatusCodigoNotInOrderByDataAberturaDesc(
                                aparelhoId,
                                STATUS_FINAIS
                        );

        if (existente.isPresent()) {
            return toResponse(existente.get());
        }

        Aparelho aparelho = aparelhoRepository
                .findById(aparelhoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Aparelho não encontrado: " + aparelhoId
                        )
                );

        DominioSistema statusAberta =
                dominioSistemaRepository
                        .findByCategoriaAndCodigoAndAtivoTrue(
                                CategoriaDominio
                                        .STATUS_ORDEM_SERVICO
                                        .getCodigo(),
                                StatusOrdemServico
                                        .ABERTA
                                        .getCodigo()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Domínio STATUS_ORDEM_SERVICO/ABERTA não encontrado."
                                )
                        );

        Long sequencial =
                ordemServicoRepository.proximoNumero();

        OrdemServico ordemServico =
                new OrdemServico();

        ordemServico.setNumero(
                gerarNumero(sequencial)
        );

        ordemServico.setCliente(
                aparelho.getCliente()
        );

        ordemServico.setAparelho(
                aparelho
        );

        ordemServico.setStatus(
                statusAberta
        );

        /*
         * Guardamos uma cópia do problema informado no
         * momento da abertura da OS.
         */
        ordemServico.setDefeitoRelatado(
                aparelho.getDefeito()
        );

        ordemServico.setObservacao(
                aparelho.getObservacao()
        );

        OrdemServico salva =
                ordemServicoRepository
                        .saveAndFlush(
                                ordemServico
                        );

        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(
            Long ordemServicoId
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

        return toResponse(ordem);
    }

    private String gerarNumero(
            Long sequencial
    ) {

        int ano =
                Year.now().getValue();

        return "OS-%d-%06d".formatted(
                ano,
                sequencial
        );
    }

    private OrdemServicoResponseDTO toResponse(
            OrdemServico ordem
    ) {

        Aparelho aparelho =
                ordem.getAparelho();

        return new OrdemServicoResponseDTO(
                ordem.getOrdemServicoId(),
                ordem.getNumero(),

                ordem.getCliente().getClienteId(),
                ordem.getCliente().getNome(),

                aparelho.getAparelhoId(),

                aparelho.getMarca() != null
                        ? aparelho.getMarca().getNome()
                        : null,

                aparelho.getModelo(),
                aparelho.getModeloComercial(),
                aparelho.getNumeroSerie(),

                ordem.getStatus() != null
                        ? ordem.getStatus().getCodigo()
                        : null,

                ordem.getStatus() != null
                        ? ordem.getStatus().getDescricao()
                        : null,

                ordem.getDefeitoRelatado(),
                ordem.getDiagnostico(),
                ordem.getSolucao(),
                ordem.getObservacao(),

                ordem.getValorOrcamento(),
                ordem.getValorFinal(),

                ordem.getDataAbertura(),
                ordem.getDataAtualizacao(),
                ordem.getDataAprovacao(),
                ordem.getDataInicioServico(),
                ordem.getDataConclusao(),
                ordem.getDataEntrega()
        );
    }
}