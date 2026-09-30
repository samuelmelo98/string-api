package org.stringtecnologia.string_api.resources;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.venda.VendaDetalheDTO;
import org.stringtecnologia.string_api.model.dto.venda.VendaRequestDTO;
import org.stringtecnologia.string_api.model.dto.venda.VendaResponseDTO;
import org.stringtecnologia.string_api.services.VendaService;
import org.stringtecnologia.string_api.services.VendaCupomService;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;
    private final VendaCupomService vendaCupomService;

    @PostMapping
    public ResponseEntity<VendaDetalheDTO> criar(
            @Valid
            @RequestBody
            VendaRequestDTO request
    ) {

        VendaDetalheDTO venda =
                vendaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venda);

    }

    @GetMapping
    public ResponseEntity<Page<VendaResponseDTO>> listar(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                vendaService.listar(
                        pageable
                )
        );

    }

    @GetMapping("/{vendaId}")
    public ResponseEntity<VendaDetalheDTO> buscar(
            @PathVariable
            Long vendaId
    ) {

        return ResponseEntity.ok(
                vendaService.buscar(
                        vendaId
                )
        );

    }

    @PutMapping("/{vendaId}")
    public ResponseEntity<VendaDetalheDTO> atualizar(
            @PathVariable
            Long vendaId,

            @Valid
            @RequestBody
            VendaRequestDTO request
    ) {

        return ResponseEntity.ok(
                vendaService.atualizar(
                        vendaId,
                        request
                )
        );

    }

    @PostMapping("/{vendaId}/finalizar")
    public ResponseEntity<VendaDetalheDTO> finalizar(
            @PathVariable
            Long vendaId
    ) {

        return ResponseEntity.ok(
                vendaService.finalizar(
                        vendaId
                )
        );

    }

    @PostMapping("/{vendaId}/cancelar")
    public ResponseEntity<VendaDetalheDTO> cancelar(
            @PathVariable
            Long vendaId
    ) {

        return ResponseEntity.ok(
                vendaService.cancelar(
                        vendaId
                )
        );

    }

    @GetMapping(
            value = "/{vendaId}/cupom",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> emitirCupom(
            @PathVariable Long vendaId
    ) {

        String html =
                vendaCupomService
                        .gerarCupom(
                                vendaId
                        );

        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType(
                                "text/html;charset=UTF-8"
                        )
                )
                .body(html);
    }

}
