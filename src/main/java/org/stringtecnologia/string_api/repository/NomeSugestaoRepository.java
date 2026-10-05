package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.NomeSugestao;
import org.stringtecnologia.string_api.model.enums.StatusNomeSugestao;
import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;


import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NomeSugestaoRepository
        extends JpaRepository<
        NomeSugestao,
        Long
        > {

    Optional<NomeSugestao>
    findByTipoAndNomeNormalizado(
            TipoNomeSugestao tipo,
            String nomeNormalizado
    );

    List<NomeSugestao>
    findByStatusOrderByNomeAsc(
            StatusNomeSugestao status
    );

    @Query("""
            SELECT n
              FROM NomeSugestao n
             WHERE n.status = :status
               AND n.tipo IN :tipos
               AND n.nomeNormalizado
                    LIKE CONCAT(:prefixo, '%')
             ORDER BY n.nome ASC
            """)
    List<NomeSugestao> buscarPorPrefixo(
            @Param("prefixo")
            String prefixo,

            @Param("tipos")
            Collection<TipoNomeSugestao> tipos,

            @Param("status")
            StatusNomeSugestao status,

            Pageable pageable
    );

    @Query("""
            SELECT n
              FROM NomeSugestao n
             WHERE n.dataCadastro >= :desde
                OR (
                    n.dataAtualizacao IS NOT NULL
                    AND n.dataAtualizacao >= :desde
                )
            """)
    List<NomeSugestao> buscarAlteradosDesde(
            @Param("desde")
            Instant desde
    );
}
