package org.stringtecnologia.string_api.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.model.enums.StatusOrcamento;
import org.stringtecnologia.string_api.repository.projection.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository
        extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumero(
            String numero
    );

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
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );

    @Query("""
            SELECT COUNT(os)

            FROM OrdemServico os

            WHERE os.status.codigo = 'ENTREGUE'
              AND os.dataEntrega >= :inicio
              AND os.dataEntrega < :fim
            """)
    long contarEntreguesNoPeriodo(
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );

    @Query("""
            SELECT
                tecnico.id AS tecnicoId,
                tecnico.nome AS tecnicoNome,
                COUNT(os) AS quantidadeEntregues,
                SUM(COALESCE(os.valorFinal, 0)) AS valorTotal

            FROM OrdemServico os

            LEFT JOIN os.tecnicoResponsavel tecnico

            WHERE os.status.codigo = 'ENTREGUE'
              AND os.dataEntrega >= :inicio
              AND os.dataEntrega < :fim

            GROUP BY
                tecnico.id,
                tecnico.nome

            ORDER BY
                tecnico.nome,
                tecnico.id
            """)
    List<EntregaTecnicoProjection> buscarEntregasPorTecnico(
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );

    @Query("""
            SELECT
                tecnico.id AS tecnicoId,
                SUM(
                    COALESCE(
                        orcamento.valorPecas,
                        0
                    )
                ) AS valorMaterial

            FROM OrdemServicoOrcamento orcamento

            JOIN orcamento.ordemServico os
            LEFT JOIN os.tecnicoResponsavel tecnico

            WHERE os.status.codigo = 'ENTREGUE'
              AND os.dataEntrega >= :inicio
              AND os.dataEntrega < :fim
              AND orcamento.status = :status
              AND orcamento.versao = (
                    SELECT MAX(outro.versao)

                    FROM OrdemServicoOrcamento outro

                    WHERE outro.ordemServico = os
                      AND outro.status = :status
              )

            GROUP BY tecnico.id
            """)
    List<MaterialTecnicoProjection> buscarMaterialPorTecnico(
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim,
            @Param("status") StatusOrcamento status
    );

    Optional<OrdemServico> findByNumeroAndConsultaToken(
            String numero,
            String consultaToken
    );

    @Query("""
            SELECT
                os.ordemServicoId AS ordemServicoId,
                os.numero AS numeroOrdemServico,

                tecnico.id AS tecnicoId,
                tecnico.nome AS tecnicoNome,
                tecnico.email AS tecnicoEmail,

                cliente.clienteId AS clienteId,
                cliente.nome AS clienteNome,

                aparelho.aparelhoId AS aparelhoId,
                aparelho.modelo AS modelo,
                aparelho.modeloComercial AS modeloComercial,
                aparelho.numeroSerie AS numeroSerie,

                os.valorFinal AS valorFinal,
                os.valorOrcamento AS valorOrcamento,

                os.dataEntrega AS dataEntrega

            FROM OrdemServico os

            JOIN os.tecnicoResponsavel tecnico
            JOIN os.cliente cliente
            JOIN os.aparelho aparelho

            WHERE os.dataEntrega >= :inicio
              AND os.dataEntrega < :fimExclusivo
              AND os.tecnicoResponsavel IS NOT NULL

            ORDER BY
                tecnico.nome,
                os.dataEntrega,
                os.numero
            """)
    List<RelatorioSemanalTecnicoOrdemProjection>
    buscarOrdensEntreguesPorPeriodo(
            @Param("inicio") Instant inicio,
            @Param("fimExclusivo") Instant fimExclusivo
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT os

            FROM OrdemServico os

            WHERE os.ordemServicoId = :ordemServicoId
            """)
    Optional<OrdemServico> buscarPorIdParaAtualizacao(
            @Param("ordemServicoId") Long ordemServicoId
    );

    @Query("""
            SELECT
                os.ordemServicoId AS ordemServicoId,
                os.numero AS numeroOrdemServico,

                tecnico.id AS tecnicoId,
                tecnico.nome AS tecnicoNome,
                tecnico.email AS tecnicoEmail,

                cliente.clienteId AS clienteId,
                cliente.nome AS clienteNome,

                aparelho.aparelhoId AS aparelhoId,
                aparelho.modelo AS modelo,
                aparelho.modeloComercial AS modeloComercial,
                aparelho.numeroSerie AS numeroSerie,

                os.valorFinal AS valorFinal,
                os.valorOrcamento AS valorOrcamento,

                os.dataEntrega AS dataEntrega

            FROM OrdemServico os

            JOIN os.tecnicoResponsavel tecnico
            JOIN os.cliente cliente
            JOIN os.aparelho aparelho

            WHERE os.dataEntrega >= :inicio
              AND os.dataEntrega < :fimExclusivo
              AND (
                    :tecnicoId IS NULL
                    OR tecnico.id = :tecnicoId
                  )

            ORDER BY
                tecnico.nome,
                os.dataEntrega,
                os.numero
            """)
    List<RelatorioSemanalTecnicoOrdemProjection>
    buscarOrdensEntreguesPorPeriodo(
            @Param("inicio") Instant inicio,
            @Param("fimExclusivo") Instant fimExclusivo,
            @Param("tecnicoId") Long tecnicoId
    );

    @Query(
            value = """
                    SELECT
                        os.ordemServicoId AS ordemServicoId,
                        os.numero AS numero,

                        status.codigo AS statusCodigo,
                        status.descricao AS statusDescricao,

                        os.dataAbertura AS dataAbertura,

                        cliente.clienteId AS clienteId,
                        cliente.nome AS clienteNome,
                        cliente.cpf AS clienteCpf,
                        cliente.telefone AS clienteTelefone,

                        aparelho.aparelhoId AS aparelhoId,
                        marca.nome AS marca,
                        aparelho.modelo AS modelo,
                        aparelho.modeloComercial AS modeloComercial,
                        aparelho.numeroSerie AS numeroSerie

                    FROM OrdemServico os

                    JOIN os.status status
                    JOIN os.cliente cliente
                    JOIN os.aparelho aparelho
                    LEFT JOIN aparelho.marca marca

                    WHERE status.codigo IN (
                        'ABERTA',
                        'EM_ANALISE',
                        'AGUARDANDO_APROVACAO',
                        'APROVADA',
                        'EM_EXECUCAO',
                        'CONCLUIDA'
                    )
                    """,

            countQuery = """
                    SELECT COUNT(os)

                    FROM OrdemServico os

                    JOIN os.status status

                    WHERE status.codigo IN (
                        'ABERTA',
                        'EM_ANALISE',
                        'AGUARDANDO_APROVACAO',
                        'APROVADA',
                        'EM_EXECUCAO',
                        'CONCLUIDA'
                    )
                    """
    )
    Page<OrdemServicoAbertaProjection> buscarOrdensAbertas(
            Pageable pageable
    );
}