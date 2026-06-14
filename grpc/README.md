# Spring Boot gRPC Demo Service

This module demonstrates how to build a gRPC service using Spring Boot 4.1 and the official Spring gRPC dependency.

## Overview

This project implements a complete CRUD (Create, Read, Update, Delete) service for managing "Demo" entities using gRPC. It showcases:

- **Spring Boot gRPC Server** using native Spring Boot starter (not external library dependency)
- **Protocol Buffers (proto3)** for service and message definitions
- **Spring gRPC Exception Handling** - declarative error handling with `GrpcExceptionHandler` beans
- **In-memory storage** with ConcurrentHashMap for simple data persistence
- **Comprehensive integration tests** using Spring Boot Test with gRPC stubs
- **Best practices** for gRPC service design and error handling

## Tech Stack

- **Spring Boot 4.1.0**
- **Spring Boot gRPC Starters** (spring-boot-starter-grpc-server, spring-boot-starter-grpc-client)
- **Spring Boot Starter gRPC Test** (for integration testing)
- **Protocol Buffers (proto3)**
- **Maven** with `io.github.ascopes:protobuf-maven-plugin` for code generation
- **Java 21**
- **gRPC 1.53+** (via Spring Boot dependency management)

## gRPC Service Definition

The `DemoService` provides the following RPCs:

| RPC Method | Description | Request | Response |
|------------|-------------|---------|----------|
| `CreateDemo` | Creates a new demo | `CreateDemoRequest` | `DemoResponse` |
| `UpdateDemo` | Updates an existing demo | `UpdateDemoRequest` | `DemoResponse` |
| `GetDemo` | Retrieves a demo by ID | `GetDemoRequest` | `DemoResponse` |
| `ListDemos` | Lists all demos | `ListDemosRequest` | `ListDemosResponse` |
| `DeleteDemo` | Deletes a demo | `DeleteDemoRequest` | `DeleteDemoResponse` |

## Getting Started

### Prerequisites

- Java 21 or later
- Maven 3.9+

### Building the Project

```bash
# Build and generate protobuf stubs
./mvnw clean package

# The protobuf-maven-plugin will generate Java classes from demo.proto
# Generated sources will be in: target/generated-sources/protobuf/
```

### Running the Service

```bash
./mvnw spring-boot:run
```

The gRPC server will start on port `9090` (configured in `application.yml`).

### Testing with grpcurl

