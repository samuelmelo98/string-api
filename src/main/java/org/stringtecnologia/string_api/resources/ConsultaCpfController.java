package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.integration.infosimples.adapter.InfosimplesCpfAdapter;
import org.stringtecnologia.string_api.integration.infosimples.dto.RestricaoSolicitacaoResponseDTO;


@RestController
@RequestMapping("/api/v1/cpf")
@RequiredArgsConstructor
//teste nome class
public class ConsultaCpfController {

    private final InfosimplesCpfAdapter infosimplesCpfAdapter;

    @GetMapping("/consulta")
    public ResponseEntity<RestricaoSolicitacaoResponseDTO> consultar(
            @RequestParam("cpf") String cpf,
            @RequestParam("birthdate") String birthdate) {

        // Executa a busca através do Adapter estruturado
        RestricaoSolicitacaoResponseDTO resultado = infosimplesCpfAdapter.buscarPorCpf(cpf, birthdate);

        if (resultado == null) {
            // Retorna 404 Not Found se o CPF não for encontrado ou se a API falhar
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resultado);
    }
}