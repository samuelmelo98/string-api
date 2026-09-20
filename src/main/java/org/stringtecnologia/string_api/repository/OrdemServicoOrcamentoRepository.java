package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stringtecnologia.string_api.model.entities.OrdemServicoOrcamento;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoOrcamentoRepository
        extends JpaRepository<OrdemServicoOrcamento, Long> {

    List<OrdemServicoOrcamento>
    findByOrdemServicoOrdemServicoIdOrderByVersaoDesc(
            Long ordemServicoId
    );

    Optional<OrdemServicoOrcamento>
    findFirstByOrdemServicoOrdemServicoIdOrderByVersaoDesc(
            Long ordemServicoId
    );
}
