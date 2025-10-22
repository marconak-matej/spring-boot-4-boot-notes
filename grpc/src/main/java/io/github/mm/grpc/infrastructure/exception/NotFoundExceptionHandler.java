package io.github.mm.grpc.infrastructure.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Component
@Order(value = Integer.MAX_VALUE - 2)
public class NotFoundExceptionHandler implements GrpcExceptionHandler {

    @Override
    public StatusException handleException(Throwable exception) {
        if (exception instanceof NotFoundException) {
            return Status.NOT_FOUND
                    .withDescription(exception.getMessage())
                    .withCause(exception)
                    .asException();
        }
        return null;
    }
}
