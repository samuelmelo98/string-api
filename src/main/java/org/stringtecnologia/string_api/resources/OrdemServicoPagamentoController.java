package org.stringtecnologia.string_api.resources;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.ordemServico.pagamento.OrdemServicoPagamentoCreateDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.pagamento.OrdemServicoPagamentoDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.pagamento.OrdemServicoPagamentoResumoDTO;
import org.stringtecnologia.string_api.services.OrdemServicoPagamentoService;

@RestController
@RequestMapping(
        "/api/ordens-servico/{ordemServicoId}/pagamentos"
)
@RequiredArgsConstructor
public class OrdemServicoPagamentoController {

    private final OrdemServicoPagamentoService service;

    @PostMapping
    public ResponseEntity<OrdemServicoPagamentoDTO> adicionar(
            @PathVariable Long ordemServicoId,
            @Valid
            @RequestBody
            OrdemServicoPagamentoCreateDTO dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.adicionarPagamento(
                                ordemServicoId,
                                dto
                        )
                );
    }

    @GetMapping
    public ResponseEntity<OrdemServicoPagamentoResumoDTO> buscarResumo(
            @PathVariable Long ordemServicoId
    ) {

        return ResponseEntity.ok(
                service.buscarResumo(
                        ordemServicoId
                )
        );
    }

    @DeleteMapping("/{pagamentoId}")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long ordemServicoId,
            @PathVariable Long pagamentoId
    ) {

        service.cancelarPagamento(
                ordemServicoId,
                pagamentoId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}