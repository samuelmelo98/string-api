package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.stringtecnologia.string_api.model.entities.Cliente;

import java.util.Optional;

public interface ClienteRepository
        extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    @Query("""
            SELECT c
            FROM Cliente c
            WHERE c.ativo = :ativo
              AND (
                    :search = ''
                    OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR c.cpf LIKE CONCAT('%', :search, '%')
              )
            """)
    Page<Cliente> buscar(
            @Param("search") String search,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );


    boolean existsByCpfAndClienteIdNot(
            String cpf,
            Long clienteId
    );
}