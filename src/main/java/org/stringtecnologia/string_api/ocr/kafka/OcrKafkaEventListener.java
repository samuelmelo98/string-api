package org.stringtecnologia.string_api.ocr.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(
        prefix = "ocr.kafka",
        name = "enabled",
        havingValue = "true"
)
public class OcrKafkaEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(OcrKafkaEventListener.class);

    private final OcrKafkaProducer producer;

    public OcrKafkaEventListener(OcrKafkaProducer producer) {
        this.producer = producer;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void publicar(OcrImagemCriadaEvent evento) {

        producer.publicar(evento.imagemId())
                .whenComplete((resultado, erro) -> {
                    if (erro != null) {
                        log.error(
                                "Falha ao publicar imagem OCR no Kafka. id={}",
                                evento.imagemId(),
                                erro
                        );
                    }
                });
    }
}

