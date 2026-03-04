package io.github.mm.graphql.infrastructure.instrumentation;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggingInstrumentation extends SimplePerformantInstrumentation {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInstrumentation.class);

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(
            InstrumentationExecutionParameters params, InstrumentationState state) {

        var mdc = MDC.putCloseable("traceId", UUID.randomUUID().toString()); // Add traceId to MDC for correlation, select from headers in real implementation

        var start = System.currentTimeMillis();
        var operationName = resolveOperationName(params);

        return SimpleInstrumentationContext.whenCompleted((_, ex) -> {
            var duration = System.currentTimeMillis() - start;
            if (ex == null) {
                // Successful field fetch
                LOGGER.atInfo()
                        .addKeyValue("operation", operationName)
                        .addKeyValue("durationMs", duration)
                        .log("GraphQL operation executed successfully");
            } else {
                // Field fetch failed
                LOGGER.atError()
                        .setMessage("GraphQL field execution failed")
                        .addKeyValue("operation", operationName)
                        .addKeyValue("durationMs", duration)
                        .addKeyValue("errorType", ex.getClass().getSimpleName())
                        .addKeyValue("errorMessage", ex.getMessage())
                        .setCause(ex)
                        .log();
            }
            mdc.close();
        });
    }

    private String resolveOperationName(InstrumentationExecutionParameters params) {
        // 1. Explicit operation name from the request
        var name = params.getOperation();
        if (name != null && !name.isBlank()) return name;

        // 2. Parse operation type from query string (query/mutation/subscription)
        var query = params.getQuery();
        if (query != null) {
            var trimmed = query.stripLeading();
            if (trimmed.startsWith("mutation")) return "<anonymous mutation>";
            if (trimmed.startsWith("subscription")) return "<anonymous subscription>";
            if (trimmed.startsWith("query")) return "<anonymous query>";
            if (trimmed.startsWith("{")) return "<anonymous shorthand query>";
        }

        return "<unknown>";
    }
}
