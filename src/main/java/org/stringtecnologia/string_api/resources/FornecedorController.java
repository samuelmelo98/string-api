
package org.stringtecnologia.string_api.resources;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.fornecedor.FornecedorOpcaoDTO;
import org.stringtecnologia.string_api.model.dto.fornecedor.FornecedorRequestDTO;
import org.stringtecnologia.string_api.model.dto.fornecedor.FornecedorResponseDTO;
import org.stringtecnologia.string_api.model.dto.fornecedor.FornecedorStatusDTO;
import org.stringtecnologia.string_api.services.FornecedorService;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(
            "@authz.has(authentication, 'FORNECEDOR_CRIAR')"
    )
    public FornecedorResponseDTO criar(
            @Valid @RequestBody FornecedorRequestDTO dto
    ) {
        return service.criar(dto);
    }

    @GetMapping
    @PreAuthorize(
            "@authz.has(authentication, 'FORNECEDOR_VISUALIZAR')"
    )
    public Page<FornecedorResponseDTO> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 10, sort = "fornecedorId")
            Pageable pageable
    ) {
        return service.listar(busca, ativo, pageable);
    }

    @GetMapping("/opcoes")
    @PreAuthorize(
            "@authz.has(authentication, 'FORNECEDOR_VISUALIZAR')"
    )
    public List<FornecedorOpcaoDTO> opcoes(
            @RequestParam(required = false) String busca
    ) {
        return service.opcoes(busca);
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "@authz.has(authentication, 'FORNECEDOR_VISUALIZAR')"
    )
    public FornecedorResponseDTO buscar(
            @PathVariable Long id
    ) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "@authz.has(authentication, 'FORNECEDOR_EDITAR')"
    )
    public FornecedorResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorRequestDTO dto
    ) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize(
            "@authz.has(authentication, 'FORNECEDOR_DESATIVAR')"
    )
    public FornecedorResponseDTO alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody FornecedorStatusDTO dto
    ) {
        return service.alterarStatus(id, dto.ativo());
    }
}
