package org.stringtecnologia.string_api.resources;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.nome.NomeAutocompleteResponse;
import org.stringtecnologia.string_api.services.NomeAutocompleteService;


import java.util.List;

@RestController
@RequestMapping(
        "/api/autocomplete/nomes"
)
@RequiredArgsConstructor
public class NomeAutocompleteController {

    private final NomeAutocompleteService
            service;

    @GetMapping
    public List<NomeAutocompleteResponse> sugerir(
            @RequestParam("q")
            String query
    ) {

        return service
                .sugerir(
                        query
                );
    }
}
