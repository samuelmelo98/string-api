package org.stringtecnologia.string_api.resources.documento.thymeleaf;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.documento.dto.AdiantamentoDocumentoDTO;
import org.stringtecnologia.string_api.documento.dto.DocumentoResponseDTO;
import org.stringtecnologia.string_api.documento.dto.OrdemServicoDocumentoDTO;
import org.stringtecnologia.string_api.model.dto.documento.DocumentoPreviewRequestDTO;
import org.stringtecnologia.string_api.services.*;
import org.stringtecnologia.string_api.util.DominioEnum;
import org.stringtecnologia.string_api.util.HtmlNormalizerUtil;
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/documentos/v1")
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




    @PostMapping(
            value = "/preview",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> preview(
            @RequestBody DocumentoPreviewRequestDTO request
    ) {

        String html =
                documentoTemplateService
                        .gerarPreview(
                                request.template(),
                                criarDadosPreview()
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

    @PostMapping(
            value = "/preview-pdf",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> previewPdf(
            @RequestBody DocumentoPreviewRequestDTO request
    ) {

        String html =
                documentoTemplateService
                        .gerarPreview(
                                request.template(),
                                criarDadosPreview()
                        );

        byte[] pdf =
                pdfGeneratorPlaywrightService
                        .generate(html);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    private Map<String, Object> criarDadosPreview() {

        Map<String, Object> dados =
                new HashMap<>();

        dados.put(
                "cliente",
                Map.of(
                        "nome", "Samuel Silva",
                        "cpf", "123.456.789-00",
                        "telefone", "(61) 99999-9999",
                        "email", "samuel@string.com.br"
                )
        );

        dados.put(
                "aparelho",
                Map.of(
                        "marca", "DELL",
                        "modelo", "Inspiron 15",
                        "numeroSerie", "ABC123XYZ"
                )
        );

        dados.put(
                "os",
                Map.of(
                        "numero", "OS-2026-000123",
                        "dataEntrada", LocalDate.now()
                )
        );

        return dados;
    }


}