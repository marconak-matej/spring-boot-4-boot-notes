# Beans Module

The beans module demonstrates the **different ways beans can be created and registered** in a Spring Boot 4.1 application, covering annotation scanning, `@Import`-based registration, and programmatic/manual approaches—all exercised in a single reusable `ApplicationContext`.

## Features

- ✅ **@Component / @Service / @Repository** -- Discovered via classpath component scanning
- ✅ **@Scope("prototype")** -- Non-singleton bean instances
- ✅ **@Bean factory method** -- Classic `@Configuration` class registration
- ✅ **@Primary / @Qualifier** -- Candidate selection and dependency disambiguation
- ✅ **@ConditionalOnProperty** -- Conditional bean registration based on environment properties
- ✅ **@Lazy** -- Deferred instantiation until first use
- ✅ **FactoryBean** -- Indirect bean manufacturing via `FactoryBean<T>`
- ✅ **BeanDefinitionRegistryPostProcessor** -- Register bean definitions during context bootstrap
- ✅ **@Import(@Configuration)** -- Import an external `@Configuration` class
- ✅ **@Import(ImportSelector)** -- Programmatically choose which config classes to import
- ✅ **@Import(ImportBeanDefinitionRegistrar)** -- Register `BeanDefinition`s driven by annotation metadata
- ✅ **ApplicationContextInitializer** -- Register beans during startup before context refresh
- ✅ **registerBean + Supplier** -- Register after startup via `GenericApplicationContext`
- ✅ **BeanDefinitionBuilder + registry** -- Register after startup via `BeanDefinitionRegistry`
- ✅ **registerSingleton** -- Register a pre-built singleton (no autowiring/init callbacks)

## Technology Stack

- **Spring Boot 4.1.0** - Application framework
- **Java 25** - Runtime
- **Spring WebMVC** - Web support

## Running the Application

```bash
./mvnw spring-boot:run -pl beans
```

## Bean Registration Mechanisms

The application registers beans through three main categories:

### 1. Annotation-Based (Component Scan)

| Bean Name | Mechanism | Notes |
|-----------|-----------|-------|
| `notificationComponent` | `@Component` | Standard component scan |
| `greetingService` | `@Service` | Service-layer stereotype |
| `userRepository` | `@Repository` | Data-access stereotype |
| `prototypeBean` | `@Component` + `@Scope("prototype")` | New instance per injection |

### 2. @Configuration + @Bean

| Bean Name | Mechanism | Notes |
|-----------|-----------|-------|
| `beanFromConfig` | Basic `@Bean` | Factory method in `@Configuration` |
| `stripeGateway` | `@Primary` `@Bean` | Preferred candidate of `PaymentGateway` |
| `paypalGateway` | Second `@Bean` candidate | Disambiguated via `@Qualifier` |
| `orderServiceLabel` | `@Qualifier` dependency | Injects specific `PaymentGateway` |
| `auditBean` | `@ConditionalOnProperty` | Only created if `features.audit.enabled=true` |
| `lazyBean` | `@Lazy` `@Bean` | Instantiated on first access |
| `factoryBeanExample` | `FactoryBean` via `@Bean` | `MyServiceFactoryBean` manufactures the bean |
| `beanFromRegistryPostProcessor` | `BeanDefinitionRegistryPostProcessor` | Registers a `BeanDefinition` during startup |

### 3. @Import-Based

| Bean Name | Mechanism | Notes |
|-----------|-----------|-------|
| `beanFromImportedConfig` | `@Import(@Configuration)` | Imported config class's `@Bean` methods |
| `beanFromImportSelector` | `@Import(ImportSelector)` | `ImportSelector` returns FQNs of configs to load |
| `beanFromImportRegistrar` | `@Import(ImportBeanDefinitionRegistrar)` | Registers `BeanDefinition`s directly from metadata |

### 4. Manual / Programmatic

| Bean Name | Mechanism | Notes |
|-----------|-----------|-------|
| `serviceFromInitializer` | `ApplicationContextInitializer` | Registered during startup, before context refresh |
| `serviceFromSupplier` | `registerBean(...)` + `Supplier` | Registered after startup on `GenericApplicationContext` |
| `serviceFromBeanDefinition` | `BeanDefinitionBuilder` + registry | Registered after startup via `BeanDefinitionRegistry` |
| `serviceFromSingleton` | `registerSingleton(...)` | Pre-built object; no autowiring or init callbacks |

## Expected Log Output

When you run the application, the following beans are registered and logged:

```
---- All MyService beans ----
beanFromConfig           -> registered-via-@Bean
orderServiceLabel        -> uses -> charged via PayPal
auditBean                -> registered-conditionally  (only if property enabled)
lazyBean                 -> registered-lazily
factoryBeanExample       -> registered-via-FactoryBean
beanFromRegistryPostProcessor -> registered-via-RegistryPostProcessor
beanFromImportedConfig   -> registered-via-@Import(@Configuration)
beanFromImportSelector   -> registered-via-@Import(ImportSelector)
beanFromImportRegistrar  -> registered-via-@Import(ImportBeanDefinitionRegistrar)
serviceFromInitializer   -> registered-during-startup (initializer)
serviceFromSupplier      -> registered-after-startup (registerBean + supplier)
serviceFromBeanDefinition -> registered-after-startup (BeanDefinition)
serviceFromSingleton     -> registered-after-startup (registerSingleton)

---- Annotation-scanned beans ----
Hello, Spring!
component alive
user-1
primary PaymentGateway -> charged via Stripe
```

### @Import Target Types

| Target Type | Purpose | When to Use |
|-------------|---------|-------------|
| `@Configuration` class | Import beans defined via `@Bean` methods | Reusable config modules |
| `ImportSelector` | Return FQNs of configs to import (programmatic) | Conditional config loading |
| `ImportBeanDefinitionRegistrar` | Register `BeanDefinition`s directly with metadata access | Fine-grained, metadata-driven registration (e.g., Spring Boot starters) |
