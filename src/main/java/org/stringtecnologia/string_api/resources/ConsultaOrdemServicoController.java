package org.stringtecnologia.string_api.resources;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.ordemServico.ConsultaOrdemServicoRequestDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.ConsultaOrdemServicoResponseDTO;
import org.stringtecnologia.string_api.services.ConsultaOrdemServicoService;

@RestController
@RequestMapping("/api/public/ordens-servico")
@RequiredArgsConstructor
public class ConsultaOrdemServicoController {

    private final ConsultaOrdemServicoService consultaOrdemServicoService;

    @PostMapping("/{numero}/consultar")
    public ResponseEntity<ConsultaOrdemServicoResponseDTO> consultar(
            @PathVariable String numero,
            @Valid
            @RequestBody ConsultaOrdemServicoRequestDTO request
    ) {

        ConsultaOrdemServicoResponseDTO response =
                consultaOrdemServicoService.consultar(
                        numero,
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }
}
