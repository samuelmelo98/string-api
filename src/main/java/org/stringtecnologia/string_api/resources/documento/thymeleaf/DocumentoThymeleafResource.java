package org.stringtecnologia.string_api.resources.documento.thymeleaf;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.documento.dto.AdiantamentoDocumentoDTO;
import org.stringtecnologia.string_api.documento.dto.DocumentoResponseDTO;
import org.stringtecnologia.string_api.documento.dto.OrdemServicoDocumentoDTO;
import org.stringtecnologia.string_api.services.*;
import org.stringtecnologia.string_api.util.DominioEnum;
import org.stringtecnologia.string_api.util.HtmlNormalizerUtil;
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/documentos/v1")
@RequiredArgsConstructor
public class DocumentoThymeleafResource {

    private final DocumentoTemplateService documentoTemplateService;
    private final PdfGeneratorPlaywrightService pdfGeneratorPlaywrightService;
    private final GeradorDocumentoService geradorDocumentoService;
    private final GeradorDocumentoGenericoService geradorDocumentoGenericoService;

    @GetMapping("/{slug}/render")
    public ResponseEntity<String> render(
            @PathVariable String slug,
            @RequestParam Long adiantamentoId
    ) {

        AdiantamentoDocumentoDTO dto =
                criarDTO(adiantamentoId);

        String html =
                documentoTemplateService
                        .gerarAdiantamento(
                                slug,
                                dto
                        );

        return ResponseEntity
                .ok()
                .contentType(
                        new MediaType(
                                "text",
                                "html",
                                StandardCharsets.UTF_8
                        )
                )
                .body(html);
    }

    @GetMapping("/{slug}/pdf")
    public ResponseEntity<byte[]> gerarPdf(
            @PathVariable String slug,
            @RequestParam Long adiantamentoId
    ) {
        AdiantamentoDocumentoDTO dto = criarDTO(adiantamentoId);

        String html = documentoTemplateService.gerarAdiantamento(slug, dto);

        // ✅ NORMALIZAÇÃO ANTES DE GERAR
        String htmlNormalizado = HtmlNormalizerUtil.normalize(html);

        // ✅ RECOMENDAÇÃO: Use o Playwright aqui também para evitar o erro de XML
        byte[] pdf = pdfGeneratorPlaywrightService.generate(htmlNormalizado);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=" + slug + ".pdf")
                .body(pdf);
    }


    @PostMapping("/{slug}/gerar")
    public DocumentoResponseDTO gerarESalvar(
            @PathVariable String slug,
            @RequestParam Long adiantamentoId
    ) {

        return geradorDocumentoService
                .gerarTermoAdiantamento(
                        "adiantamento",
                        adiantamentoId
                );
    }

    private AdiantamentoDocumentoDTO criarDTO(
            Long adiantamentoId
    ) {

        return new AdiantamentoDocumentoDTO(
                adiantamentoId,
                "Samuel Silva",
                BigDecimal.valueOf(1500),
                LocalDate.now()

        );
    }

    @PostMapping("/{slug}/gerar/ordem")
    public DocumentoResponseDTO gerarESalvarOrdem(
            @PathVariable String slug,
            @RequestParam Long equipamentoId
    ) {

//        return geradorDocumentoService.gerarOrdemServico(slug, equipamentoId);
        return geradorDocumentoGenericoService.gerarDocumento(slug,dto,
                equipamentoId, TipoDocumentoDominio.ORDEM_SERVICO,"ordem_servico");
    }
    OrdemServicoDocumentoDTO dto =
            new OrdemServicoDocumentoDTO(
                    100L,
                    "OS-2026-0001",
                    "Samuel Silvaaaa",
                    "Notebook Dell Latitude 5420",
                    "Troca de SSD e instalação do sistema operacional",
                    BigDecimal.valueOf(350.00),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now()
            );


}