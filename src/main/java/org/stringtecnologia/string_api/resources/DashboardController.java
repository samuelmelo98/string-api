package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.model.dto.dashboard.DashboardOrdemServicoDTO;
import org.stringtecnologia.string_api.services.DashboardService;


@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/ordens-servico")
    public ResponseEntity<DashboardOrdemServicoDTO>
    buscarMetricasOrdensServico() {

        return ResponseEntity.ok(
                dashboardService.buscarMetricasOrdensServico()
        );
    }
}
