package org.stringtecnologia.string_api.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.entities.DocumentProcessor;
import org.stringtecnologia.string_api.model.entities.DocumentoTemplate;
import org.stringtecnologia.string_api.model.entities.Venda;
import org.stringtecnologia.string_api.model.entities.VendaItem;
import org.stringtecnologia.string_api.model.enums.StatusVenda;
import org.stringtecnologia.string_api.repository.DocumentoTemplateRepository;
import org.stringtecnologia.string_api.repository.VendaRepository;
import org.stringtecnologia.string_api.util.exceptions.venda.OperacaoVendaInvalidaException;
import org.stringtecnologia.string_api.util.exceptions.venda.VendaNaoEncontradaException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class VendaCupomService {

    private static final String SLUG = "venda-cupom";
    private static final Locale LOCALE_BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.of("America/Sao_Paulo"));

    private static final String EMPRESA_NOME = "STRING TECNOLOGIA & SOLUÇÕES";
    private static final String EMPRESA_ENDERECO =
            "Grande Colorado QD 01 CJ A LT 27 - Sobradinho II, Brasília - DF, 73105-900";
    private static final String EMPRESA_TELEFONE = "(61) 3302-3957";

    private final VendaRepository vendaRepository;
    private final DocumentoTemplateRepository documentoTemplateRepository;
    private final DocumentProcessor documentProcessor;

    public VendaCupomService(
            VendaRepository vendaRepository,
            DocumentoTemplateRepository documentoTemplateRepository,
            @Qualifier("mustacheDocumentProcessor") DocumentProcessor documentProcessor
    ) {
        this.vendaRepository = vendaRepository;
        this.documentoTemplateRepository = documentoTemplateRepository;
        this.documentProcessor = documentProcessor;
    }

    @Transactional(readOnly = true)
    public String gerarCupom(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new VendaNaoEncontradaException(vendaId));

        if (venda.getStatus() != StatusVenda.FINALIZADA) {
            throw new OperacaoVendaInvalidaException(
                    "Somente vendas finalizadas podem emitir o cupom."
            );
        }

        DocumentoTemplate template = documentoTemplateRepository
                .findFirstBySlugAndActiveTrueOrderByVersionDesc(SLUG)
                .orElseThrow(() -> new IllegalStateException(
                        "Template ativo '" + SLUG + "' não encontrado."
                ));

        return documentProcessor.process(template.getTemplate(), criarVariaveis(venda));
    }

    private Map<String, Object> criarVariaveis(Venda venda) {
        String nomeCliente = venda.getCliente() == null
                ? "VENDA DE BALCÃO" : venda.getCliente().getNome();
        String cpf = venda.getCliente() == null
                ? null : venda.getCliente().getCpf();
        String observacao = venda.getObservacao();

        Map<String, Object> dadosVenda = new LinkedHashMap<>();
        dadosVenda.put("numero", texto(venda.getNumero()));
        dadosVenda.put("dataVenda", formatarData(venda.getDataVenda()));
        dadosVenda.put("clienteNome", texto(nomeCliente));
        dadosVenda.put("clienteCpf", texto(cpf));
        dadosVenda.put("temCpf", cpf != null && !cpf.isBlank());
        dadosVenda.put("subtotal", dinheiro(venda.getValorSubtotal()));
        dadosVenda.put("desconto", dinheiro(venda.getDesconto()));
        dadosVenda.put("total", dinheiro(venda.getValorTotal()));
        dadosVenda.put("observacao", texto(observacao));
        dadosVenda.put("temObservacao", observacao != null && !observacao.isBlank());
        dadosVenda.put("itens", criarItens(venda.getItens()));

        Map<String, Object> empresa = new LinkedHashMap<>();
        empresa.put("nome", EMPRESA_NOME);
        empresa.put("endereco", EMPRESA_ENDERECO);
        empresa.put("telefone", EMPRESA_TELEFONE);

        Map<String, Object> variaveis = new LinkedHashMap<>();
        variaveis.put("empresa", empresa);
        variaveis.put("venda", dadosVenda);
        return variaveis;
    }

    private List<Map<String, Object>> criarItens(List<VendaItem> itens) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        if (itens == null) return resultado;

        int sequencia = 1;
        for (VendaItem item : itens) {
            Map<String, Object> dados = new LinkedHashMap<>();
            dados.put("numero", "%02d".formatted(sequencia++));
            dados.put("descricao", texto(item.getDescricao()));
            dados.put("quantidade", item.getQuantidade() == null ? "0"
                    : item.getQuantidade().stripTrailingZeros().toPlainString());
            dados.put("valorUnitario", dinheiro(item.getValorUnitario()));
            dados.put("valorTotal", dinheiro(item.getValorTotal()));
            resultado.add(dados);
        }
        return resultado;
    }

    private String dinheiro(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(LOCALE_BR)
                .format(valor == null ? BigDecimal.ZERO : valor);
    }

    private String formatarData(Instant data) {
        return data == null ? "" : FORMATO_DATA.format(data);
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> variaveisParaPreview(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new VendaNaoEncontradaException(vendaId));
        if (venda.getStatus() != StatusVenda.FINALIZADA) {
            throw new OperacaoVendaInvalidaException(
                    "Escolha uma venda finalizada para validar o cupom.");
        }
        return criarVariaveis(venda);
    }
}
