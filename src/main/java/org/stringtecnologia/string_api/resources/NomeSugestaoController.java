package org.stringtecnologia.string_api.resources;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.nome.CriarNomeSugestaoRequest;
import org.stringtecnologia.string_api.model.dto.nome.NomeSugestaoResponse;
import org.stringtecnologia.string_api.services.NomeSugestaoCacheService;
import org.stringtecnologia.string_api.services.NomeSugestaoService;

@RestController
@RequestMapping(
        "/api/nomes-sugestao"
)
@RequiredArgsConstructor
public class NomeSugestaoController {

    private final NomeSugestaoService
            service;

    private final NomeSugestaoCacheService
            cacheService;

    @PostMapping
    @ResponseStatus(
            HttpStatus.CREATED
    )
    public NomeSugestaoResponse criar(
            @Valid
            @RequestBody
            CriarNomeSugestaoRequest request
    ) {

        return service.criar(
                request
        );
    }

    @PatchMapping(
            "/{id}/ativar"
    )
    public NomeSugestaoResponse ativar(
            @PathVariable
            Long id
    ) {

        return service.ativar(
                id
        );
    }

    @PatchMapping(
            "/{id}/inativar"
    )
    public NomeSugestaoResponse inativar(
            @PathVariable
            Long id
    ) {

        return service.inativar(
                id
        );
    }

    @PostMapping(
            "/cache/reconstruir"
    )
    @ResponseStatus(
            HttpStatus.NO_CONTENT
    )
    public void reconstruirCache() {

        cacheService.reconstruir();
    }
}
