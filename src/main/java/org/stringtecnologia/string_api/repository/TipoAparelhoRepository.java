package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.TipoAparelho;

import java.util.List;

public interface TipoAparelhoRepository
        extends JpaRepository<TipoAparelho, Long> {

    List<TipoAparelho> findByAtivoTrueOrderByNomeAsc();
}