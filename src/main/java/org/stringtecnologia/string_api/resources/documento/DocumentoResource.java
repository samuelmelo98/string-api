package org.stringtecnologia.string_api.resources.documento;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.documento.dto.DocumentoResponseDTO;
import org.stringtecnologia.string_api.model.dto.documento.DocumentoDownloadDTO;
import org.stringtecnologia.string_api.services.DocumentoService;
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;

import java.util.List;

/**
 * TESTES REMOVER DEPOIS
 * BASE_URL="http://localhost:8022/string-api"
 * ADIANTAMENTO_ID=73
 *
 * ANEXO
 * curl -X POST "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos" \
 *   -F "file=@./teste.pdf" \
 *   -F "tipo=ANEXO"
 *
 *   TERMO ASSINATURA
 *
 *   curl -X POST "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos" \
 *   -F "file=@./termo.pdf" \
 *   -F "tipo=TERMO_ADIANTAMENTO_PARA_ASSINAR"
 *
 *   DOCUMENTO ASSINADO
 *
 *   curl -X POST "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos" \
 *   -F "file=@./assinado.pdf" \
 *   -F "tipo=TERMO_ADIANTAMENTO_ASSINADO"
 *
 *   LISTA DOCUMENTOS
 *
 *   curl -X GET "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos"
 *
 *   BUSCAR TERMO PARA ASSINAR
 *
 *   curl -X GET "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos/termo-assinatura" \
 *   -o termo.pdf
 *
 *
 *   DELETAR DOCUMENTO
 *
 *   curl -X DELETE "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos/10"
 *
 *   TESTES COM DEBUG
 *
 *   curl -v -X GET "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos/termo-assinatura" \
 *   -o termo.pdf
 *
 *   TIPO DOCUMENTO INVALIDO
 *
 *   curl -X POST "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos" \
 *   -F "file=@./teste.pdf" \
 *   -F "tipo=INVALIDO"
 *
 *   ARQUIVO MUITO GRANDE
 *
 *   curl -X POST "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos" \
 *   -F "file=@./arquivo_grande.pdf" \
 *   -F "tipo=ANEXO"
 *
 *   TIPO NÃO PERMITIDO
 *
 *   curl -X POST "$BASE_URL/adiantamentos/$ADIANTAMENTO_ID/documentos" \
 *   -F "file=@./teste.exe" \
 *   -F "tipo=ANEXO"
 *
 *
 */

@RestController
@RequestMapping("/adiantamentos/{adiantamentoId}/documentos")
@RequiredArgsConstructor
public class DocumentoResource {

    private final DocumentoService service;

    @PostMapping
    public ResponseEntity<DocumentoResponseDTO> upload(
            @PathVariable Long adiantamentoId,
            @RequestParam("file") MultipartFile file,
            @RequestParam TipoDocumentoDominio tipo
    ) {
        DocumentoResponseDTO response = service.upload(adiantamentoId, file, tipo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{documentoId}")
    public ResponseEntity<byte[]> download(
            @PathVariable Long documentoId
    ) {
        DocumentoDownloadDTO dto = service.download(documentoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + dto.nome() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, dto.mimeType())
                .body(dto.dados());
    }

    @DeleteMapping("/{documentoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long documentoId) {
        service.deletar(documentoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DocumentoResponseDTO>> listar(
            @PathVariable Long adiantamentoId
    ) {
        return ResponseEntity.ok(service.listarPorAdiantamento(adiantamentoId));
    }

    @GetMapping("/termo-assinatura")
    public ResponseEntity<byte[]> buscarTermoParaAssinar(
            @PathVariable Long adiantamentoId
    ) {
        DocumentoDownloadDTO dto = service.buscarTermoParaAssinar(adiantamentoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + dto.nome() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, dto.mimeType())
                .body(dto.dados());
    }

    @PostMapping("/batch")
    public ResponseEntity<List<DocumentoResponseDTO>> uploadBatch(
            @PathVariable Long adiantamentoId,
            @RequestPart(value = "adiantamento", required = false) String adiantamentoJson,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart(value = "tipos", required = false) List<TipoDocumentoDominio> tipos
    ) {

        List<DocumentoResponseDTO> response =
                service.uploadBatch(adiantamentoId, files, tipos);

        return ResponseEntity.ok(response);
    }
}