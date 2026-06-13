package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.dto.cliente.ClienteResponseDTO;
import org.stringtecnologia.string_api.model.entities.Cliente;
import org.stringtecnologia.string_api.repository.ClienteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public boolean salvarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
        return true;
    }

    public  Cliente buscarCliente(String cpf) {
        try {
            return clienteRepository.findByCpf(cpf).get() ;
        } catch (Exception e) {
            return null;
        }



    }

    public Page<ClienteResponseDTO> listar(
            Pageable pageable,
            String search) {

        List<ClienteResponseDTO> clientes = List.of(
                new ClienteResponseDTO(
                        1L,
                        "Samuel Anderson",
                        "12345678901",
                        "samuel@email.com",
                        "(65) 99999-1111"
                ),
                new ClienteResponseDTO(
                        2L,
                        "Maria Silva",
                        "98765432100",
                        "maria@email.com",
                        "(65) 99999-2222"
                ),
                new ClienteResponseDTO(
                        3L,
                        "João Santos",
                        "11122233344",
                        "joao@email.com",
                        "(65) 99999-3333"
                )
        );

        if (search != null && !search.isBlank()) {
            clientes = clientes.stream()
                    .filter(c ->
                            c.nome().toLowerCase()
                                    .contains(search.toLowerCase()))
                    .toList();
        }

        return new PageImpl<>(
                clientes,
                pageable,
                clientes.size()
        );
    }
}
