package io.github.mm.grpc.infrastructure.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Component
@Order(value = Integer.MAX_VALUE - 1)
public class GlobalExceptionHandler implements GrpcExceptionHandler {

    @Override
    public StatusException handleException(Throwable exception) {
        // This is a fallback handler for all unhandled exceptions
        return Status.INTERNAL
                .withDescription("Internal server error occurred")
                .withCause(exception)
                .asException();
    }
}
