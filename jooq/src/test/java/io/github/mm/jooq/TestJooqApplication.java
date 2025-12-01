package io.github.mm.jooq;

import io.github.mm.jooq.config.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestJooqApplication {

    public static void main(String[] args) {
        SpringApplication.from(JooqApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
