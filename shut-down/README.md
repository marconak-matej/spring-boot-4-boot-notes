# Shutdown Module

The shutdown module demonstrates the **Spring Boot 4.1 application shutdown lifecycle**, showcasing the precise order in which destruction hooks, callbacks, event listeners, and lifecycle methods are invoked during application teardown.

## Features

- ✅ **@PreDestroy** -- Standard Jakarta annotation for local bean cleanup
- ✅ **DisposableBean.destroy()** -- Spring-specific callback for programmatic teardown
- ✅ **Custom destroyMethod** -- Defined via `@Bean(destroyMethod="cleanup")` for 3rd-party code
- ✅ **JVM shutdown hook** -- `Runtime.getRuntime().addShutdownHook(...)` for lowest-level cleanup
- ✅ **ContextClosedEvent** -- Fires when the context begins closing
- ✅ **SmartLifecycle.stop()** -- Pre-destruction phase callback for graceful shutdown

## Technology Stack

- **Spring Boot 4.1.0** - Application framework
- **Java 21** - Runtime
- **Spring WebMVC** - Web support

## Running the Application

```bash
./mvnw spring-boot:run -pl shut-down
```

The application runs on port **8089**. Press `Ctrl+C` to trigger the shutdown sequence.

## Shutdown Lifecycle Execution Order

The shutdown hooks fire in this exact sequence when the application context is closed (e.g., via `Ctrl+C` or `actuator/shutdown`):

| Order | Hook | Log Label | Thread |
|-------|------|-----------|--------|
| 01 | JVM shutdown hook | `Runtime.getRuntime().addShutdownHook()` | `Thread-1` |
| 02 | ContextClosedEvent | `@EventListener` | `ionShutdownHook` |
| 03 | SmartLifecycle.stop() | Interface callback | `ionShutdownHook` |
| 04 | @PreDestroy | `jakarta.annotation.PreDestroy` | `ionShutdownHook` |
| 05 | DisposableBean.destroy() | Interface callback | `ionShutdownHook` |
| 06 | Custom destroyMethod | `@Bean(destroyMethod="cleanup")` | `ionShutdownHook` |

> **Note:** The JVM shutdown hook fires first on its own thread (`Thread-1`), followed by the Spring shutdown sequence on the `ionShutdownHook` thread. Tomcat graceful shutdown runs between `SmartLifecycle.stop()` and `@PreDestroy`.

### Expected Console Output

```
Ctrl+C pressed
01 JVM shutdown hook                                ← Thread-1
02 ContextClosedEvent                               ← ionShutdownHook
03 SmartLifecycle.stop()                            ← ionShutdownHook
Commencing graceful shutdown. Waiting for active requests to complete
Graceful shutdown complete
04 @PreDestroy                                      ← ionShutdownHook
05 DisposableBean.destroy()                         ← ionShutdownHook
06 Custom destroyMethod                             ← ionShutdownHook
```

The `ShutdownApplication` logs each step during the shutdown lifecycle, providing full visibility into the entire teardown sequence.
