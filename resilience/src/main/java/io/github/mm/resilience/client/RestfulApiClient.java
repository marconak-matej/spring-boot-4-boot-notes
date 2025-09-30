package io.github.mm.resilience.client;

import io.github.mm.resilience.client.domain.RestfulApiResponse;
import org.springframework.stereotype.Component;

@Component
public class RestfulApiClient {

    public RestfulApiResponse getResponse(String key) {
        return new RestfulApiResponse("Body for key: " + key);
    }
}
