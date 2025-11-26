package io.github.mm.flyway.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FlywayConfig {

    @Bean
    @Profile("!prod")
    public FlywayMigrationStrategy migrateStrategy() {
        return Flyway::migrate;
    }

    @Bean
    @Profile("prod")
    public FlywayMigrationStrategy validateStrategy() {
        return Flyway::validate;
    }
}
