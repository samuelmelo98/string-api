package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stringtecnologia.string_api.model.entities.OrdemServicoOrcamento;
import org.stringtecnologia.string_api.model.enums.StatusOrcamento;
import org.stringtecnologia.string_api.repository.projection.RelatorioSemanalTecnicoMaterialProjection;

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

    @Query("""
    SELECT
        orcamento.ordemServico.ordemServicoId AS ordemServicoId,
        orcamento.valorPecas AS valorMaterial
    FROM OrdemServicoOrcamento orcamento
    WHERE orcamento.ordemServico.ordemServicoId IN :ordemServicoIds
      AND orcamento.status = :status
      AND orcamento.versao = (
          SELECT MAX(orcamentoVersao.versao)
          FROM OrdemServicoOrcamento orcamentoVersao
          WHERE orcamentoVersao.ordemServico.ordemServicoId =
                orcamento.ordemServico.ordemServicoId
            AND orcamentoVersao.status = :status
      )
    """)
    List<RelatorioSemanalTecnicoMaterialProjection>
    buscarMateriaisPorOrdensServico(
            @Param("ordemServicoIds") List<Long> ordemServicoIds,
            @Param("status") StatusOrcamento status
    );
}
