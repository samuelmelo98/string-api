
package org.stringtecnologia.string_api.ocr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stringtecnologia.string_api.ocr.model.OcrOutbox;

import java.util.List;

public interface OcrOutboxRepository
        extends JpaRepository<OcrOutbox, Long> {

    @Query(
            value = """
            SELECT *
            FROM tb_ocr_outbox
            WHERE publicado_em IS NULL
            ORDER BY criado_em, id
            LIMIT 5
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true
    )
    List<OcrOutbox> buscarPendentesParaPublicacao();
}
