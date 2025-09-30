package io.github.mm.resilience.feature;

import io.github.mm.resilience.client.GatewayTimeoutException;
import io.github.mm.resilience.client.RestfulApiClient;
import io.github.mm.resilience.client.domain.RestfulApiResponse;
import java.time.Duration;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProgrammaticBasedRetryService {

    private final RestfulApiClient client;
    private final RetryTemplate template;

    public ProgrammaticBasedRetryService(RestfulApiClient client) {
        this.client = client;
        var retryPolicy = RetryPolicy.builder()
                .includes(GatewayTimeoutException.class)
                .maxAttempts(4)
                .delay(Duration.ofMillis(200))
                .build();
        this.template = new RetryTemplate(retryPolicy);
    }

    public RestfulApiResponse processRequest(String key) throws RetryException {
        return template.execute(() -> this.client.getResponse(key));
    }
}
