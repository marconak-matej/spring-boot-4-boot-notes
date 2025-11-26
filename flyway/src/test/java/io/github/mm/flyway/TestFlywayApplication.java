package io.github.mm.flyway;

import io.github.mm.flyway.config.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestFlywayApplication {

    public static void main(String[] args) {
        SpringApplication.from(FlywayApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
