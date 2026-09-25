package org.stringtecnologia.string_api.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.entities.OrdemServicoPagamento;
import org.stringtecnologia.string_api.model.enums.StatusPagamento;
import org.stringtecnologia.string_api.repository.projection.RelatorioSemanalTecnicoPagamentoProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoPagamentoRepository
        extends JpaRepository<OrdemServicoPagamento, Long> {

    List<OrdemServicoPagamento>
    findByOrdemServicoOrdemServicoIdAndStatusOrderByDataPagamentoAsc(
            Long ordemServicoId,
            StatusPagamento status
    );

    Optional<OrdemServicoPagamento>
    findByOrdemServicoPagamentoIdAndOrdemServicoOrdemServicoId(
            Long pagamentoId,
            Long ordemServicoId
    );

    @Query("""
        SELECT COALESCE(SUM(p.valor), 0)
        FROM OrdemServicoPagamento p
        WHERE p.ordemServico.ordemServicoId = :ordemServicoId
          AND p.status = :status
        """)
    BigDecimal somarPagamentos(
            @Param("ordemServicoId") Long ordemServicoId,
            @Param("status") StatusPagamento status
    );

    @Query("""
    SELECT
        p.ordemServico.ordemServicoId AS ordemServicoId,
        p.formaPagamento AS formaPagamento,
        p.valor AS valor,
        p.parcelas AS parcelas
    FROM OrdemServicoPagamento p
    WHERE p.ordemServico.ordemServicoId IN :ordemServicoIds
      AND p.status = :status
    ORDER BY
        p.ordemServico.ordemServicoId,
        p.dataPagamento,
        p.ordemServicoPagamentoId
    """)
    List<RelatorioSemanalTecnicoPagamentoProjection>
    buscarPagamentosPorOrdensServico(
            @Param("ordemServicoIds") List<Long> ordemServicoIds,
            @Param("status") StatusPagamento status
    );
}