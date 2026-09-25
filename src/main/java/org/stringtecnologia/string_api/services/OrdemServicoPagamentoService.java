package org.stringtecnologia.string_api.services;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.ordemServico.pagamento.OrdemServicoPagamentoCreateDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.pagamento.OrdemServicoPagamentoDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.pagamento.OrdemServicoPagamentoResumoDTO;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.model.entities.OrdemServicoPagamento;
import org.stringtecnologia.string_api.model.enums.FormaPagamento;
import org.stringtecnologia.string_api.model.enums.StatusPagamento;
import org.stringtecnologia.string_api.repository.OrdemServicoPagamentoRepository;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemServicoPagamentoService {

    private static final BigDecimal ZERO =
            BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );

    private final OrdemServicoPagamentoRepository pagamentoRepository;

    private final OrdemServicoRepository ordemServicoRepository;

    @Transactional
    public OrdemServicoPagamentoDTO adicionarPagamento(
            Long ordemServicoId,
            OrdemServicoPagamentoCreateDTO dto
    ) {

        OrdemServico ordemServico =
                ordemServicoRepository
                        .buscarPorIdParaAtualizacao(
                                ordemServicoId
                        )
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Ordem de serviço não encontrada"
                                )
                        );

        BigDecimal valorOrdemServico =
                obterValorOrdemServico(
                        ordemServico
                );

        if (valorOrdemServico.compareTo(ZERO) <= 0) {
            throw new IllegalStateException(
                    "A ordem de serviço não possui valor definido"
            );
        }

        BigDecimal totalPago =
                valorSeguro(
                        pagamentoRepository.somarPagamentos(
                                ordemServicoId,
                                StatusPagamento.ATIVO
                        )
                );

        BigDecimal saldo =
                valorOrdemServico.subtract(
                        totalPago
                );

        BigDecimal valorPagamento =
                normalizarValor(
                        dto.valor()
                );

        if (saldo.compareTo(ZERO) <= 0) {
            throw new IllegalStateException(
                    "A ordem de serviço já está quitada"
            );
        }

        if (valorPagamento.compareTo(saldo) > 0) {
            throw new IllegalArgumentException(
                    "O pagamento não pode ser maior que o saldo pendente"
            );
        }

        Integer parcelas =
                validarParcelas(
                        dto.formaPagamento(),
                        dto.parcelas()
                );

        OrdemServicoPagamento pagamento =
                new OrdemServicoPagamento();

        pagamento.setOrdemServico(
                ordemServico
        );

        pagamento.setFormaPagamento(
                dto.formaPagamento()
        );

        pagamento.setValor(
                valorPagamento
        );

        pagamento.setParcelas(
                parcelas
        );

        pagamento.setObservacao(
                normalizarTexto(
                        dto.observacao()
                )
        );

        pagamento.setStatus(
                StatusPagamento.ATIVO
        );

        OrdemServicoPagamento salvo =
                pagamentoRepository.save(
                        pagamento
                );

        return converterDTO(
                salvo
        );
    }

    @Transactional(readOnly = true)
    public OrdemServicoPagamentoResumoDTO buscarResumo(
            Long ordemServicoId
    ) {

        OrdemServico ordemServico =
                ordemServicoRepository
                        .findById(
                                ordemServicoId
                        )
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Ordem de serviço não encontrada"
                                )
                        );

        List<OrdemServicoPagamento> pagamentos =
                pagamentoRepository
                        .findByOrdemServicoOrdemServicoIdAndStatusOrderByDataPagamentoAsc(
                                ordemServicoId,
                                StatusPagamento.ATIVO
                        );

        BigDecimal valorOrdemServico =
                obterValorOrdemServico(
                        ordemServico
                );

        BigDecimal totalPago =
                pagamentos.stream()
                        .map(
                                OrdemServicoPagamento::getValor
                        )
                        .map(
                                this::valorSeguro
                        )
                        .reduce(
                                ZERO,
                                BigDecimal::add
                        );

        BigDecimal saldo =
                valorOrdemServico.subtract(
                        totalPago
                );

        if (saldo.compareTo(ZERO) < 0) {
            saldo = ZERO;
        }

        boolean quitado =
                valorOrdemServico.compareTo(ZERO) > 0
                        && saldo.compareTo(ZERO) == 0;

        List<OrdemServicoPagamentoDTO> pagamentosDTO =
                pagamentos.stream()
                        .map(
                                this::converterDTO
                        )
                        .toList();

        return new OrdemServicoPagamentoResumoDTO(
                ordemServicoId,
                valorOrdemServico,
                totalPago,
                saldo,
                quitado,
                pagamentosDTO
        );
    }

    @Transactional
    public void cancelarPagamento(
            Long ordemServicoId,
            Long pagamentoId
    ) {

        OrdemServicoPagamento pagamento =
                pagamentoRepository
                        .findByOrdemServicoPagamentoIdAndOrdemServicoOrdemServicoId(
                                pagamentoId,
                                ordemServicoId
                        )
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Pagamento não encontrado para esta ordem de serviço"
                                )
                        );

        if (pagamento.getStatus()
                == StatusPagamento.CANCELADO) {

            throw new IllegalStateException(
                    "O pagamento já está cancelado"
            );
        }

        pagamento.cancelar();
    }

    private BigDecimal obterValorOrdemServico(
            OrdemServico ordemServico
    ) {

        if (ordemServico.getValorFinal() != null) {
            return normalizarValor(
                    ordemServico.getValorFinal()
            );
        }

        if (ordemServico.getValorOrcamento() != null) {
            return normalizarValor(
                    ordemServico.getValorOrcamento()
            );
        }

        return ZERO;
    }

    private Integer validarParcelas(
            FormaPagamento formaPagamento,
            Integer parcelasInformadas
    ) {

        if (formaPagamento != FormaPagamento.CARTAO_CREDITO) {
            return 1;
        }

        if (parcelasInformadas == null) {
            return 1;
        }

        if (parcelasInformadas < 1) {
            throw new IllegalArgumentException(
                    "A quantidade de parcelas deve ser maior que zero"
            );
        }

        return parcelasInformadas;
    }

    private BigDecimal normalizarValor(
            BigDecimal valor
    ) {

        if (valor == null) {
            return ZERO;
        }

        return valor.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal valorSeguro(
            BigDecimal valor
    ) {

        return valor != null
                ? normalizarValor(valor)
                : ZERO;
    }

    private String normalizarTexto(
            String valor
    ) {

        if (valor == null) {
            return null;
        }

        String texto =
                valor.trim();

        return texto.isEmpty()
                ? null
                : texto;
    }

    private OrdemServicoPagamentoDTO converterDTO(
            OrdemServicoPagamento pagamento
    ) {

        return new OrdemServicoPagamentoDTO(
                pagamento.getOrdemServicoPagamentoId(),
                pagamento
                        .getOrdemServico()
                        .getOrdemServicoId(),
                pagamento.getFormaPagamento(),
                pagamento.getValor(),
                pagamento.getParcelas(),
                pagamento.getObservacao(),
                pagamento.getStatus(),
                pagamento.getDataPagamento(),
                pagamento.getDataCancelamento()
        );
    }
}