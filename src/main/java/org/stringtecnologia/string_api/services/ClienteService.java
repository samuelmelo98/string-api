package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.entities.Cliente;
import org.stringtecnologia.string_api.repository.ClienteRepository;

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
}