You can test the service using [grpcurl](https://github.com/fullstorydev/grpcurl):

#### Create a Demo
```bash
grpcurl -plaintext -d '{"name":"My First Demo"}' \
  localhost:9090 demo.DemoService/CreateDemo
```

#### Get a Demo
```bash
grpcurl -plaintext -d '{"id":"your-demo-id"}' \
  localhost:9090 demo.DemoService/GetDemo
```

#### List All Demos
```bash
grpcurl -plaintext -d '{}' \
  localhost:9090 demo.DemoService/ListDemos
```

#### Update a Demo
```bash
grpcurl -plaintext -d '{"id":"your-demo-id","name":"Updated Name"}' \
  localhost:9090 demo.DemoService/UpdateDemo
```

#### Delete a Demo
```bash
grpcurl -plaintext -d '{"id":"your-demo-id"}' \
  localhost:9090 demo.DemoService/DeleteDemo
```

## Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=GrpcDemoServiceIntegrationTest
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  application:
    name: grpc-demo-service
  grpc:
    server:
      port: 9090
```

**Configuration Details:**
- `spring.application.name`: Application name (used for service identification)
- `spring.grpc.server.port`: Port where gRPC server listens (default: 9090)

## Error Handling

Spring Boot gRPC provides automatic exception handling through `GrpcExceptionHandler` beans. Instead of manual try-catch blocks or status checking, handlers are registered in the Spring context and automatically invoked for matching exceptions.

### Exception Handlers in This Module

1. **NotFoundExceptionHandler** - Handles `NotFoundException`
   - Returns gRPC status: `NOT_FOUND` (Code: 5)
   - Used when a requested demo doesn't exist
   - Order: `Integer.MAX_VALUE - 2` (high priority)

2. **ValidationExceptionHandler** - Handles `IllegalArgumentException`
   - Returns gRPC status: `INVALID_ARGUMENT` (Code: 3)
   - Used for validation errors:
     - Blank or null name
     - Name exceeding 50 characters
     - Blank or null ID

3. **GlobalExceptionHandler** - Handles all other exceptions
   - Returns gRPC status: `INTERNAL` (Code: 13)
   - Fallback handler for unexpected server errors
   - Order: `Integer.MAX_VALUE` (lowest priority)

### Handler Implementation Pattern

Each handler implements `GrpcExceptionHandler` and is annotated with `@Component`:

```java
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
        return null; // Return null for unhandled exceptions
    }
}
```

### How It Works

- Spring gRPC automatically discovers and registers all `GrpcExceptionHandler` beans
- When an exception is thrown in a gRPC service method, handlers are invoked in order
- Each handler inspects the exception and returns a `StatusException` if it handles it
- Handlers return `null` if they don't handle the exception (allows chain of responsibility)
- The `@Order` annotation controls handler precedence (lower value = higher priority)

### Advantages Over Manual Error Handling

- **Cleaner Service Code** - No try-catch blocks needed in service methods
- **Consistent Error Mapping** - All services use the same error translation
- **Better Separation of Concerns** - Error handling logic is centralized and reusable
- **Easy to Extend** - Add new handlers without modifying service code
- **Framework Integration** - Works seamlessly with Spring Boot's component scanning

## Best Practices Implemented

1. **gRPC Naming Conventions**:
   - Service methods use verb-noun pattern (e.g., `CreateDemo`, `ListDemos`)
   - Messages use descriptive names with Request/Response suffixes
   - Package name follows reverse domain notation

2. **Validation**:
   - Input validation before processing
   - Clear error messages with appropriate status codes

3. **Testing**:
   - Comprehensive unit tests for business logic
   - Integration tests using in-process gRPC server
   - Test coverage for success and error scenarios

4. **Code Organization**:
   - Separation of concerns (domain, service, gRPC layer)
   - Business logic isolated from gRPC infrastructure

## Dependencies

Key dependencies from `pom.xml` (inherited via Spring Boot parent):

```xml
<dependencies>
    <!-- Core Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- gRPC Server (Spring Boot Starter) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-grpc-server</artifactId>
    </dependency>

    <!-- gRPC Services (reflection, health checks, etc.) -->
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-services</artifactId>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- gRPC Client for Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-grpc-client</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Spring Boot gRPC Server Test Utilities -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-grpc-server-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Spring Boot gRPC Client Test Utilities -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-grpc-client-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- gRPC Testing Utilities -->
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-testing</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

**Key Spring Boot Starters:**
- `spring-boot-starter-grpc-server` - Enables gRPC server auto-configuration
- `spring-boot-starter-grpc-client` - Enables gRPC client auto-configuration (test scope)
- `spring-boot-starter-grpc-server-test` - Server testing utilities
- `spring-boot-starter-grpc-client-test` - Client testing utilities

These starters are automatically managed by the Spring Boot parent POM.

## Protocol Buffers Configuration

The project uses the `io.github.ascopes:protobuf-maven-plugin` for generating Java code from `.proto` files.

### Build Configuration

```xml
<plugin>
    <groupId>io.github.ascopes</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <configuration>
        <!-- Use protoc version from parent POM -->
        <protoc>${protobuf-java.version}</protoc>
        
        <!-- Configure gRPC code generator -->
        <plugins>
            <plugin kind="binary-maven">
                <groupId>io.grpc</groupId>
                <artifactId>protoc-gen-grpc-java</artifactId>
                <options>@generated=omit</options>
            </plugin>
        </plugins>
    </configuration>
    <executions>
        <execution>
            <id>generate</id>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
