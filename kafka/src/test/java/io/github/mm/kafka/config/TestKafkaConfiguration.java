package io.github.mm.kafka.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestKafkaConfiguration {

    @Bean
    @ServiceConnection
    public ConfluentKafkaContainer kafkaContainer() {
        //noinspection resource
        return new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:8.1.0"))
                .withEnv("KAFKA_GROUP_COORDINATOR_REBALANCE_PROTOCOLS", "classic,consumer,share")
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_SHARE_COORDINATOR_ENABLE", "true")
                .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_SHARE_COORDINATOR_STATE_TOPIC_MIN_ISR", "1")
                .withEnv("KAFKA_GROUP_SHARE_ENABLE", "true");
    }
}
