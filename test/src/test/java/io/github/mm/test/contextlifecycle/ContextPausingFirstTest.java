package io.github.mm.test.contextlifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.order.internal.BackgroundTaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Context Pausing - First Test (Spring 7)")
class ContextPausingFirstTest {

    @Autowired
    BackgroundTaskService service;

    @Test
    @DisplayName("Test 1: Background service should be running")
    void shouldHaveBackgroundServiceRunning() {

        // Background service should be running
        assertThat(service.isRunning())
                .as("Background service should be running during test execution")
                .isTrue();
    }

    @Test
    @DisplayName("Test 1b: Context should be active")
    void shouldHaveActiveContext() {

        // Background service is still running (same context)
        assertThat(service.isRunning()).isTrue();

        int executionCount = service.getExecutionCount();

        assertThat(executionCount)
                .as("Background task should have executed multiple times")
                .isGreaterThan(0);
    }
}
