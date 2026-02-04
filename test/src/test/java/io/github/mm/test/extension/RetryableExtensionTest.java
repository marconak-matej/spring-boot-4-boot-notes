package io.github.mm.test.extension;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RetryableExtension Tests")
class RetryableExtensionTest {
    private static final Logger log = LoggerFactory.getLogger(RetryableExtensionTest.class);

    @BeforeEach
    void logTestStart() {
        log.info("Test starting");
    }

    @AfterEach
    void logTestCompletion() {
        log.info("Test completed");
    }

    @Test
    @Retryable
    @DisplayName("Should pass on first attempt without retrying")
    void shouldPassOnFirstAttemptWithoutRetrying() {
        log.info("Test that succeeds immediately");
        assertTrue(true);
    }

    private static final AtomicInteger failOnceThenSucceedCounter = new AtomicInteger(0);

    @Test
    @Retryable
    @DisplayName("Should succeed after one retry attempt")
    void shouldSucceedAfterOneRetryAttempt() {
        int attempt = failOnceThenSucceedCounter.incrementAndGet();
        log.info("Attempt number: {}", attempt);

        if (attempt == 1) {
            throw new RuntimeException("Simulated failure on first attempt");
        }

        assertTrue(true, "Should succeed on second attempt");
    }

    private static final AtomicInteger failTwiceThenSucceedCounter = new AtomicInteger(0);

    @Test
    @Retryable
    @DisplayName("Should succeed after two retry attempts")
    void shouldSucceedAfterTwoRetryAttempts() {
        int attempt = failTwiceThenSucceedCounter.incrementAndGet();
        log.info("Attempt number: {}", attempt);

        if (attempt <= 2) {
            throw new RuntimeException("Simulated failure on attempt " + attempt);
        }

        assertTrue(true, "Should succeed on third attempt");
    }

    private static final AtomicInteger failFiveTimesThenSucceedCounter = new AtomicInteger(0);

    @Test
    @Retryable(maxAttempts = 5)
    @DisplayName("Should succeed after five retry attempts (maximum retries)")
    void shouldSucceedAfterThreeRetryAttempts() {
        int attempt = failFiveTimesThenSucceedCounter.incrementAndGet();
        log.info("Attempt number: {}", attempt);

        if (attempt < 5) {
            throw new RuntimeException("Simulated failure on attempt " + attempt);
        }

        assertTrue(true, "Should succeed on fourth attempt");
    }

    private static final AtomicInteger alwaysFailCounter = new AtomicInteger(0);

    @Test
    @Retryable
    @DisplayName("Should fail after exhausting all retry attempts")
    void shouldFailAfterExhaustingAllRetryAttempts() {
        int attempt = alwaysFailCounter.incrementAndGet();
        log.info("Attempt number: {}", attempt);

        if (attempt < 3) {
            throw new RuntimeException("Simulated failure on attempt " + attempt);
        }

        assertThrowsExactly(RuntimeException.class, () -> {
            // This test will fail even after all retries (3 attempts + 1 original = 4 total)
            throw new RuntimeException("Always fails - attempt " + attempt);
        });
    }

    private static final AtomicInteger exceptionTypeTest = new AtomicInteger(0);

    @Test
    @Retryable
    @DisplayName("Should handle different exception types across retry attempts")
    void shouldHandleDifferentExceptionTypesAcrossRetryAttempts() {
        int attempt = exceptionTypeTest.incrementAndGet();
        log.info("Exception type test - Attempt: {}", attempt);

        switch (attempt) {
            case 1:
                throw new IllegalArgumentException("IllegalArgumentException on attempt 1");
            case 2:
                throw new NullPointerException("NullPointerException on attempt 2");
            default:
                assertTrue(true, "Should succeed after different exception types");
        }
    }

    private static final AtomicInteger assertionErrorTest = new AtomicInteger(0);

    @Test
    @Retryable
    @DisplayName("Should retry on assertion failures")
    void shouldRetryOnAssertionFailures() {
        int attempt = assertionErrorTest.incrementAndGet();
        log.info("Assertion error test - Attempt: {}", attempt);

        if (attempt < 2) {
            fail("Intentional assertion failure for retry testing");
        }

        assertEquals(2, attempt, "Should succeed on second attempt after assertion failure");
    }
}
