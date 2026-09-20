package org.stringtecnologia.string_api.resources;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.stringtecnologia.string_api.model.dto.cliente.ClienteCreateDTO;
import org.stringtecnologia.string_api.model.dto.cliente.ClienteResponseDTO;
import org.stringtecnologia.string_api.model.dto.cliente.ClienteUpdateDTO;

import org.stringtecnologia.string_api.services.ClienteService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;


    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(
            @Valid @RequestBody ClienteCreateDTO dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        clienteService.criar(dto)
                );
    }


    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listar(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "true") Boolean ativo
    ) {

        return ResponseEntity.ok(
                clienteService.listar(
                        pageable,
                        search,
                        ativo
                )
        );
    }


    @GetMapping("/existe-cpf")
    public ResponseEntity<Boolean> existePorCpf(
            @RequestParam String cpf
    ) {

        return ResponseEntity.ok(
                clienteService.existePorCpf(
                        cpf
                )
        );
    }


    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @PathVariable Long clienteId
    ) {

        return ResponseEntity.ok(
                clienteService.buscarPorId(
                        clienteId
                )
        );
    }


    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long clienteId,
            @Valid @RequestBody ClienteUpdateDTO dto
    ) {

        return ResponseEntity.ok(
                clienteService.atualizar(
                        clienteId,
                        dto
                )
        );
    }


    /**
     * Exclusão lógica:
     *
     * ativo = false
     */
    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long clienteId
    ) {

        clienteService.excluir(
                clienteId
        );

        return ResponseEntity
                .noContent()
                .build();
    }


    @PatchMapping("/{clienteId}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable Long clienteId
    ) {

        clienteService.reativar(
                clienteId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}