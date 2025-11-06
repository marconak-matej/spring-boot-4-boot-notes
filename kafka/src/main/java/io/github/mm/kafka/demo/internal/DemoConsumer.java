package io.github.mm.kafka.demo.internal;

import io.github.mm.kafka.demo.DemoEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DemoConsumer {

    private static final Logger log = LoggerFactory.getLogger(DemoConsumer.class);

    @KafkaListener(
            topics = "${demo.kafka.topic}",
            containerFactory = "shareKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.listener.concurrency}")
    public void listen(ConsumerRecord<String, DemoEvent> record) {
        var demo = record.value();
        log.info(
                "Received event for key {} from partition {} with: {} for {}",
                record.key(),
                record.partition(),
                demo.id(),
                demo.message());
    }
}
