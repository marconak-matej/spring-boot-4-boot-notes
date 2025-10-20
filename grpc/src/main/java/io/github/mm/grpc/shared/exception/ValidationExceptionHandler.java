package io.github.mm.grpc.shared.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Component
@Order(value = Integer.MAX_VALUE - 3)
public class ValidationExceptionHandler implements GrpcExceptionHandler {

    @Override
    public StatusException handleException(Throwable exception) {
        if (exception instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT
                    .withDescription(exception.getMessage())
                    .withCause(exception)
                    .asException();
        }
        return null;
    }
}
