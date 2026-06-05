package org.stringtecnologia.string_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.Aparelho;

public interface AparelhoRepository extends JpaRepository<Aparelho, Long> {
}
