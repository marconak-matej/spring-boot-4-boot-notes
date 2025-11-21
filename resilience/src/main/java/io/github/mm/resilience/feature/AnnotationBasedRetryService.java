package io.github.mm.resilience.feature;

import io.github.mm.resilience.client.GatewayTimeoutException;
import io.github.mm.resilience.client.RestfulApiClient;
import io.github.mm.resilience.client.domain.RestfulApiResponse;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class AnnotationBasedRetryService {

    private final RestfulApiClient client;

    public AnnotationBasedRetryService(RestfulApiClient client) {
        this.client = client;
    }

    @ConcurrencyLimit(15) // Only 15 concurrent executions allowed
    @Retryable(includes = GatewayTimeoutException.class, maxRetries = 4, multiplier = 2)
    public RestfulApiResponse processRequest(String key) {
        return this.client.getResponse(key);
    }
}
