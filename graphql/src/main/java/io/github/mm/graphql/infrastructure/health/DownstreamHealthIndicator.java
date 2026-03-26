package io.github.mm.graphql.infrastructure.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DownstreamHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        if (checkExternalService()) {
            return Health.up().build();
        }
        return Health.down().withDetail("error", "Downstream unavailable").build();
    }

    private boolean checkExternalService() {
        return true;
    }
}
