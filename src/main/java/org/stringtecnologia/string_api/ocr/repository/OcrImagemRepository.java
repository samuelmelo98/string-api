package org.stringtecnologia.string_api.ocr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrStatus;

import java.util.*;

public interface OcrImagemRepository extends JpaRepository<OcrImagem, Long> {
    Optional<OcrImagem> findBySha256(String sha256);

    List<OcrImagem> findTop20ByStatusOrderByCriadoEmAsc(OcrStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE OcrImagem i
       SET i.status = :processando,
           i.tentativa = i.tentativa + 1
     WHERE i.id = :id
       AND i.status = :pendente
    """)
    int iniciarProcessamento(
            @Param("id") Long id,
            @Param("pendente") OcrStatus pendente,
            @Param("processando") OcrStatus processando
    );
}
