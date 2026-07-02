# Startup Module

The startup module demonstrates the **Spring Boot 4.1 application startup lifecycle**, showcasing the precise order in which initialization hooks, callbacks, event listeners, and runners are invoked during application bootstrap.

## Features

- ✅ **ApplicationContextInitializer** -- Fires before context refresh; no beans available yet
- ✅ **@Bean Factory Method** -- Factory method logs during bean definition processing
- ✅ **Constructor** -- Bean constructor invoked during instantiation
- ✅ **@PostConstruct** -- JSR-250 lifecycle callback after bean construction
- ✅ **InitializingBean.afterPropertiesSet()** -- Spring interface callback, fires after @PostConstruct
- ✅ **@Bean(initMethod="start")** -- Custom init method configured on the @Bean annotation
- ✅ **SmartInitializingSingleton** -- Fires once all eager singleton beans are fully initialized
- ✅ **ContextRefreshedEvent** -- Published when the ApplicationContext has been refreshed
- ✅ **ApplicationStartedEvent** -- Published after context refresh, before runners
- ✅ **ApplicationRunner** -- Callback with parsed ApplicationArguments
- ✅ **CommandLineRunner** -- Callback with raw command-line arguments
- ✅ **ApplicationReadyEvent** -- Published when the application is fully up and serving (last event)
- ✅ **ApplicationListener&lt;ApplicationEvent&gt;** -- Universal listener capturing every event for full visibility

## Technology Stack

- **Spring Boot 4.1.0** - Application framework
- **Java 21** - Runtime
- **Spring WebMVC** - Web support

## Package Structure

```
src/
└── main/
    ├── java/io/github/mm/http/client/
    │   └── StartupApplication.java
    └── resources/
        └── application.yml
```

## Running the Application

```bash
./mvnw spring-boot:run -pl start-up
```

The application runs on port **8088**.

## Startup Lifecycle Execution Order

When you run the application, the startup hooks fire in this exact sequence:

| Order | Hook | Timing |
|-------|------|--------|
| 01 | `ApplicationContextInitializer` | Before context refresh, no beans available |
| 02 | `@Bean` factory method | Bean definition method executes |
| 03 | Constructor | Bean instantiation |
| 04 | `@PostConstruct` | JSR-250 lifecycle callback |
| 05 | `InitializingBean.afterPropertiesSet()` | Spring initialization callback |
| 06 | `@Bean(initMethod="start")` | Custom init method |
| 6.5 | `SmartInitializingSingleton.afterSingletonsInstantiated()` | All eager singletons ready |
| 07 | `ContextRefreshedEvent` | Context fully refreshed |
| 07b | `ApplicationStartedEvent` | After refresh, before runners |
| 08 | `ApplicationRunner.run(ApplicationArguments)` | Context ready with parsed args |
| 09 | `CommandLineRunner.run(String...)` | Context ready with raw args |
| 10 | `ApplicationReadyEvent` | Application fully up and serving |

### Expected Console Output

```
01 ApplicationContextInitializer  -> context not refreshed yet, no beans
02 @Bean factory method           -> ThirdPartyClient instance created
03 Constructor                    -> bean being instantiated
04 @PostConstruct                 -> this bean initialized (per-bean)
05 InitializingBean.afterProps... -> just after @PostConstruct (per-bean)
06 @Bean(initMethod="start")      -> external bean init callback (per-bean)
6.5 SmartInitializingSingleton    -> ALL eager singletons ready (once, during refresh)
07 ContextRefreshedEvent          -> context refreshed (once)
07b ApplicationStartedEvent       -> after refresh, before runners
08 ApplicationRunner              -> context fully ready (parsed args)
09 CommandLineRunner              -> context fully ready (raw args)
10 ApplicationReadyEvent          -> app fully up and serving (last)
```

The `ApplicationListener<ApplicationEvent>` base handler also logs every application event published during startup, providing full visibility into the entire boot sequence.
