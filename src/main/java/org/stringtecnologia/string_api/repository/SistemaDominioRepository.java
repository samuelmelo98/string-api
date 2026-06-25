package org.stringtecnologia.string_api.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.DominioSistema;

import java.util.List;
import java.util.Optional;

public interface SistemaDominioRepository extends JpaRepository<DominioSistema, Long> {

    Optional<DominioSistema>findByCategoriaAndCodigoAndAtivoTrue(String categoria, String codigo);

    List<DominioSistema> findByCategoriaAndAtivoTrueOrderByCodigoAsc(
            String categoria
    );

    List<DominioSistema> findByCategoriaAndAtivoTrueOrderByDescricaoAsc(String categoria);


}
