package io.github.mm.kafka;

import io.github.mm.kafka.config.TestKafkaConfiguration;
import org.springframework.boot.SpringApplication;

public class TestKafkaApplication {

    void main(String[] args) {
        SpringApplication.from(KafkaApplication::main)
                .with(TestKafkaConfiguration.class)
                .run(args);
    }
}
