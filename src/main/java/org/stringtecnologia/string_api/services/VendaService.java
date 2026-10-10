package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.venda.*;
import org.stringtecnologia.string_api.model.entities.Cliente;
import org.stringtecnologia.string_api.model.entities.Venda;
import org.stringtecnologia.string_api.model.entities.VendaItem;
import org.stringtecnologia.string_api.model.enums.StatusVenda;
import org.stringtecnologia.string_api.repository.ClienteRepository;
import org.stringtecnologia.string_api.repository.VendaRepository;
import org.stringtecnologia.string_api.util.exceptions.venda.OperacaoVendaInvalidaException;
import org.stringtecnologia.string_api.util.exceptions.venda.VendaNaoEncontradaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VendaService {

    private static final ZoneId ZONE_ID =
            ZoneId.of("America/Sao_Paulo");

    private static final int SCALE_MONETARIA = 2;

    private final VendaRepository vendaRepository;

    private final ClienteRepository clienteRepository;

    @Transactional
    public VendaDetalheDTO criar(
            VendaRequestDTO request
    ) {

        Venda venda = new Venda();

        venda.setNumero(
                gerarNumero()
        );

        venda.setDataVenda(Instant.now());

        venda.setStatus(
                StatusVenda.ABERTA
        );

        venda.setCliente(
                buscarCliente(
                        request.getClienteId()
                )
        );

        venda.setObservacao(
                normalizarTexto(
                        request.getObservacao()
                )
        );

        venda.setDesconto(
                monetario(
                        request.getDesconto()
                )
        );

        venda.setFormaPagamento(request.getFormaPagamento());

        adicionarItens(
                venda,
                request.getItens()
        );

        recalcular(venda);

        venda = vendaRepository.save(venda);

        return converterDetalhe(venda);

    }

    public Page<VendaResponseDTO> listar(
            Pageable pageable
    ) {

        return vendaRepository
                .findAll(pageable)
                .map(this::converterResponse);

    }

    public VendaDetalheDTO buscar(
            Long vendaId
    ) {

        Venda venda = buscarVenda(vendaId);

        return converterDetalhe(venda);

    }

    @Transactional
    public VendaDetalheDTO atualizar(
            Long vendaId,
            VendaRequestDTO request
    ) {

        Venda venda = buscarVenda(vendaId);

        validarVendaAberta(venda);

        venda.setCliente(
                buscarCliente(
                        request.getClienteId()
                )
        );

        venda.setObservacao(
                normalizarTexto(
                        request.getObservacao()
                )
        );

        venda.setDesconto(
                monetario(
                        request.getDesconto()
                )
        );

        venda.limparItens();

        adicionarItens(
                venda,
                request.getItens()
        );

        recalcular(venda);

        venda = vendaRepository.save(venda);

        return converterDetalhe(venda);

    }

    @Transactional
    public VendaDetalheDTO finalizar(
            Long vendaId
    ) {

        Venda venda = buscarVenda(vendaId);

        validarVendaAberta(venda);

        if (
                venda.getItens() == null
                        || venda.getItens().isEmpty()
        ) {

            throw new OperacaoVendaInvalidaException(
                    "Não é possível finalizar uma venda sem itens."
            );

        }

        recalcular(venda);

        venda.setStatus(
                StatusVenda.FINALIZADA
        );

        venda = vendaRepository.save(venda);

        return converterDetalhe(venda);

    }

    @Transactional
    public VendaDetalheDTO cancelar(
            Long vendaId
    ) {

        Venda venda = buscarVenda(vendaId);

        if (
                venda.getStatus()
                        == StatusVenda.CANCELADA
        ) {

            throw new OperacaoVendaInvalidaException(
                    "A venda já está cancelada."
            );

        }

        venda.setStatus(
                StatusVenda.CANCELADA
        );

        venda = vendaRepository.save(venda);

        return converterDetalhe(venda);

    }

    private Venda buscarVenda(
            Long vendaId
    ) {

        return vendaRepository
                .buscarDetalhe(vendaId)
                .orElseThrow(
                        () ->
                                new VendaNaoEncontradaException(
                                        vendaId
                                )
                );

    }

    private Cliente buscarCliente(
            Long clienteId
    ) {

        if (clienteId == null) {
            return null;
        }

        return clienteRepository
                .findById(clienteId)
                .orElseThrow(
                        () ->
                                new OperacaoVendaInvalidaException(
                                        "Cliente não encontrado. ID: "
                                                + clienteId
                                )
                );

    }

    private void validarVendaAberta(
            Venda venda
    ) {

        if (
                venda.getStatus()
                        != StatusVenda.ABERTA
        ) {

            throw new OperacaoVendaInvalidaException(
                    "A venda "
                            + venda.getNumero()
                            + " não está aberta para alteração."
            );

        }

    }

    private void adicionarItens(
            Venda venda,
            List<VendaItemRequestDTO> requests
    ) {

        if (
                requests == null
                        || requests.isEmpty()
        ) {

            throw new OperacaoVendaInvalidaException(
                    "A venda deve possuir pelo menos um item."
            );

        }

        for (
                VendaItemRequestDTO request :
                requests
        ) {

            VendaItem item =
                    criarItem(request);

            venda.adicionarItem(item);

        }

    }

    private VendaItem criarItem(
            VendaItemRequestDTO request
    ) {

        BigDecimal quantidade =
                quantidade(
                        request.getQuantidade()
                );

        BigDecimal valorUnitario =
                monetario(
                        request.getValorUnitario()
                );

        BigDecimal desconto =
                monetario(
                        request.getDesconto()
                );

        BigDecimal valorBruto =
                quantidade
                        .multiply(valorUnitario)
                        .setScale(
                                SCALE_MONETARIA,
                                RoundingMode.HALF_UP
                        );

        if (
                desconto.compareTo(valorBruto)
                        > 0
        ) {

            throw new OperacaoVendaInvalidaException(
                    "O desconto do item '"
                            + request.getDescricao()
                            + "' não pode ser maior que seu valor bruto."
            );

        }

        BigDecimal valorTotal =
                valorBruto
                        .subtract(desconto)
                        .setScale(
                                SCALE_MONETARIA,
                                RoundingMode.HALF_UP
                        );

        VendaItem item =
                new VendaItem();

        item.setDescricao(
                request.getDescricao().trim()
        );

        item.setQuantidade(
                quantidade
        );

        item.setValorUnitario(
                valorUnitario
        );

        item.setDesconto(
                desconto
        );

        item.setValorTotal(
                valorTotal
        );

        return item;

    }

    private void recalcular(
            Venda venda
    ) {

        BigDecimal subtotal =
                BigDecimal.ZERO;

        BigDecimal totalItens =
                BigDecimal.ZERO;

        for (
                VendaItem item :
                venda.getItens()
        ) {

            BigDecimal valorBruto =
                    item.getQuantidade()
                            .multiply(
                                    item.getValorUnitario()
                            )
                            .setScale(
                                    SCALE_MONETARIA,
                                    RoundingMode.HALF_UP
                            );

            BigDecimal descontoItem =
                    monetario(
                            item.getDesconto()
                    );

            if (
                    descontoItem.compareTo(valorBruto)
                            > 0
            ) {

                throw new OperacaoVendaInvalidaException(
                        "O desconto do item "
                                + item.getDescricao()
                                + " é maior que seu valor."
                );

            }

            BigDecimal totalItem =
                    valorBruto
                            .subtract(
                                    descontoItem
                            )
                            .setScale(
                                    SCALE_MONETARIA,
                                    RoundingMode.HALF_UP
                            );

            item.setValorTotal(
                    totalItem
            );

            subtotal =
                    subtotal.add(
                            valorBruto
                    );

            totalItens =
                    totalItens.add(
                            totalItem
                    );

        }

        subtotal =
                monetario(subtotal);

        totalItens =
                monetario(totalItens);

        BigDecimal descontoVenda =
                monetario(
                        venda.getDesconto()
                );

        if (
                descontoVenda.compareTo(
                        totalItens
                ) > 0
        ) {

            throw new OperacaoVendaInvalidaException(
                    "O desconto da venda não pode ser maior "
                            + "que o valor dos itens."
            );

        }

        BigDecimal valorTotal =
                totalItens
                        .subtract(
                                descontoVenda
                        )
                        .setScale(
                                SCALE_MONETARIA,
                                RoundingMode.HALF_UP
                        );

        venda.setValorSubtotal(
                subtotal
        );

        venda.setDesconto(
                descontoVenda
        );

        venda.setValorTotal(
                valorTotal
        );

    }

    private String gerarNumero() {

        Long sequencial =
                vendaRepository.proximoNumero();

        int ano =
                LocalDateTime
                        .now(ZONE_ID)
                        .getYear();

        return String.format(
                "VEN-%d-%06d",
                ano,
                sequencial
        );

    }

    private BigDecimal monetario(
            BigDecimal valor
    ) {

        if (valor == null) {

            return BigDecimal.ZERO
                    .setScale(
                            SCALE_MONETARIA,
                            RoundingMode.HALF_UP
                    );

        }

        if (
                valor.compareTo(
                        BigDecimal.ZERO
                ) < 0
        ) {

            throw new OperacaoVendaInvalidaException(
                    "Valores monetários não podem ser negativos."
            );

        }

        return valor.setScale(
                SCALE_MONETARIA,
                RoundingMode.HALF_UP
        );

    }

    private BigDecimal quantidade(
            BigDecimal valor
    ) {

        if (
                valor == null
                        || valor.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {

            throw new OperacaoVendaInvalidaException(
                    "A quantidade do item deve ser maior que zero."
            );

        }

        return valor.setScale(
                2,
                RoundingMode.HALF_UP
        );

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

    private VendaResponseDTO converterResponse(
            Venda venda
    ) {

        VendaResponseDTO dto =
                new VendaResponseDTO();

        dto.setVendaId(
                venda.getVendaId()
        );

        dto.setNumero(
                venda.getNumero()
        );

        preencherCliente(
                dto,
                venda
        );

        dto.setDataVenda(
                venda.getDataVenda()
        );

        dto.setStatus(
                venda.getStatus()
        );

        dto.setValorSubtotal(
                venda.getValorSubtotal()
        );

        dto.setDescontoItens(
                calcularDescontoItens(venda)
        );

        dto.setDesconto(
                venda.getDesconto()
        );

        dto.setFormaPagamento(venda.getFormaPagamento());

        dto.setValorTotal(
                venda.getValorTotal()
        );

        dto.setQuantidadeItens(
                venda.getItens() == null
                        ? 0
                        : venda.getItens().size()
        );

        return dto;

    }

    private VendaDetalheDTO converterDetalhe(
            Venda venda
    ) {

        VendaDetalheDTO dto =
                new VendaDetalheDTO();

        dto.setVendaId(
                venda.getVendaId()
        );

        dto.setNumero(
                venda.getNumero()
        );

        if (
                venda.getCliente()
                        != null
        ) {

            dto.setClienteId(
                    venda.getCliente()
                            .getClienteId()
            );

            dto.setClienteNome(
                    venda.getCliente()
                            .getNome()
            );

        }

        dto.setDataVenda(
                venda.getDataVenda()
        );

        dto.setStatus(
                venda.getStatus()
        );

        dto.setValorSubtotal(
                venda.getValorSubtotal()
        );

        dto.setDescontoItens(
                calcularDescontoItens(
                        venda
                )
        );

        dto.setDesconto(
                venda.getDesconto()
        );

        dto.setValorTotal(
                venda.getValorTotal()
        );

        dto.setObservacao(
                venda.getObservacao()
        );

        dto.setDataCriacao(
                venda.getDataCriacao()
        );

        dto.setDataAtualizacao(
                venda.getDataAtualizacao()
        );

        dto.setItens(
                venda.getItens()
                        .stream()
                        .map(
                                this::converterItem
                        )
                        .toList()
        );

        return dto;

    }

    private VendaItemResponseDTO converterItem(
            VendaItem item
    ) {

        VendaItemResponseDTO dto =
                new VendaItemResponseDTO();

        dto.setVendaItemId(
                item.getVendaItemId()
        );

        dto.setDescricao(
                item.getDescricao()
        );

        dto.setQuantidade(
                item.getQuantidade()
        );

        dto.setValorUnitario(
                item.getValorUnitario()
        );

        dto.setDesconto(
                item.getDesconto()
        );

        dto.setValorTotal(
                item.getValorTotal()
        );

        return dto;

    }

    private BigDecimal calcularDescontoItens(
            Venda venda
    ) {

        if (
                venda.getItens() == null
                        || venda.getItens().isEmpty()
        ) {

            return BigDecimal.ZERO
                    .setScale(
                            SCALE_MONETARIA
                    );

        }

        return venda.getItens()
                .stream()
                .map(
                        VendaItem::getDesconto
                )
                .filter(
                        desconto ->
                                desconto != null
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                )
                .setScale(
                        SCALE_MONETARIA,
                        RoundingMode.HALF_UP
                );

    }

    private void preencherCliente(
            VendaResponseDTO dto,
            Venda venda
    ) {

        if (
                venda.getCliente()
                        == null
        ) {

            return;

        }

        dto.setClienteId(
                venda.getCliente()
                        .getClienteId()
        );

        dto.setClienteNome(
                venda.getCliente()
                        .getNome()
        );

    }

}
