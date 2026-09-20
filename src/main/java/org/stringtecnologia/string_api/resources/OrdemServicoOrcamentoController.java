package org.stringtecnologia.string_api.resources;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoOrcamentoRequestDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.OrdemServicoOrcamentoResponseDTO;
import org.stringtecnologia.string_api.services.OrdemServicoOrcamentoService;

@RestController
@RequestMapping("/api/ordens-servico/{ordemServicoId}/orcamentos")
@RequiredArgsConstructor
public class OrdemServicoOrcamentoController {

    private final OrdemServicoOrcamentoService ordemServicoOrcamentoService;

    @PostMapping
    public ResponseEntity<OrdemServicoOrcamentoResponseDTO> salvarRascunho(
            @PathVariable Long ordemServicoId,
            @Valid @RequestBody OrdemServicoOrcamentoRequestDTO request
    ) {

        return ResponseEntity.ok(
                ordemServicoOrcamentoService.salvarRascunho(
                        ordemServicoId,
                        request
                )
        );
    }

    @GetMapping("/atual")
    public ResponseEntity<OrdemServicoOrcamentoResponseDTO> buscarAtual(
            @PathVariable Long ordemServicoId
    ) {

        return ordemServicoOrcamentoService
                .buscarAtual(ordemServicoId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent().build()
                );
    }

    @PostMapping("/{orcamentoId}/enviar")
    public ResponseEntity<OrdemServicoOrcamentoResponseDTO> enviarParaAprovacao(
            @PathVariable Long ordemServicoId,
            @PathVariable Long orcamentoId
    ) {

        return ResponseEntity.ok(
                ordemServicoOrcamentoService
                        .enviarParaAprovacao(
                                ordemServicoId,
                                orcamentoId
                        )
        );
    }

    @PostMapping("/{orcamentoId}/aprovar")
    public ResponseEntity<OrdemServicoOrcamentoResponseDTO> aprovar(
            @PathVariable Long ordemServicoId,
            @PathVariable Long orcamentoId
    ) {

        return ResponseEntity.ok(
                ordemServicoOrcamentoService
                        .aprovar(
                                ordemServicoId,
                                orcamentoId
                        )
        );
    }


    @PostMapping("/{orcamentoId}/reprovar")
    public ResponseEntity<OrdemServicoOrcamentoResponseDTO> reprovar(
            @PathVariable Long ordemServicoId,
            @PathVariable Long orcamentoId
    ) {

        return ResponseEntity.ok(
                ordemServicoOrcamentoService
                        .reprovar(
                                ordemServicoId,
                                orcamentoId
                        )
        );
    }
}