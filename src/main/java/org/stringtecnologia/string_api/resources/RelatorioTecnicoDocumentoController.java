package org.stringtecnologia.string_api.resources;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.stringtecnologia.string_api.services.RelatorioTecnicoDocumentoService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios/tecnicos")
public class RelatorioTecnicoDocumentoController {
    private final RelatorioTecnicoDocumentoService service;

    public RelatorioTecnicoDocumentoController(RelatorioTecnicoDocumentoService service) {
        this.service = service;
    }

    // Aplicar aqui a mesma autorização usada na consulta do relatório atual.
    @GetMapping(value = "/documento", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> documento(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(value = "tecnicoId", required = false) Long tecnicoId
    ) {
        if (fim.isBefore(inicio) || (tecnicoId != null && tecnicoId <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Período ou técnico inválido.");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(service.gerar(inicio, fim, tecnicoId));
    }
}
