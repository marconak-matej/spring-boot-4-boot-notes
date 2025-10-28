package io.github.mm.jms.demo.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mm.demo")
public record DemoProperties(String queue) {}
