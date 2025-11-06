package io.github.mm.kafka.demo.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo.kafka")
public record DemoKafkaProperties(String topic) {}
