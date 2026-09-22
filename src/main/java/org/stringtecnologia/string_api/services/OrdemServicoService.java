package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.ordemServico.ConcluirOrdemServicoRequestDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoResponseDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.DominioSistema;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.model.entities.User;
import org.stringtecnologia.string_api.model.enums.StatusOrdemServico;
import org.stringtecnologia.string_api.repository.AparelhoRepository;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;
import org.stringtecnologia.string_api.repository.UserRepository;
import org.stringtecnologia.string_api.util.CategoriaDominio;
import org.stringtecnologia.string_api.util.StatusAparelho;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {


    private final OrdemServicoRepository ordemServicoRepository;

    private final AparelhoRepository aparelhoRepository;

    private final DominioSistemaService dominioSistemaService;

    private final UserRepository userRepository;


    /**
     * Abre uma nova ordem de servico para o aparelho.
     *
     * Se existir uma OS ainda ativa, retorna a mesma.
     */
    @Transactional
    public OrdemServicoResponseDTO abrir(
            Long aparelhoId
    ) {

        var existente =
                ordemServicoRepository
                        .findFirstByAparelhoAparelhoIdOrderByDataAberturaDesc(
                                aparelhoId
                        );

        if (existente.isPresent()) {

            throw new IllegalStateException(
                    "Este aparelho já possui uma ordem de serviço. "
                            + "Para um novo atendimento, cadastre o equipamento novamente."
            );
        }

        Aparelho aparelho =
                aparelhoRepository
                        .findById(aparelhoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Aparelho não encontrado: "
                                                + aparelhoId
                                )
                        );

        aparelho.setStatusAparelho(
                dominioSistemaService.buscar(
                        StatusAparelho.PARA_ORCAMENTO
                )
        );

        DominioSistema statusAberta =
                buscarStatus(
                        StatusOrdemServico.ABERTA
                );

        Long sequencial =
                ordemServicoRepository
                        .proximoNumero();

        OrdemServico ordemServico =
                new OrdemServico();

        ordemServico.setNumero(
                gerarNumero(
                        sequencial
                )
        );

        /*
         * Token público e imprevisível utilizado na consulta
         * externa da ordem de serviço via QR Code.
         *
         * O cliente ainda será validado pelos 2 últimos
         * dígitos do CPF antes de visualizar a OS.
         */
        ordemServico.setConsultaToken(
                UUID.randomUUID().toString()
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
         * Snapshot do defeito no momento
         * da abertura da ordem.
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

        return toResponse(
                salva
        );
    }


    /**
     * Consulta uma ordem pelo ID.
     */
    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(
            Long ordemServicoId
    ) {

        OrdemServico ordem =
                buscarEntidade(
                        ordemServicoId
                );

        return toResponse(
                ordem
        );
    }


    /**
     * ABERTA -> EM_ANALISE
     */
    @Transactional
    public OrdemServicoResponseDTO iniciarAnalise(
            Long ordemServicoId
    ) {

        OrdemServico ordem =
                buscarEntidade(
                        ordemServicoId
                );

        validarStatusAtual(
                ordem,
                StatusOrdemServico.ABERTA
        );

        DominioSistema novoStatus =
                buscarStatus(
                        StatusOrdemServico.EM_ANALISE
                );

        ordem.setStatus(
                novoStatus
        );

        /*
         * Nao e obrigatorio chamar save(), pois a entidade
         * foi carregada dentro da transacao e esta gerenciada
         * pelo Hibernate.
         *
         * Mantemos somente o retorno do estado atualizado.
         */
        return toResponse(
                ordem
        );
    }


    /**
     * Busca internamente uma OS.
     */
    private OrdemServico buscarEntidade(
            Long ordemServicoId
    ) {

        if (ordemServicoId == null
                || ordemServicoId <= 0) {

            throw new IllegalArgumentException(
                    "Identificador da ordem de serviço inválido."
            );
        }

        return ordemServicoRepository
                .findById(
                        ordemServicoId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ordem de serviço não encontrada: "
                                        + ordemServicoId
                        )
                );
    }


    /**
     * Busca um status da OS na tabela de dominio.
     *
     * Toda consulta de dominio fica centralizada no
     * DominioSistemaService.
     */
    private DominioSistema buscarStatus(
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


    /**
     * Garante que uma transicao somente aconteca
     * a partir do estado esperado.
     */
    private void validarStatusAtual(
            OrdemServico ordem,
            StatusOrdemServico statusEsperado
    ) {

        if (ordem.getStatus() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui status."
            );
        }

        String codigoAtual =
                ordem.getStatus()
                        .getCodigo();

        if (!statusEsperado
                .getCodigo()
                .equals(codigoAtual)) {

            throw new IllegalStateException(
                    "Operação não permitida para a ordem "
                            + ordem.getNumero()
                            + ". Status atual: "
                            + codigoAtual
                            + ". Status esperado: "
                            + statusEsperado.getCodigo()
            );
        }
    }


    /**
     * Gera o numero comercial da OS.
     *
     * Ex:
     * OS-2026-000001
     */
    private String gerarNumero(
            Long sequencial
    ) {

        int ano =
                Year.now()
                        .getValue();

        return "OS-%d-%06d"
                .formatted(
                        ano,
                        sequencial
                );
    }


    /**
     * Converte a entidade para o DTO utilizado
     * pelo frontend.
     */
    private OrdemServicoResponseDTO toResponse(
            OrdemServico ordem
    ) {

        Aparelho aparelho =
                ordem.getAparelho();

        return new OrdemServicoResponseDTO(

                ordem.getOrdemServicoId(),

                ordem.getNumero(),

                ordem.getCliente()
                        .getClienteId(),

                ordem.getCliente()
                        .getNome(),

                aparelho.getAparelhoId(),

                aparelho.getMarca() != null
                        ? aparelho
                        .getMarca()
                        .getNome()
                        : null,

                aparelho.getModelo(),

                aparelho.getModeloComercial(),

                aparelho.getNumeroSerie(),

                ordem.getStatus() != null
                        ? ordem
                        .getStatus()
                        .getCodigo()
                        : null,

                ordem.getStatus() != null
                        ? ordem
                        .getStatus()
                        .getDescricao()
                        : null,

                ordem.getDefeitoRelatado(),

                ordem.getDiagnostico(),

                ordem.getSolucao(),

                ordem.getObservacao(),

                ordem.getValorOrcamento(),

                ordem.getValorFinal(),
                ordem.getTecnicoResponsavel() != null
                        ? ordem.getTecnicoResponsavel().getId()
                        : null,

                ordem.getTecnicoResponsavel() != null
                        ? ordem.getTecnicoResponsavel().getNome()
                        : null,

                ordem.getDataAtribuicaoTecnico(),

                ordem.getDataAbertura(),

                ordem.getDataAtualizacao(),

                ordem.getDataAprovacao(),

                ordem.getDataInicioServico(),

                ordem.getDataConclusao(),

                ordem.getDataEntrega()
        );
    }

    @Transactional
    public OrdemServicoResponseDTO atribuirTecnico(
            Long ordemServicoId,
            Long tecnicoId
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

        if (ordem.getStatus() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui status."
            );
        }

        if (!StatusOrdemServico
                .APROVADA
                .getCodigo()
                .equals(
                        ordem.getStatus().getCodigo()
                )) {

            throw new IllegalStateException(
                    "Só é possível atribuir técnico "
                            + "a uma ordem aprovada."
            );
        }

        User tecnico =
                userRepository
                        .findById(tecnicoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Técnico não encontrado: "
                                                + tecnicoId
                                )
                        );

        if (!Boolean.TRUE.equals(
                tecnico.getAtivo()
        )) {

            throw new IllegalStateException(
                    "O usuário selecionado está inativo."
            );
        }

        ordem.setTecnicoResponsavel(
                tecnico
        );

        ordem.setDataAtribuicaoTecnico(
                LocalDateTime.now()
        );

        return toResponse(
                ordem
        );
    }

    @Transactional
    public OrdemServicoResponseDTO iniciarExecucao(
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

        if (ordem.getStatus() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui status."
            );
        }

        if (!StatusOrdemServico
                .APROVADA
                .getCodigo()
                .equals(
                        ordem.getStatus().getCodigo()
                )) {

            throw new IllegalStateException(
                    "Só é possível iniciar o serviço "
                            + "de uma ordem aprovada."
            );
        }

        if (ordem.getTecnicoResponsavel() == null) {

            throw new IllegalStateException(
                    "É necessário atribuir um técnico "
                            + "antes de iniciar o serviço."
            );
        }

        ordem.setStatus(
                buscarStatus(
                        StatusOrdemServico.EM_EXECUCAO
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
                        StatusAparelho.EM_MANUTENCAO
                )
        );

        ordem.setDataInicioServico(
                LocalDateTime.now()
        );

        return toResponse(
                ordem
        );
    }

    @Transactional
    public OrdemServicoResponseDTO concluir(
            Long ordemServicoId,
            ConcluirOrdemServicoRequestDTO request
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

        if (ordem.getStatus() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui status."
            );
        }

        if (!StatusOrdemServico
                .EM_EXECUCAO
                .getCodigo()
                .equals(
                        ordem.getStatus().getCodigo()
                )) {

            throw new IllegalStateException(
                    "Só é possível concluir uma ordem em execução."
            );
        }

        if (ordem.getTecnicoResponsavel() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui técnico responsável."
            );
        }

        ordem.setSolucao(
                request.solucao().trim()
        );

        ordem.setValorFinal(
                request.valorFinal()
        );

        if (
                request.observacao() != null &&
                        !request.observacao().isBlank()
        ) {

            ordem.setObservacao(
                    request.observacao().trim()
            );
        }

        ordem.setDataConclusao(
                LocalDateTime.now()
        );

        ordem.setStatus(
                buscarStatus(
                        StatusOrdemServico.CONCLUIDA
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
                        StatusAparelho.PRONTO
                )
        );

        return toResponse(
                ordem
        );
    }

    @Transactional
    public OrdemServicoResponseDTO entregar(
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

        if (ordem.getStatus() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui status."
            );
        }

        if (!StatusOrdemServico
                .CONCLUIDA
                .getCodigo()
                .equals(
                        ordem.getStatus()
                                .getCodigo()
                )) {

            throw new IllegalStateException(
                    "Só é possível registrar a entrega "
                            + "de uma ordem concluída."
            );
        }

        if (ordem.getDataConclusao() == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui data de conclusão."
            );
        }

        Aparelho aparelho =
                ordem.getAparelho();

        if (aparelho == null) {

            throw new IllegalStateException(
                    "A ordem de serviço não possui aparelho vinculado."
            );
        }

        LocalDateTime agora =
                LocalDateTime.now();


        /*
         * FINALIZA A ORDEM DE SERVICO
         */
        ordem.setStatus(
                buscarStatus(
                        StatusOrdemServico.ENTREGUE
                )
        );

        ordem.setDataEntrega(
                agora
        );


        /*
         * FINALIZA O APARELHO
         */
        aparelho.setStatusAparelho(
                dominioSistemaService.buscar(
                        StatusAparelho.ENTREGUE
                )
        );

        aparelho.setDataSaida(
                agora
        );


        return toResponse(
                ordem
        );
    }

    @Transactional(readOnly = true)
    public Optional<OrdemServicoResponseDTO> buscarUltimaPorAparelho(
            Long aparelhoId
    ) {

        return ordemServicoRepository
                .findFirstByAparelhoAparelhoIdOrderByDataAberturaDesc(
                        aparelhoId
                )
                .map(this::toResponse);
    }


}
