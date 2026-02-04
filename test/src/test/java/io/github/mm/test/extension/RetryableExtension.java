package io.github.mm.test.extension;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class RetryableExtension implements InvocationInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RetryableExtension.class);
    private static final String RETRY_COUNT_KEY = "retry_count";

    @Override
    public void interceptTestMethod(@NonNull Invocation<Void> invocation,
                                    @NonNull ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {

        var retryable = extensionContext.getRequiredTestMethod()
                .getAnnotation(Retryable.class);

        if (retryable == null) {
            invocation.proceed();
            return;
        }

        var maxAttempts = retryable.maxAttempts();
        var store = getStore(extensionContext);

        for (var attempt = 0; attempt < maxAttempts; attempt++) {
            store.put(RETRY_COUNT_KEY, attempt);

            try {
                if (attempt == 0) {
                    // First attempt - use normal JUnit invocation (includes full lifecycle)
                    invocation.proceed();
                } else {
                    // Retry - re-invoke test method only
                    // Note: @BeforeEach/@AfterEach won't run again
                    extensionContext.getRequiredTestMethod()
                            .invoke(extensionContext.getRequiredTestInstance());
                }

                // Test passed
                if (attempt > 0) {
                    log.info("✓ Test '{}' passed on attempt {}/{}",
                            extensionContext.getDisplayName(),
                            attempt,
                            maxAttempts
                    );
                }
                return; // Exit on success

            } catch (Throwable throwable) {
                // Unwrap reflection exceptions to get the real cause
                var actualException = unwrapException(throwable);

                if (attempt < (maxAttempts - 1)) {
                    log.warn("✗ Test '{}' failed on attempt {}/{}. Error: {} - retrying...",
                            extensionContext.getDisplayName(),
                            attempt,
                            maxAttempts,
                            actualException.getMessage()
                    );
                } else {
                    log.error("✗ Test '{}' failed after {} attempts. Giving up.",
                            extensionContext.getDisplayName(),
                            maxAttempts
                    );
                    throw actualException;
                }
            }
        }
    }

    /**
     * Unwrap InvocationTargetException to get the actual test failure.
     */
    private Throwable unwrapException(Throwable throwable) {
        if (throwable instanceof java.lang.reflect.InvocationTargetException) {
            var cause = throwable.getCause();
            return cause != null ? cause : throwable;
        }
        return throwable;
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(
                ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod())
        );
    }
}