package org.stringtecnologia.string_api.ocr.kafka;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.ocr.service.OcrOutboxPublicacaoService;

@Component
@ConditionalOnProperty(
        prefix = "ocr.kafka",
        name = "enabled",
        havingValue = "true"
)
@ConditionalOnProperty(
        prefix = "ocr.outbox.publisher",
        name = "enabled",
        havingValue = "true"
)
public class OcrOutboxPublisher {

    private final OcrOutboxPublicacaoService publicacaoService;

    public OcrOutboxPublisher(
            OcrOutboxPublicacaoService publicacaoService
    ) {
        this.publicacaoService = publicacaoService;
    }

    @Scheduled(
            fixedDelayString = "${ocr.outbox.publisher.delay-ms:5000}"
    )
    public void executar() {
        publicacaoService.publicarPendentes();
    }
}

