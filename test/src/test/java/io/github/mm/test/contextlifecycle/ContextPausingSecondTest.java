package io.github.mm.test.contextlifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.order.internal.BackgroundTaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Context Pausing - Second Test (Spring 7)")
class ContextPausingSecondTest {

    @Autowired
    BackgroundTaskService service;

    @Test
    @DisplayName("Test 2: Context should be restarted from paused state")
    void shouldRestartContextAutomatically() {

        // Background service should be running again (restarted by Spring 7)
        assertThat(service.isRunning())
                .as("Background service should be restarted by Spring 7")
                .isTrue();

        int executionCount = service.getExecutionCount();

        assertThat(executionCount)
                .as("Background task should continue counting (context not reloaded)")
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 2b: Verify context caching works")
    void shouldUseCachedContext() {

        // Same context, still running
        assertThat(service.isRunning()).isTrue();
    }
}
