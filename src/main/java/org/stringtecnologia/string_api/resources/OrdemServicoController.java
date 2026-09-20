package org.stringtecnologia.string_api.resources;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.ordemServico.AtribuirTecnicoRequestDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.ConcluirOrdemServicoRequestDTO;
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

    @PostMapping("/{ordemServicoId}/iniciar-analise")
    public ResponseEntity<OrdemServicoResponseDTO> iniciarAnalise(
            @PathVariable Long ordemServicoId
    ) {

        return ResponseEntity.ok(
                ordemServicoService
                        .iniciarAnalise(
                                ordemServicoId
                        )
        );
    }

    @PostMapping("/{ordemServicoId}/atribuir-tecnico")
    public ResponseEntity<OrdemServicoResponseDTO> atribuirTecnico(
            @PathVariable Long ordemServicoId,
            @Valid @RequestBody AtribuirTecnicoRequestDTO request
    ) {

        return ResponseEntity.ok(
                ordemServicoService
                        .atribuirTecnico(
                                ordemServicoId,
                                request.tecnicoId()
                        )
        );
    }

    @PostMapping("/{ordemServicoId}/iniciar-execucao")
    public ResponseEntity<OrdemServicoResponseDTO> iniciarExecucao(
            @PathVariable Long ordemServicoId
    ) {

        return ResponseEntity.ok(
                ordemServicoService
                        .iniciarExecucao(
                                ordemServicoId
                        )
        );
    }

    @PostMapping("/{ordemServicoId}/concluir")
    public ResponseEntity<OrdemServicoResponseDTO> concluir(
            @PathVariable Long ordemServicoId,
            @Valid @RequestBody ConcluirOrdemServicoRequestDTO request
    ) {

        return ResponseEntity.ok(
                ordemServicoService.concluir(
                        ordemServicoId,
                        request
                )
        );
    }

    @PostMapping("/{ordemServicoId}/entregar")
    public ResponseEntity<OrdemServicoResponseDTO> entregar(
            @PathVariable Long ordemServicoId
    ) {

        return ResponseEntity.ok(
                ordemServicoService.entregar(
                        ordemServicoId
                )
        );
    }

    @GetMapping("/aparelho/{aparelhoId}/ultima")
    public ResponseEntity<OrdemServicoResponseDTO> buscarUltimaPorAparelho(
            @PathVariable Long aparelhoId
    ) {

        return ordemServicoService
                .buscarUltimaPorAparelho(aparelhoId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent().build()
                );
    }
}