package org.stringtecnologia.string_api.ocr.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OcrKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public OcrKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${ocr.kafka.topic:ocr.imagens.dev}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<Void> publicar(Long imagemId) {

        if (imagemId == null) {
            throw new IllegalArgumentException("ID da imagem obrigatório");
        }

        String id = imagemId.toString();

        return kafkaTemplate.send(topic, id, id)
                .thenAccept(resultado -> {
                    var metadata = resultado.getRecordMetadata();

                    System.out.printf(
                            "OCR publicado: imagem=%s, topico=%s, particao=%d, offset=%d%n",
                            id,
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset()
                    );
                });
    }
}

