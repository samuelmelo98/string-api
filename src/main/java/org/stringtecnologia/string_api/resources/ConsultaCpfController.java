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
public class ConsultaCpfController {

    private final InfosimplesCpfAdapter infosimplesCpfAdapter;

    private final ClienteService clienteService;

    @GetMapping("/consulta")
    public ResponseEntity<RestricaoSolicitacaoResponseDTO> consultar(
            @RequestParam String cpf,
            @RequestParam String birthdate) {

        Cliente cliente = clienteService.buscarCliente(cpf);

        if (cliente != null) {
            return ResponseEntity.ok(
                    new RestricaoSolicitacaoResponseDTO(cliente)
            );
        }

        RestricaoSolicitacaoResponseDTO resultado =
                infosimplesCpfAdapter.buscarPorCpf(
                        cpf,
                        birthdate
                );

        if (resultado == null) {
            return ResponseEntity.notFound().build();
        }

        Cliente novoCliente = new Cliente();

        novoCliente.setNome(resultado.nome());

        novoCliente.setCpf(
                resultado.matricula()
                        .replaceAll("\\D", "")
        );


        novoCliente.setDataNascimento(resultado.dataNascimento());
        novoCliente.setEmail(null);
        novoCliente.setTelefone(null);
        novoCliente.setEndereco(null);
        novoCliente.setCidade(null);
        novoCliente.setEstado(null);
        novoCliente.setCep(null);

        clienteService.salvar(novoCliente);

        return ResponseEntity.ok(resultado);
    }
}