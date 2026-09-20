package org.stringtecnologia.string_api.resources;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.stringtecnologia.string_api.model.dto.aparelho.TipoAparelhoResponseDTO;
import org.stringtecnologia.string_api.services.TipoAparelhoService;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-aparelho")
@RequiredArgsConstructor
public class TipoAparelhoController {

    private final TipoAparelhoService tipoAparelhoService;

    @GetMapping
    public List<TipoAparelhoResponseDTO> listarAtivos() {
        return tipoAparelhoService.listarAtivos();
    }
}
