package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;

public interface AparelhoRepository
        extends JpaRepository<Aparelho, Long> {

    Page<Aparelho> findByClienteClienteId(
            Long clienteId,
            Pageable pageable
    );

    @Query("""
            SELECT new org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO(
                a.aparelhoId,

                m.marcaId,
                m.nome,

                t.tipoAparelhoId,
                t.nome,

                a.modelo,
                a.modeloComercial,
                a.numeroSerie,

                s.dominioSistemaId,
                s.descricao,

                os.numero,

                a.dataCadastro,

                a.observacao
            )
            FROM Aparelho a

            JOIN a.marca m
            JOIN a.tipo t
            JOIN a.statusAparelho s

            LEFT JOIN OrdemServico os
                ON os.aparelho = a
                AND os.ordemServicoId = (
                    SELECT MAX(os2.ordemServicoId)
                    FROM OrdemServico os2
                    WHERE os2.aparelho = a
                )

            WHERE a.cliente.clienteId = :clienteId
            """)
    Page<AparelhoResponseDTO> listarPorClienteComUltimaOrdemServico(
            @Param("clienteId")
            Long clienteId,
            Pageable pageable
    );
}