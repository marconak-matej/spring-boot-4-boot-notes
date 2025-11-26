package io.github.mm.flyway.infrastructure.flyway;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.flyway.test.SpringBootIntegrationTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootIntegrationTest
@DisplayName("Flyway Migration Integration Tests")
class FlywayMigrationIntegrationTest {

    @Autowired
    private Flyway flyway;

    @Test
    @DisplayName("Should successfully execute all migrations")
    void shouldExecuteAllMigrationsSuccessfully() {
        var info = flyway.info();

        assertThat(info.all()).isNotEmpty();
        assertThat(info.pending()).isEmpty();
        assertThat(info.current()).isNotNull();
    }

    @Test
    @DisplayName("Should have correct migration version applied")
    void shouldHaveCorrectMigrationVersionApplied() {
        var info = flyway.info();
        var current = info.current();

        assertThat(current).isNotNull();
        assertThat(current.getVersion()).isNotNull();
        assertThat(current.getState().isApplied()).isTrue();
    }

    @Test
    @DisplayName("Should validate migration checksums")
    void shouldValidateMigrationChecksums() {
        assertThat(flyway.validateWithResult().validationSuccessful).isTrue();
    }
}
