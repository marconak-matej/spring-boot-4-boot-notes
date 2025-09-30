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

### 2. [Future Modules]

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
