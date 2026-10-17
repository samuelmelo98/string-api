package org.stringtecnologia.string_api.ocr.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrStatus;
import org.stringtecnologia.string_api.ocr.repository.OcrImagemRepository;

@Component
@ConditionalOnProperty(
        prefix = "ocr.worker",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class OcrWorker {

    private final OcrImagemRepository repo;
    private final OcrProcessamentoService processamentoService;

    public OcrWorker(
            OcrImagemRepository repo,
            OcrProcessamentoService processamentoService
    ) {
        this.repo = repo;
        this.processamentoService = processamentoService;
    }

    @Scheduled(fixedDelayString = "${ocr.worker.delay-ms:3000}")
    public void executar() {

        for (OcrImagem imagem :
                repo.findTop20ByStatusOrderByCriadoEmAsc(OcrStatus.PENDENTE)) {

            try {
                processamentoService.processarPorId(imagem.id);
            } catch (Exception e) {
                // A falha já foi registrada pelo serviço de processamento.
                // O worker continua com as próximas imagens.
            }
        }
    }
}