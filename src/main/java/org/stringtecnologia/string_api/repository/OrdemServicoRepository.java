package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.repository.projection.MetricaOrdemServicoProjection;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository
        extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumero(String numero);

    List<OrdemServico>
    findByAparelhoAparelhoIdOrderByDataAberturaDesc(
            Long aparelhoId
    );

    Page<OrdemServico> findByClienteClienteId(
            Long clienteId,
            Pageable pageable
    );

    boolean existsByAparelhoAparelhoId(
            Long aparelhoId
    );

    Optional<OrdemServico>
    findFirstByAparelhoAparelhoIdAndStatusCodigoNotInOrderByDataAberturaDesc(
            Long aparelhoId,
            Collection<String> statusFinais
    );

    @Query(
            value = "SELECT nextval('seq_numero_ordem_servico')",
            nativeQuery = true
    )
    Long proximoNumero();

    Optional<OrdemServico>
    findFirstByAparelhoAparelhoIdOrderByDataAberturaDesc(
            Long aparelhoId
    );

    @Query("""
    SELECT
        COUNT(os) AS total,

        COALESCE(
            SUM(
                CASE
                    WHEN os.status.codigo IN (
                        'ABERTA',
                        'EM_ANALISE',
                        'AGUARDANDO_APROVACAO',
                        'EM_EXECUCAO',
                        'CONCLUIDA'
                    )
                    THEN 1
                    ELSE 0
                END
            ),
            0
        ) AS abertas,

        COALESCE(
            SUM(
                CASE
                    WHEN os.status.codigo = 'APROVADA'
                    THEN 1
                    ELSE 0
                END
            ),
            0
        ) AS autorizadas,

        COALESCE(
            SUM(
                CASE
                    WHEN os.status.codigo = 'ENTREGUE'
                    THEN 1
                    ELSE 0
                END
            ),
            0
        ) AS entregues,

        COALESCE(
            SUM(
                CASE
                    WHEN os.status.codigo = 'REPROVADA'
                    THEN 1
                    ELSE 0
                END
            ),
            0
        ) AS naoAutorizadas

    FROM OrdemServico os

    WHERE os.dataAbertura >= :inicio
      AND os.dataAbertura < :fim
    """)
    MetricaOrdemServicoProjection buscarMetricas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
    SELECT COUNT(os)
    FROM OrdemServico os
    WHERE os.status.codigo = 'ENTREGUE'
      AND os.dataEntrega >= :inicio
      AND os.dataEntrega < :fim
    """)
    long contarEntreguesNoPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}