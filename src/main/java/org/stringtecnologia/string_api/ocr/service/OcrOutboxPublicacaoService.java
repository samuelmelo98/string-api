package org.stringtecnologia.string_api.ocr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.ocr.repository.OcrOutboxRepository;

import java.util.concurrent.TimeUnit;

@Service
public class OcrOutboxPublicacaoService {

    private static final Logger log =
            LoggerFactory.getLogger(OcrOutboxPublicacaoService.class);

    private final OcrOutboxRepository outbox;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OcrOutboxPublicacaoService(
            OcrOutboxRepository outbox,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outbox = outbox;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void publicarPendentes() {

        var eventos = outbox.buscarPendentesParaPublicacao();

        for (var evento : eventos) {

            String imagemId = evento.getImagemId().toString();

            try {
                var resultado = kafkaTemplate.send(
                        evento.getTopico(),
                        imagemId,
                        imagemId
                ).get(30, TimeUnit.SECONDS);

                evento.marcarPublicado();

                log.info(
                        "OCR publicado. imagemId={}, particao={}, offset={}",
                        imagemId,
                        resultado.getRecordMetadata().partition(),
                        resultado.getRecordMetadata().offset()
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "Publicacao OCR interrompida", e
                );

            } catch (Exception e) {
                evento.registrarFalha(e);

                log.error(
                        "Falha ao publicar OCR. imagemId={}, tentativa={}",
                        imagemId,
                        evento.getTentativas(),
                        e
                );
            }
        }
    }
}
