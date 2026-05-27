package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stringtecnologia.string_api.model.entities.Cliente;
import org.stringtecnologia.string_api.model.entities.DocumentTemplate;

import java.util.Optional;


@Repository

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

}
