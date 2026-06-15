package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoStatusRequestDTO;
import org.stringtecnologia.string_api.services.AparelhoService;

import java.util.List;

@RestController
@RequestMapping("api/aparelhos")
@RequiredArgsConstructor
public class AparelhoController {

    private final AparelhoService aparelhoService;

    @PostMapping
    public ResponseEntity<AparelhoResponseDTO> criar(
            @RequestBody AparelhoRequestDTO request
    ) {
        return ResponseEntity.ok(aparelhoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<AparelhoResponseDTO>> listar() {

        return ResponseEntity.ok(aparelhoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AparelhoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(aparelhoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AparelhoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AparelhoRequestDTO request
    ) {
        return ResponseEntity.ok(aparelhoService.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AparelhoResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestBody AparelhoStatusRequestDTO request
    ) {
        return ResponseEntity.ok(aparelhoService.alterarStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        aparelhoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<AparelhoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                aparelhoService.listarPorCliente(
                        clienteId,
                        pageable
                )
        );
    }
}
