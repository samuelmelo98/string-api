package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoRequestDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoResponseDTO;
import org.stringtecnologia.string_api.services.OrdemServicoDocumentoService;
import org.stringtecnologia.string_api.services.OrdemServicoService;

@RestController
@RequestMapping("/api/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    private final OrdemServicoDocumentoService
            ordemServicoDocumentoService;

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> abrir(
            @RequestBody OrdemServicoRequestDTO request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ordemServicoService.abrir(
                                request.aparelhoId()
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ordemServicoService.buscarPorId(id)
        );
    }

    @GetMapping(
            value = "/{id}/documento",
            produces = "text/html;charset=UTF-8"
    )
    public ResponseEntity<String> documento(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ordemServicoDocumentoService
                        .gerar(id)
        );
    }
}