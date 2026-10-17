package org.stringtecnologia.string_api.ocr.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.ocr.dto.ImagemDto;
import org.stringtecnologia.string_api.ocr.service.OcrImagemService;

@RestController
@RequestMapping("/api/ocr/imagens")
public class OcrImagemController {
    private final OcrImagemService service;

    public OcrImagemController(OcrImagemService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemDto> upload(@RequestPart("arquivo") MultipartFile arquivo) throws Exception {
        return ResponseEntity.accepted().body(service.upload(arquivo));
    }

    @GetMapping("/{id}")
    public ImagemDto detalhe(@PathVariable Long id) {
        return service.detalhe(id);
    }

    @GetMapping("/buscar")
    public Page<ImagemDto> buscar(@RequestParam String codigo, @PageableDefault(size = 20) Pageable pageable) {
        return service.buscar(codigo, pageable);
    }

    @GetMapping("/{id}/arquivo")
    public ResponseEntity<byte[]> arquivo(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(service.contentType(id)))
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=60").body(service.arquivo(id));
    }

    @PostMapping("/{id}/reprocessar")
    public ImagemDto reprocessar(@PathVariable Long id) {
        return service.reprocessar(id);
    }
}
