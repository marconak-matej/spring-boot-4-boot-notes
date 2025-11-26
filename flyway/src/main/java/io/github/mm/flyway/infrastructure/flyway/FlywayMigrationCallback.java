package io.github.mm.flyway.infrastructure.flyway;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FlywayMigrationCallback implements Callback {

    private static final Logger log = LoggerFactory.getLogger(FlywayMigrationCallback.class);

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_MIGRATE
                || event == Event.BEFORE_MIGRATE
                || event == Event.AFTER_MIGRATE_ERROR
                || event == Event.BEFORE_VALIDATE
                || event == Event.AFTER_VALIDATE;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        switch (event) {
            case BEFORE_MIGRATE -> log.info("→ Starting Flyway migration...");
            case AFTER_MIGRATE -> log.info("✓ Flyway migration completed successfully");
            case AFTER_MIGRATE_ERROR -> log.error("✗ Flyway migration failed");
            case BEFORE_VALIDATE -> log.info("→ Validating migrations...");
            case AFTER_VALIDATE -> log.info("✓ Migration validation completed");
            default -> {}
        }
    }

    @Override
    public String getCallbackName() {
        return "FlywayMigrationCallback";
    }
}
