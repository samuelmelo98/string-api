package org.stringtecnologia.string_api.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.Documento;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

//    List<Documento> findByAdiantamentoAdiantamentoId(Long adiantamentoId);

//    List<Documento> findByAdiantamentoAdiantamentoIdOrderByDataCadastroDesc(Long adiantamentoId);

//    Optional<Documento> findFirstByAdiantamentoAdiantamentoIdAndTipoDocumentoCodigoOrderByDataCadastroDesc(
//            Long adiantamentoId,
//            String codigo
//    );
}
