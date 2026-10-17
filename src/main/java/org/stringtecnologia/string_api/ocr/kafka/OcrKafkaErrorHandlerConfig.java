
package org.stringtecnologia.string_api.ocr.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(
        prefix = "ocr.kafka.consumer",
        name = "enabled",
        havingValue = "true"
)
public class OcrKafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler ocrKafkaErrorHandler(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${ocr.kafka.dlt-topic}") String dltTopic
    ) {

        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (registro, erro) -> new TopicPartition(
                        dltTopic,
                        registro.partition()
                )
        );

        recoverer.setFailIfSendResultIsError(true);
        recoverer.setWaitForSendResultTimeout(
                Duration.ofSeconds(30)
        );

        var errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(5000L, 2L)
        );

        errorHandler.setAckAfterHandle(true);

        return errorHandler;
    }
}
