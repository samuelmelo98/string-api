package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.Aparelho;

import java.util.List;

public interface AparelhoRepository extends JpaRepository<Aparelho, Long> {

    Page<Aparelho> findByClienteClienteId(
            Long clienteId,
            Pageable pageable
    );
}
