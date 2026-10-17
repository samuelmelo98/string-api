package org.stringtecnologia.string_api.resources;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.services.DocumentoTemplateAdminService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/documento-templates")
@PreAuthorize(
        "@authz.has(authentication, 'DOCUMENTO_TEMPLATE_CRIAR')"
)
public class DocumentoTemplateAdminController {
    private final DocumentoTemplateAdminService service;

    public DocumentoTemplateAdminController(DocumentoTemplateAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentoTemplateAdminService.Resumo> listar() {
        return service.listar();
    }

    @GetMapping("/{slug}/versoes/{version}")
    public DocumentoTemplateAdminService.Detalhe buscar(@PathVariable String slug,
                                                         @PathVariable int version) {
        return service.buscar(slug, version);
    }

    @PostMapping("/{slug}/preview")
    public DocumentoTemplateAdminService.PreviewResponse preview(@PathVariable String slug,
            @RequestBody DocumentoTemplateAdminService.PreviewRequest request) {
        return service.preview(slug, request);
    }

    @PostMapping("/{slug}/publicar")
    public ResponseEntity<DocumentoTemplateAdminService.Detalhe> publicar(@PathVariable String slug,
            @RequestBody DocumentoTemplateAdminService.PublicarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.publicar(slug, request));
    }
}
