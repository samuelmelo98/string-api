package org.stringtecnologia.string_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.model.entities.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    boolean existsByCpfCnpjAndFornecedorIdNot(String cpfCnpj, Long fornecedorId);

    boolean existsByCpfCnpj(String cpfCnpj);

    @Query("""

            SELECT f
    FROM Fornecedor f
    WHERE (:ativo IS NULL OR f.ativo = :ativo)
      AND (
          :ignorarBusca = true
          OR LOWER(f.razaoSocial) LIKE :termo
          OR LOWER(f.nomeFantasia) LIKE :termo
          OR f.cpfCnpj LIKE :termo
      )
    """)
    Page<Fornecedor> buscarFiltrados(
            @Param("ativo") Boolean ativo,
            @Param("ignorarBusca") boolean ignorarBusca,
            @Param("termo") String termo,
            Pageable pageable
    );
    }


