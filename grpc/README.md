# Spring Boot gRPC Demo Service

This module demonstrates how to build a gRPC service using Spring Boot 4.0 and the official Spring gRPC dependency.

## Overview

This project implements a complete CRUD (Create, Read, Update, Delete) service for managing "Demo" entities using gRPC. It showcases:

- **Spring Boot gRPC integration** using the official `spring-grpc-dependencies`
- **Protocol Buffers** for service definition and message serialization
- **Best practices** for gRPC naming conventions and error handling
- **Comprehensive testing** with unit and integration tests

## Tech Stack

- **Spring Boot 4.0.0**
- **Spring gRPC 0.11.0**
- **Protocol Buffers (proto3)**
- **Maven** with `protobuf-maven-plugin` by io.github.ascopes
- **Java 21**

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

The gRPC server will start on port `9090` (configured in `application.properties`).

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

Key configuration properties in `application.properties`:

```properties
# gRPC Server Configuration
spring.grpc.server.port=9090

# Application Configuration
spring.application.name=grpc-demo-service
```

## Error Handling

The service uses Spring gRPC's autoconfigured exception handlers for consistent error handling across all gRPC endpoints. Instead of manual try-catch blocks, we implement `GrpcExceptionHandler` beans that automatically handle exceptions:

### Exception Handlers

1. **DemoNotFoundExceptionHandler** - Handles `DemoNotFoundException`
   - Returns gRPC status: `NOT_FOUND`
   - Used when a requested demo doesn't exist

2. **ValidationExceptionHandler** - Handles `IllegalArgumentException`
   - Returns gRPC status: `INVALID_ARGUMENT`
   - Used for validation errors (blank name, name too long, missing ID)

3. **GlobalExceptionHandler** - Handles all other exceptions
   - Returns gRPC status: `INTERNAL`
   - Fallback handler for unexpected server errors

### How It Works

All you need to do is add `@Bean` or `@Component` annotated classes of type `GrpcExceptionHandler` to your application context, and they will be automatically used by Spring gRPC to handle exceptions thrown by your services. 

A `GrpcExceptionHandler` can:
- Handle exceptions of a specific type (returning `null` for unsupported types)
- Handle all exceptions (as a global fallback)

This approach provides:
- **Cleaner service code** - No try-catch blocks needed
- **Consistent error handling** - All services use the same error mapping
- **Better separation of concerns** - Error handling logic is centralized
- **Easy to extend** - Just add new handler beans for new exception types

### Error Status Codes

| Exception Type | gRPC Status | Description |
|----------------|-------------|-------------|
| `IllegalArgumentException` | `INVALID_ARGUMENT` | Validation errors |
| `DemoNotFoundException` | `NOT_FOUND` | Resource not found |
| All others | `INTERNAL` | Unexpected errors |

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

Key dependencies in `pom.xml`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.grpc</groupId>
            <artifactId>spring-grpc-dependencies</artifactId>
            <version>${spring-grpc.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-services</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.grpc</groupId>
        <artifactId>spring-grpc-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.grpc</groupId>
        <artifactId>spring-grpc-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-testing</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Protocol Buffers Plugin

Using the `io.github.ascopes` protobuf-maven-plugin for generating Java code from `.proto` files:

```xml
<plugin>
    <groupId>io.github.ascopes</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>${protobuf-maven-plugin.version}</version>
    <configuration>
        <protocVersion>${protobuf-java.version}</protocVersion>
        <binaryMavenPlugins>
            <binaryMavenPlugin>
                <groupId>io.grpc</groupId>
                <artifactId>protoc-gen-grpc-java</artifactId>
                <version>${grpc.version}</version>
                <options>@generated=omit</options>
            </binaryMavenPlugin>
        </binaryMavenPlugins>
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
