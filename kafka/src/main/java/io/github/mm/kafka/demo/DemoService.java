package io.github.mm.kafka.demo;

import io.github.mm.kafka.demo.internal.config.DemoKafkaProperties;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    private static final Logger log = LoggerFactory.getLogger(DemoService.class);

    private final KafkaTemplate<@NonNull String, @NonNull DemoEvent> kafkaTemplate;
    private final DemoKafkaProperties properties;

    public DemoService(
            KafkaTemplate<@NonNull String, @NonNull DemoEvent> kafkaTemplate, DemoKafkaProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public void sendMessage(String id, String message) {
        log.info("Sending message to topic '{}': {} from {}", properties.topic(), id, message);
        kafkaTemplate.send(properties.topic(), "key-" + id, new DemoEvent(id, message));
        log.info("Message sent successfully");
    }
}
