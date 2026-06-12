package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoStatusRequestDTO;
import org.stringtecnologia.string_api.services.AparelhoService;

import java.util.List;

@RestController
@RequestMapping("/aparelhos")
@RequiredArgsConstructor
public class AparelhoController {

    private final AparelhoService service;

    @PostMapping
    public ResponseEntity<AparelhoResponseDTO> criar(
            @RequestBody AparelhoRequestDTO request
    ) {
        return ResponseEntity.ok(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<AparelhoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AparelhoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AparelhoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AparelhoRequestDTO request
    ) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AparelhoResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestBody AparelhoStatusRequestDTO request
    ) {
        return ResponseEntity.ok(service.alterarStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
