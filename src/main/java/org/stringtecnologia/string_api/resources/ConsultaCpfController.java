package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.integration.infosimples.adapter.InfosimplesCpfAdapter;
import org.stringtecnologia.string_api.integration.infosimples.dto.RestricaoSolicitacaoResponseDTO;
import org.stringtecnologia.string_api.model.entities.Cliente;
import org.stringtecnologia.string_api.services.ClienteService;


@RestController
@RequestMapping("/api/v1/cpf")
@RequiredArgsConstructor
//teste nome class
public class ConsultaCpfController {

    private final InfosimplesCpfAdapter infosimplesCpfAdapter;
    private final ClienteService clienteService;

    @GetMapping("/consulta")
    public ResponseEntity<RestricaoSolicitacaoResponseDTO> consultar(
            @RequestParam("cpf") String cpf,
            @RequestParam("birthdate") String birthdate) {

        RestricaoSolicitacaoResponseDTO resultado = null;

        // 1 - Busca no banco local
        var cliente = clienteService.buscarCliente(cpf);

        // 2 - Se encontrou no banco
        if (cliente != null) {
            resultado = new RestricaoSolicitacaoResponseDTO(cliente);
        }

        // 3 - Se não encontrou, consulta API externa
        if (cliente == null) {

            resultado = infosimplesCpfAdapter.buscarPorCpf(cpf, birthdate);

            // 4 - Se API retornou dados, salva no banco
            if (resultado != null) {

                Cliente novoCliente = new Cliente();

                novoCliente.setNome(resultado.nome());
                novoCliente.setCpf(
                        resultado.matricula().replaceAll("\\D", "")
                ); // matricula = CPF
                novoCliente.setEmail(null);
                novoCliente.setTelefone(null);
                novoCliente.setEndereco(null);
                novoCliente.setCidade(null);
                novoCliente.setEstado(null);
                novoCliente.setCep(null);

                clienteService.salvarCliente(novoCliente);
            }
        }

        // 5 - Nenhum resultado encontrado
        if (resultado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resultado);
    }


    }