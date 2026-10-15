package org.stringtecnologia.string_api.ocr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrStatus;

import java.util.*;

public interface OcrImagemRepository extends JpaRepository<OcrImagem, Long> {
    Optional<OcrImagem> findBySha256(String sha256);

    List<OcrImagem> findTop20ByStatusOrderByCriadoEmAsc(OcrStatus status);
}
