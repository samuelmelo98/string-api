package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stringtecnologia.string_api.model.entities.OrdemServico;

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
}