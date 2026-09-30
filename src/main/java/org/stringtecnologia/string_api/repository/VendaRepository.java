package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.entities.Venda;
import org.stringtecnologia.string_api.model.enums.StatusVenda;
import org.stringtecnologia.string_api.repository.projection.ResumoVendaProjection;

import java.time.Instant;
import java.util.Optional;

public interface VendaRepository
        extends JpaRepository<Venda, Long> {

    @Override
    @EntityGraph(
            attributePaths = {
                    "cliente"
            }
    )
    Page<Venda> findAll(Pageable pageable);

    @EntityGraph(
            attributePaths = {
                    "cliente",
                    "itens"
            }
    )
    @Query("""
            select v
            from Venda v
            where v.vendaId = :vendaId
            """)
    Optional<Venda> buscarDetalhe(
            @Param("vendaId") Long vendaId
    );

    Optional<Venda> findByNumero(String numero);

    boolean existsByNumero(String numero);

    @Query(
            value = """
                    select nextval('seq_numero_venda')
                    """,
            nativeQuery = true
    )
    Long proximoNumero();

    @Query("""
        select
            coalesce(
                sum(
                    case
                        when v.status = :statusFinalizada
                        then 1
                        else 0
                    end
                ),
                0
            ) as quantidade,

            coalesce(
                sum(
                    case
                        when v.status = :statusFinalizada
                        then v.valorTotal
                        else 0
                    end
                ),
                0
            ) as valorTotal,

            coalesce(
                sum(
                    case
                        when v.status = :statusCancelada
                        then 1
                        else 0
                    end
                ),
                0
            ) as quantidadeCanceladas,

            coalesce(
                sum(
                    case
                        when v.status = :statusCancelada
                        then v.valorTotal
                        else 0
                    end
                ),
                0
            ) as valorCancelado

        from Venda v

        where v.dataVenda >= :inicio
          and v.dataVenda < :fim
        """)
    ResumoVendaProjection resumirPeriodo(
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim,
            @Param("statusFinalizada") StatusVenda statusFinalizada,
            @Param("statusCancelada") StatusVenda statusCancelada
    );

}
