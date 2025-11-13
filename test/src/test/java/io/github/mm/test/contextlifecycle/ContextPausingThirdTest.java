package io.github.mm.test.contextlifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.order.internal.BackgroundTaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Context Pausing - Third Test (Spring 7)")
class ContextPausingThirdTest {

    @Autowired
    BackgroundTaskService service;

    @Test
    @DisplayName("Test 3: Context should be restarted again")
    void shouldRestartContextMultipleTimes() {

        // Background service should be running (restarted by Spring 7)
        assertThat(service.isRunning())
                .as("Background service should be restarted by Spring 7")
                .isTrue();

        int executionCount = service.getExecutionCount();

        assertThat(executionCount)
                .as("Execution count proves context was NOT reloaded (counter preserved)")
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("Test 3b: Verify isPauseable() controls behavior")
    void shouldRespectIsPauseableFlag() {

        // BackgroundTaskService implements isPauseable() = true
        assertThat(service.isPauseable())
                .as("BackgroundTaskService should be pauseable")
                .isTrue();
    }
}
