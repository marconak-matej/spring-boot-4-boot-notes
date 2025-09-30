package io.github.mm.resilience.client;

public class GatewayTimeoutException extends RuntimeException {
    public GatewayTimeoutException() {}

    public GatewayTimeoutException(Throwable cause) {
        super(cause);
    }
}
