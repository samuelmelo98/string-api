package org.stringtecnologia.string_api.ocr.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.ocr.model.OcrCodigo;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;


public interface OcrCodigoRepository extends JpaRepository<OcrCodigo, Long> {
    @Query("select c.imagem from OcrCodigo c where c.normalizado = :codigo")
    Page<OcrImagem> buscarPorCodigo(@Param("codigo") String codigo, Pageable pageable);

    void deleteByImagemId(Long imagemId);
}
