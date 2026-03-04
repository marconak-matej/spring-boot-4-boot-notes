package io.github.mm.graphql.infrastructure.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQlExceptionHandler extends DataFetcherExceptionResolverAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQlExceptionHandler.class);

    @Override
    protected GraphQLError resolveToSingleError(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {
        if (ex instanceof NotFoundException) {
            var path = env.getExecutionStepInfo().getPath().toString();
            LOGGER.atError()
                    .setMessage("GraphQL NotFoundException")
                    .addKeyValue("errorType", "NOT_FOUND")
                    .addKeyValue("errorMessage", ex.getMessage())
                    .addKeyValue("path", path)
                    .addKeyValue("fieldName", env.getField().getName())
                    .log();
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }

        if (ex instanceof IllegalArgumentException) {
            var path = env.getExecutionStepInfo().getPath().toString();
            LOGGER.atError()
                    .setMessage("GraphQL IllegalArgumentException")
                    .addKeyValue("errorType", "BAD_REQUEST")
                    .addKeyValue("errorMessage", ex.getMessage())
                    .addKeyValue("path", path)
                    .addKeyValue("fieldName", env.getField().getName())
                    .log();
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }

        LOGGER.atError()
                .setMessage("GraphQL unhandled exception")
                .addKeyValue("exceptionType", ex.getClass().getSimpleName())
                .addKeyValue("errorMessage", ex.getMessage())
                .addKeyValue("fieldName", env.getField().getName())
                .setCause(ex)
                .log();

        return null; // Let default handler deal with other exceptions
    }
}
