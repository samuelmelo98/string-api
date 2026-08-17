package org.stringtecnologia.string_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.Arquivo;
import java.util.Optional;
import java.util.UUID;

public interface ArquivoRepository  extends JpaRepository<Arquivo, Long> {
    Optional<Arquivo> findByUuid(UUID uuid);

}
