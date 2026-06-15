package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.stringtecnologia.string_api.integration.infosimples.dto.RestricaoSolicitacaoResponseDTO;

import org.stringtecnologia.string_api.model.dto.cliente.ClienteCreateDTO;
import org.stringtecnologia.string_api.model.dto.cliente.ClienteResponseDTO;
import org.stringtecnologia.string_api.model.dto.cliente.ClienteUpdateDTO;

import org.stringtecnologia.string_api.model.entities.Cliente;

import org.stringtecnologia.string_api.repository.ClienteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteResponseDTO criar(
            ClienteCreateDTO dto
    ) {

        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setEndereco(dto.endereco());
        cliente.setCidade(dto.cidade());
        cliente.setEstado(dto.estado());
        cliente.setCep(dto.cep());

        return toResponse(
                clienteRepository.save(cliente)
        );
    }

    public Cliente salvar(
            Cliente cliente
    ) {
        return clienteRepository.save(cliente);
    }

    public Page<ClienteResponseDTO> listar(
            Pageable pageable,
            String search
    ) {

        return clienteRepository
                .findByNomeContainingIgnoreCaseOrCpfContaining(
                        search == null ? "" : search,
                        search == null ? "" : search,
                        pageable
                )
                .map(this::toResponse);
    }

    public ClienteResponseDTO buscarPorId(
            Long clienteId
    ) {

        Cliente cliente = clienteRepository
                .findById(clienteId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cliente não encontrado"
                        ));

        return toResponse(cliente);
    }

    public Cliente buscarCliente(
            String cpf
    ) {

        return clienteRepository
                .findByCpf(cpf)
                .orElse(null);
    }

    public ClienteResponseDTO atualizar(
            Long clienteId,
            ClienteUpdateDTO dto
    ) {

        Cliente cliente = clienteRepository
                .findById(clienteId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cliente não encontrado"
                        ));

        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setEndereco(dto.endereco());
        cliente.setCidade(dto.cidade());
        cliente.setEstado(dto.estado());
        cliente.setCep(dto.cep());

        return toResponse(
                clienteRepository.save(cliente)
        );
    }

    public void excluir(
            Long clienteId
    ) {

        if (!clienteRepository.existsById(clienteId)) {
            throw new RuntimeException(
                    "Cliente não encontrado"
            );
        }

        clienteRepository.deleteById(clienteId);
    }

    public Cliente criarCliente(
            RestricaoSolicitacaoResponseDTO dto
    ) {

        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());

        cliente.setCpf(
                dto.matricula()
                        .replaceAll("\\D", "")
        );

        cliente.setEmail(null);
        cliente.setTelefone(null);
        cliente.setEndereco(null);
        cliente.setCidade(null);
        cliente.setEstado(null);
        cliente.setCep(null);

        return cliente;
    }

    private ClienteResponseDTO toResponse(
            Cliente cliente
    ) {

        return new ClienteResponseDTO(
                cliente.getClienteId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEndereco(),
                cliente.getCidade(),
                cliente.getEstado(),
                cliente.getCep(),
                cliente.getDataNascimento()
        );
    }
}