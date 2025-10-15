# Spring Boot 4.0 Notes

A comprehensive exploration of Spring Boot 4.0 features and improvements through practical examples and demonstrations.

## Overview

This project serves as a practical guide to Spring Boot 4.0, containing multiple modules that each focus on different aspects of the framework.

## Prerequisites

- Java 25 or higher
- Maven 3.8+
- Spring Boot 4.0.0-M3

## Project Structure

```
boot-notes/
├── resilience/           # Resilience patterns and retry mechanisms
├── versioning/          # API versioning with Spring Framework 7
├── http-exchange/         # Declarative HTTP client with Spring Boot 4
└── [future-modules]     # Placeholder for upcoming modules
```

## Getting Started

1. Clone the repository
2. Build all modules:
   ```bash
   ./mvnw clean install
   ```
3. Run a specific module:
   ```bash
   ./mvnw spring-boot:run -pl <module-name>
   ```

## Modules

### 1. Resilience Module

The resilience module demonstrates Spring Boot 4.0's enhanced resilience patterns and retry mechanisms.

#### Features
- **Annotation-based Retry**
  ```java
  @Retryable(includes = GatewayTimeoutException.class, maxAttempts = 4, multiplier = 2)
  @ConcurrencyLimit(15)
  ```
  - Automatic retry on specific exceptions
  - Configurable retry attempts and backoff
  - Concurrent execution limits

- **Programmatic Retry**
  - Programmatic retry configuration using `RetryTemplate`
  - Custom retry policies
  - Flexible error handling

#### Running the Resilience Module
```bash
./mvnw spring-boot:run -pl resilience
```

### 2. Versioning Module

The versioning module showcases Spring Framework 7's first-class API versioning support through a real-world e-commerce Product catalog API.

#### Features
- **Header-Based Versioning**
  ```java
  @GetMapping(path = "/{id}", version = "1.0")
  @GetMapping(path = "/{id}", version = "2.0")
  ```
  - Native version attribute in request mappings
  - Header-based version resolution (X-API-Version)
  - Semantic version parsing and comparison

- **Breaking Changes Handling**
  - V1: Classic format (`price_usd` as decimal, `is_available` as boolean)
  - V2: Enhanced format (`price` in cents, separate `currency`, `status` enum)
  - Type-safe models prevent field leakage between versions

- **E-Commerce Product API**
  - Demonstrates real-world API evolution scenario
  - Financial data type migration (Double → Integer cents)
  - Field renaming and restructuring (breaking changes)

#### Running the Versioning Module
```bash
./mvnw spring-boot:run -pl versioning
```

#### Testing the API
```bash
# Product V1.0 - Classic format with decimal price
curl -H "API-Version: 1.0" http://localhost:8080/api/products/456

# Product V2.0 - Enhanced format with price in cents
curl -H "API-Version: 2.0" http://localhost:8080/api/products/456
```

### 3. HTTP Exchange Module

The http-exchange module demonstrates Spring Boot 4.0's declarative HTTP client capabilities using `@HttpExchange` and `@ImportHttpServices`.

#### Features
- Declarative HTTP client interfaces
- Type-safe request/response handling with Java records
- Easy configuration via `@ImportHttpServices`
- Built on Spring's modern RestClient
- Comprehensive integration tests

#### Running the HTTP Exchange Module
```bash
./mvnw spring-boot:run -pl http-exchange
```

#### Testing the Demo Endpoints
```bash
# Get all products
curl http://localhost:8080/api/demo/products

# Get a specific product by ID
curl http://localhost:8080/api/demo/products/1
```

#### Configuration
Add to `http-exchange/src/main/resources/application.properties`:
```properties
spring.http.services.product.base-url=https://api.restful-api.dev
```

### 4. [Future Modules]

_More modules will be added to demonstrate other Spring Boot 4.0 features._

## Build and Development

### Build System
- Maven-based multi-module project
- Spotless for code formatting with Palantir Java format rules
- Module-specific dependencies managed through individual POMs

### Code Style
- Consistent formatting using Spotless
- Palantir Java format rules applied
- Automated formatting during compile phase

## Version Information

- Spring Boot: 4.0.0-M3
- Java: 25
