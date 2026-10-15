package org.stringtecnologia.string_api.ocr.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrStatus;
import org.stringtecnologia.string_api.ocr.repository.OcrImagemRepository;
import org.stringtecnologia.string_api.ocr.storage.OcrStorage;


@Component
public class OcrWorker {
    private final OcrImagemRepository repo;
    private final OcrIndexador indexador;
    private final OcrExtractor extractor;
    private final OcrStorage storage;

    public OcrWorker(OcrImagemRepository repo, OcrIndexador indexador, OcrExtractor extractor, OcrStorage storage) {
        this.repo = repo;
        this.indexador = indexador;
        this.extractor = extractor;
        this.storage = storage;
    }

    @Scheduled(fixedDelayString = "${ocr.worker.delay-ms:3000}")
    public void executar() {
        for (OcrImagem i : repo.findTop20ByStatusOrderByCriadoEmAsc(OcrStatus.PENDENTE)) {
            if (!indexador.iniciar(i.id)) continue;
            try {
                indexador.sucesso(i.id, extractor.extrair(storage.ler(i.storageKey)));
            } catch (Exception e) {
                indexador.falha(i.id, e);
            }
        }
    }
}
