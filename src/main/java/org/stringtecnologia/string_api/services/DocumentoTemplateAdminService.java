package org.stringtecnologia.string_api.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.entities.DocumentProcessor;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class DocumentoTemplateAdminService {
    private static final String CUPOM = "venda-cupom";
    private static final Pattern CONTEUDO_ATIVO = Pattern.compile(
            "(?is)<\\s*script\\b|<\\s*iframe\\b|<\\s*object\\b|<\\s*embed\\b|\\bon[a-z]+\\s*=|javascript\\s*:|\\{\\{\\{|\\{\\{\\s*&"
    );

    private final JdbcTemplate jdbc;
    private final DocumentProcessor processor;
    private final VendaCupomService cupomService;

    public DocumentoTemplateAdminService(JdbcTemplate jdbc,
            @Qualifier("mustacheDocumentProcessor") DocumentProcessor processor,
            VendaCupomService cupomService) {
        this.jdbc = jdbc;
        this.processor = processor;
        this.cupomService = cupomService;
    }

    public record Resumo(String slug, int version, boolean active, String description) {}
    public record Detalhe(String slug, int version, boolean active,
                          String description, String template) {}
    public record PreviewRequest(String html, Long vendaId) {}
    public record PublicarRequest(String html, String description, Integer baseVersion) {}
    public record PreviewResponse(String html) {}

    @Transactional(readOnly = true)
    public List<Resumo> listar() {
        return jdbc.query("""
            SELECT slug, version, active, description
            FROM public.tb_documento_template
            WHERE slug = ?
            ORDER BY version DESC
            """, (rs, row) -> new Resumo(rs.getString("slug"),
                rs.getInt("version"), rs.getBoolean("active"), rs.getString("description")), CUPOM);
    }

    @Transactional(readOnly = true)
    public Detalhe buscar(String slug, int version) {
        validarSlug(slug);
        return jdbc.query("""
            SELECT slug, version, active, description, template
            FROM public.tb_documento_template
            WHERE slug = ? AND version = ?
            """, (rs, row) -> new Detalhe(rs.getString("slug"),
                rs.getInt("version"), rs.getBoolean("active"),
                rs.getString("description"), rs.getString("template")), slug, version)
            .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Versão não encontrada"));
    }

    @Transactional(readOnly = true)
    public PreviewResponse preview(String slug, PreviewRequest pedido) {
        validarSlug(slug);
        validarHtml(pedido.html());
        if (pedido.vendaId() == null || pedido.vendaId() <= 0) {
            throw new IllegalArgumentException("Informe um ID de venda válido para o preview");
        }
        // Usa o mesmo mapa de variáveis da impressão real, sem gravar no banco.
        Map<String, Object> variaveis = cupomService.variaveisParaPreview(pedido.vendaId());
        return new PreviewResponse(processor.process(pedido.html(), variaveis));
    }

    @Transactional
    public Detalhe publicar(String slug, PublicarRequest pedido) {
        validarSlug(slug);
        validarHtml(pedido.html());
        // Serializa publicações concorrentes do mesmo slug dentro da transação PostgreSQL.
        jdbc.query("SELECT pg_advisory_xact_lock(hashtext(?))", rs -> null, slug);
        Integer ultima = jdbc.queryForObject("""
            SELECT COALESCE(MAX(version), 0) FROM public.tb_documento_template WHERE slug = ?
            """, Integer.class, slug);
        int versaoAtual = ultima == null ? 0 : ultima;
        if (pedido.baseVersion() == null || pedido.baseVersion() != versaoAtual) {
            throw new IllegalStateException("O template foi alterado por outro usuário. Recarregue antes de publicar.");
        }
        int proxima = versaoAtual + 1;
        jdbc.update("""
            INSERT INTO public.tb_documento_template
                (documento_template_id, slug, version, template, active,
                 created_at, updated_at, description)
            VALUES (nextval('public.seq_documento_template'), ?, ?, ?, TRUE,
                    NOW(), NOW(), ?)
            """, slug, proxima, pedido.html(), pedido.description());
        // A impressão usa findFirstBySlugAndActiveTrueOrderByVersionDesc:
        // a nova versão ativa vence, mantendo versões antigas para auditoria.
        return buscar(slug, proxima);
    }

    private void validarSlug(String slug) {
        if (!CUPOM.equals(slug)) {
            throw new IllegalArgumentException("Template ainda não habilitado para edição: " + slug);
        }
    }

    private void validarHtml(String html) {
        if (html == null || html.isBlank() || html.length() > 250_000) {
            throw new IllegalArgumentException("HTML vazio ou maior que 250.000 caracteres");
        }
        if (CONTEUDO_ATIVO.matcher(html).find()) {
            throw new IllegalArgumentException("Scripts, eventos HTML e Mustache sem escape não são permitidos");
        }
        if (!html.contains("{{venda.numero}}")) {
            throw new IllegalArgumentException("A variável {{venda.numero}} é obrigatória");
        }
    }
}
