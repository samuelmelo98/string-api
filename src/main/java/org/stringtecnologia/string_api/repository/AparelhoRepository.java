package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.OrdemServico;

import java.util.List;
import java.util.Optional;

public interface AparelhoRepository extends JpaRepository<Aparelho, Long> {

    Page<Aparelho> findByClienteClienteId(
            Long clienteId,
            Pageable pageable
    );

}
