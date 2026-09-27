package org.stringtecnologia.string_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.Permissao;

import java.util.Optional;

public interface PermissaoRepository
        extends JpaRepository<Permissao, Long> {

    Optional<Permissao> findByCodigo(String codigo);
}
