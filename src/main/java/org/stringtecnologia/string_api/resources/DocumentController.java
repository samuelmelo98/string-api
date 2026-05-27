package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.services.DocumentService;
import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/{slug}/render")
    public ResponseEntity<String> render(
            @PathVariable String slug,
            @RequestParam Long adiantamentoId
    ) {

        Map<String, Object> model = new HashMap<>();

        model.put("usuario", "Samuel Silva");
        model.put("valor", 1500.00);
        model.put("data", LocalDate.now());

        // ADICIONE
        model.put("adiantamentoId", adiantamentoId);

        String htmlFinal = documentService.generateDocument(slug, model);

//        return ResponseEntity.ok()
//                .contentType(new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8))
//                .body(htmlFinal);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "html", StandardCharsets.UTF_8))
                .body(htmlFinal);
    }
}
