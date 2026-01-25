package io.github.mm.resilience.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import io.github.mm.resilience.client.GatewayTimeoutException;
import io.github.mm.resilience.client.RestfulApiClient;
import io.github.mm.resilience.client.domain.RestfulApiResponse;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ProgrammaticBasedRetryServiceTest {

    @MockitoBean
    private RestfulApiClient client;

    @Autowired
    private ProgrammaticBasedRetryService service;

    @BeforeEach
    void setUp() {
        reset(client);
    }

    @Test
    @DisplayName("Successfully processes request on first attempt")
    void successfullyProcessesRequestOnFirstAttempt() {
        var expectedResponse = new RestfulApiResponse("response body");
        when(client.getResponse(anyString())).thenReturn(expectedResponse);

        var actualResponse = service.processRequest("testKey");

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(client, times(1)).getResponse("testKey");
    }

    @Test
    @DisplayName("Retries on timeout and succeeds within max attempts")
    void retriesOnTimeoutAndSucceedsWithinMaxAttempts() {
        var expectedResponse = new RestfulApiResponse("response after retry");
        when(client.getResponse(anyString()))
                .thenThrow(new GatewayTimeoutException())
                .thenThrow(new GatewayTimeoutException())
                .thenReturn(expectedResponse);

        var actualResponse = service.processRequest("testKey");

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(client, times(3)).getResponse("testKey");
    }

    @Test
    @DisplayName("Throws exception after exceeding max retry attempts")
    void throwsExceptionAfterExceedingMaxRetryAttempts() {
        when(client.getResponse(anyString())).thenThrow(new GatewayTimeoutException());

        assertThrows(GatewayTimeoutException.class, () -> service.processRequest("testKey"));
        // Verify total attempts = 5 (1 initial + 4 retries)
        verify(client, times(5)).getResponse("testKey");
    }

    @Test
    @DisplayName("Handles null key parameter")
    void handlesNullKeyParameter() {
        var expectedResponse = new RestfulApiResponse("response for null key");
        when(client.getResponse(null)).thenReturn(expectedResponse);

        var actualResponse = service.processRequest(null);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(client, times(1)).getResponse(null);
    }

    @Test
    @DisplayName("Verifies exponential backoff behavior")
    void verifiesExponentialBackoffBehavior() {
        var expectedResponse = new RestfulApiResponse("final response");
        var startTime = System.currentTimeMillis();
        var executionTimes = new AtomicInteger(0);

        when(client.getResponse(anyString())).thenAnswer(_ -> {
            executionTimes.incrementAndGet();
            if (executionTimes.get() < 3) {
                throw new GatewayTimeoutException();
            }
            return expectedResponse;
        });

        var actualResponse = service.processRequest("testKey");
        var totalTime = System.currentTimeMillis() - startTime;

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(client, times(3)).getResponse("testKey");
        assertThat(totalTime).isGreaterThanOrEqualTo(400);
    }
}
