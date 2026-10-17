
package org.stringtecnologia.string_api.ocr.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.ocr.service.OcrProcessamentoService;

@Component
@ConditionalOnProperty(
        prefix = "ocr.kafka.consumer",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class OcrKafkaConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OcrKafkaConsumer.class);

    private final OcrProcessamentoService processamentoService;

    public OcrKafkaConsumer(
            OcrProcessamentoService processamentoService
    ) {
        this.processamentoService = processamentoService;
    }

    @KafkaListener(
            topics = "${ocr.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(
            ConsumerRecord<String, String> registro,
            Acknowledgment acknowledgment
    ) {

        Long imagemId = Long.valueOf(registro.value());

        log.info(
                "Kafka OCR recebido: imagemId={}, particao={}, offset={}",
                imagemId,
                registro.partition(),
                registro.offset()
        );

        processamentoService.processarPorId(imagemId);

        acknowledgment.acknowledge();

        log.info(
                "Kafka OCR confirmado: imagemId={}, offset={}",
                imagemId,
                registro.offset()
        );
    }
}
