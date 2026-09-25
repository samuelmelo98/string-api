package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.model.dto.relatorio.RelatorioSemanalTecnicoDTO;
import org.stringtecnologia.string_api.services.RelatorioSemanalTecnicoService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios/tecnicos")
@RequiredArgsConstructor
public class RelatorioSemanalTecnicoController {

    private final RelatorioSemanalTecnicoService service;

    @GetMapping("/semanal")
    public ResponseEntity<List<RelatorioSemanalTecnicoDTO>>
    gerarRelatorioSemanal(
            @RequestParam(required = false) Long tecnicoId
    ) {

        return ResponseEntity.ok(
                service.gerarRelatorioSemanaAnterior(
                        tecnicoId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<RelatorioSemanalTecnicoDTO>>
    gerarRelatorioPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            @RequestParam(required = false) Long tecnicoId
    ) {

        return ResponseEntity.ok(
                service.gerarRelatorioPorPeriodo(
                        inicio,
                        fim,
                        tecnicoId
                )
        );
    }
}