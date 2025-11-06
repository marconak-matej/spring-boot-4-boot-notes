package io.github.mm.kafka.demo.internal.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private final DemoKafkaProperties properties;

    public KafkaTopicConfig(DemoKafkaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public NewTopic demoTopic() {
        return TopicBuilder.name(properties.topic()).partitions(3).replicas(1).build();
    }
}
