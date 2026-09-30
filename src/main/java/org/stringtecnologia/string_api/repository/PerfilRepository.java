package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.Perfil;

import java.util.Optional;

public interface PerfilRepository
        extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByCodigo(String codigo);
}