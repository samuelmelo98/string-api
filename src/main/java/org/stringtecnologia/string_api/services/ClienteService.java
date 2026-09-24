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
import org.stringtecnologia.string_api.util.exceptions.ConflitoException;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;


    public ClienteResponseDTO criar(
            ClienteCreateDTO dto
    ) {

        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());

        cliente.setCpf(
                normalizarCpf(dto.cpf())
        );

        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        cliente.setEndereco(dto.endereco());
        cliente.setCidade(dto.cidade());
        cliente.setEstado(dto.estado());
        cliente.setCep(dto.cep());

        cliente.setAtivo(true);

        return toResponse(
                clienteRepository.save(cliente)
        );
    }


    public Cliente salvar(
            Cliente cliente
    ) {

        if (cliente.getCpf() != null) {
            cliente.setCpf(
                    normalizarCpf(
                            cliente.getCpf()
                    )
            );
        }

        if (cliente.getAtivo() == null) {
            cliente.setAtivo(true);
        }

        return clienteRepository.save(
                cliente
        );
    }


    public Page<ClienteResponseDTO> listar(
            Pageable pageable,
            String search
    ) {

        return listar(
                pageable,
                search,
                true
        );
    }


    public Page<ClienteResponseDTO> listar(
            Pageable pageable,
            String search,
            Boolean ativo
    ) {

        String termo =
                search == null
                        ? ""
                        : search.trim();

        return clienteRepository
                .buscar(
                        termo,
                        ativo,
                        pageable
                )
                .map(this::toResponse);
    }

    public ClienteResponseDTO buscarPorId(
            Long clienteId
    ) {

        Cliente cliente =
                clienteRepository
                        .findById(clienteId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Cliente não encontrado"
                                        )
                        );

        return toResponse(cliente);
    }


    public Cliente buscarCliente(
            String cpf
    ) {

        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        return clienteRepository
                .findByCpf(
                        normalizarCpf(cpf)
                )
                .orElse(null);
    }


    public ClienteResponseDTO atualizar(
            Long clienteId,
            ClienteUpdateDTO dto
    ) {

        Cliente cliente =
                clienteRepository
                        .findById(clienteId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Cliente não encontrado"
                                        )
                        );

        String cpfNormalizado =
                normalizarCpf(
                        dto.cpf()
                );

        boolean cpfJaExiste =
                clienteRepository
                        .existsByCpfAndClienteIdNot(
                                cpfNormalizado,
                                clienteId
                        );

        if (cpfJaExiste) {

            throw new ConflitoException(
                    "Já existe outro cliente cadastrado com este CPF."
            );

        }

        cliente.setNome(
                dto.nome()
        );

        cliente.setCpf(
                cpfNormalizado
        );

        cliente.setEmail(
                dto.email()
        );

        cliente.setTelefone(
                dto.telefone()
        );

        cliente.setEndereco(
                dto.endereco()
        );

        cliente.setCidade(
                dto.cidade()
        );

        cliente.setEstado(
                dto.estado()
        );

        cliente.setCep(
                dto.cep()
        );

        return toResponse(
                clienteRepository.save(
                        cliente
                )
        );
    }


    /**
     * Exclusão lógica.
     *
     * Não remove cliente, aparelhos
     * ou ordens de serviço do banco.
     */
    public void excluir(
            Long clienteId
    ) {

        Cliente cliente =
                clienteRepository
                        .findById(clienteId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Cliente não encontrado"
                                        )
                        );

        if (Boolean.FALSE.equals(
                cliente.getAtivo()
        )) {

            return;
        }

        cliente.setAtivo(false);

        clienteRepository.save(
                cliente
        );
    }


    public void reativar(
            Long clienteId
    ) {

        Cliente cliente =
                clienteRepository
                        .findById(clienteId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Cliente não encontrado"
                                        )
                        );

        cliente.setAtivo(true);

        clienteRepository.save(
                cliente
        );
    }


    public Cliente criarCliente(
            RestricaoSolicitacaoResponseDTO dto
    ) {

        Cliente cliente = new Cliente();

        cliente.setNome(
                dto.nome()
        );

        cliente.setCpf(
                normalizarCpf(
                        dto.matricula()
                )
        );

        cliente.setEmail(null);
        cliente.setTelefone(null);
        cliente.setEndereco(null);
        cliente.setCidade(null);
        cliente.setEstado(null);
        cliente.setCep(null);

        cliente.setAtivo(true);

        return cliente;
    }


    public boolean existePorCpf(
            String cpf
    ) {

        if (
                cpf == null ||
                        cpf.isBlank()
        ) {
            return false;
        }

        return clienteRepository
                .existsByCpf(
                        normalizarCpf(cpf)
                );
    }


    private String normalizarCpf(
            String cpf
    ) {

        if (cpf == null) {
            return null;
        }

        return cpf.replaceAll(
                "\\D",
                ""
        );
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
                cliente.getDataNascimento(),
                cliente.getAtivo(),
                cliente.getDataCadastro()
        );
    }
}