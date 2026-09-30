package org.stringtecnologia.string_api.resources;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.model.dto.venda.DashboardVendaDTO;
import org.stringtecnologia.string_api.services.DashboardVendaService;

@RestController
@RequestMapping(
        "/api/dashboard/vendas"
)
@RequiredArgsConstructor
public class DashboardVendaController {

    private final DashboardVendaService
            dashboardVendaService;


    @GetMapping
    public DashboardVendaDTO buscar() {

        return dashboardVendaService
                .buscar();
    }

}
