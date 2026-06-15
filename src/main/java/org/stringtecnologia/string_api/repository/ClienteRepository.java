package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import org.stringtecnologia.string_api.model.entities.Cliente;

import java.util.Optional;

@Repository
public interface ClienteRepository
        extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(
            String cpf
    );

    Page<Cliente> findByNomeContainingIgnoreCase(
            String nome,
            Pageable pageable
    );

    Page<Cliente> findByNomeContainingIgnoreCaseOrCpfContaining(
            String nome,
            String cpf,
            Pageable pageable
    );
}