package org.stringtecnologia.string_api.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.interfaces.DocumentProcessor;
import org.stringtecnologia.string_api.model.dto.relatorio.RelatorioSemanalTecnicoDTO;
import org.stringtecnologia.string_api.model.dto.relatorio.RelatorioSemanalTecnicoItemDTO;
import org.stringtecnologia.string_api.model.dto.relatorio.RelatorioSemanalTecnicoPagamentoDTO;
import org.stringtecnologia.string_api.repository.DocumentoTemplateRepository;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Service
public class RelatorioTecnicoDocumentoService {
    private static final String SLUG = "relatorio-semanal-tecnico";
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZONA);

    private final RelatorioSemanalTecnicoService relatorioService;
    private final DocumentoTemplateRepository templateRepository;
    private final DocumentProcessor documentProcessor;

    public RelatorioTecnicoDocumentoService(
            RelatorioSemanalTecnicoService relatorioService,
            DocumentoTemplateRepository templateRepository,
            @Qualifier("mustacheDocumentProcessor") DocumentProcessor documentProcessor
    ) {
        this.relatorioService = relatorioService;
        this.templateRepository = templateRepository;
        this.documentProcessor = documentProcessor;
    }

    @Transactional(readOnly = true)
    public String gerar(LocalDate inicio, LocalDate fim, Long tecnicoId) {
        var relatorios = relatorioService.gerarRelatorioPorPeriodo(inicio, fim, tecnicoId);
        var template = templateRepository
                .findFirstBySlugAndActiveTrueOrderByVersionDesc(SLUG)
                .orElseThrow(() -> new IllegalStateException("Template ativo '" + SLUG + "' não encontrado."));
        return documentProcessor.process(template.getTemplate(), criarVariaveis(relatorios, inicio, fim));
    }

    private Map<String, Object> criarVariaveis(
            List<RelatorioSemanalTecnicoDTO> relatorios, LocalDate inicio, LocalDate fim
    ) {
        return Map.of(
                "empresa", Map.of("nome", "String Tecnologia"),
                "inicioConsulta", DATA.format(inicio),
                "fimConsulta", DATA.format(fim),
                "emitidoEm", DATA_HORA.format(Instant.now()),
                "relatorios", relatorios.stream().map(this::mapearRelatorio).toList()
        );
    }

    private Map<String, Object> mapearRelatorio(RelatorioSemanalTecnicoDTO r) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("tecnicoId", r.getTecnicoId());
        dados.put("tecnicoNome", texto(r.getTecnicoNome()));
        dados.put("tecnicoEmail", texto(r.getTecnicoEmail()));
        dados.put("inicio", DATA.format(r.getInicio()));
        dados.put("fim", DATA.format(r.getFim()));
        dados.put("quantidade", r.getAparelhos().size());
        dados.put("aparelhos", r.getAparelhos().stream().map(this::mapearAparelho).toList());
        dados.put("totalOrdensServico", moeda(r.getTotalOrdensServico()));
        dados.put("totalMateriais", moeda(r.getTotalMateriais()));
        dados.put("valorLiquido", moeda(r.getValorLiquido()));
        dados.put("valorTecnico", moeda(r.getValorTecnico()));
        return dados;
    }

    private Map<String, Object> mapearAparelho(RelatorioSemanalTecnicoItemDTO item) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("numeroOrdemServico", texto(item.getNumeroOrdemServico()));
        dados.put("clienteNome", texto(item.getClienteNome()));
        dados.put("descricaoAparelho", descricaoAparelho(item));
        dados.put("numeroSerie", texto(item.getNumeroSerie()));
        dados.put("dataEntrega", item.getDataEntrega() == null ? "—" : DATA_HORA.format(item.getDataEntrega()));
        dados.put("valorOrdemServico", moeda(item.getValorOrdemServico()));
        dados.put("valorMaterial", moeda(item.getValorMaterial()));
        dados.put("pagamentos", item.getPagamentos() == null ? List.of() :
                item.getPagamentos().stream().map(this::mapearPagamento).toList());
        return dados;
    }

    private Map<String, Object> mapearPagamento(RelatorioSemanalTecnicoPagamentoDTO p) {
        Integer parcelas = p.getParcelas();
        return Map.of(
                "formaPagamento", nomeFormaPagamento(p.getFormaPagamento()),
                "valor", moeda(p.getValor()),
                "parcelas", parcelas == null ? 1 : parcelas,
                "parcelado", parcelas != null && parcelas > 1
        );
    }

    private String descricaoAparelho(RelatorioSemanalTecnicoItemDTO item) {
        String descricao = Stream.of(item.getModeloComercial(), item.getModelo())
                .filter(Objects::nonNull).map(String::trim)
                .filter(s -> !s.isBlank()).distinct().collect(Collectors.joining(" - "));
        return descricao.isBlank() ? "Aparelho #" + item.getAparelhoId() : descricao;
    }

    private String nomeFormaPagamento(Object forma) {
        if (forma == null) return "Não informado";
        String codigo = forma instanceof Enum<?> e ? e.name() : forma.toString();
        return switch (codigo) {
            case "DINHEIRO" -> "Dinheiro";
            case "PIX" -> "PIX";
            case "CARTAO_DEBITO" -> "Cartão de débito";
            case "CARTAO_CREDITO" -> "Cartão de crédito";
            case "TRANSFERENCIA" -> "Transferência";
            default -> codigo;
        };
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "—" : valor;
    }

    private String moeda(BigDecimal valor) {
        // NumberFormat é criado por chamada; não é compartilhado entre requisições.
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
                .format(valor == null ? BigDecimal.ZERO : valor);
    }
}
