# Spring Boot 4.0 Notes

A comprehensive exploration of Spring Boot 4.0 features and improvements through practical examples and demonstrations.

## Overview

This project serves as a practical guide to Spring Boot 4.0, containing multiple modules that each focus on different aspects of the framework.

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- Spring Boot 4.0.0

## Project Structure

```
boot-notes/
├── resilience/           # Resilience patterns and retry mechanisms
├── versioning/          # API versioning with Spring Framework 7
├── http-exchange/        # Declarative HTTP client with Spring Boot 4
├── http-client/          # HTTP client examples (RestClient, WebClient, RestTemplate)
├── grpc/                 # gRPC service with Spring Boot 4 and Protocol Buffers
├── graphql/              # GraphQL API with CRUD operations and pagination
├── jms/                  # JMS messaging with JmsClient and embedded Artemis
├── kafka/                # Kafka integration with Share Groups and Testcontainers
├── test/                 # Spring Boot 4.0 & Spring Framework 7 testing enhancements
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
  class Config{}
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
  public String a(){}
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

### 4. HTTP Client Module

The http-client module demonstrates different ways to make HTTP requests in Spring Boot without using `@HttpExchange`. It includes a DemoApi REST endpoint and comprehensive integration tests showing various HTTP client approaches.

#### Features
- **DemoApi REST Endpoints**
  - POST `/api/demos` - Create a new demo
  - PUT `/api/demos/{id}` - Update an existing demo
  - GET `/api/demos` - Get all demos
  - GET `/api/demos/{id}` - Get a specific demo by ID
  - DELETE `/api/demos/{id}` - Delete a demo

- **HTTP Client Integration Tests**
  - **RestClient** - Modern, fluent API (Spring 6.1+)
  - **WebClient** - Reactive, non-blocking client (Spring WebFlux)
  - **RestTemplate** - Classic Spring HTTP client (maintenance mode)
  - **TestRestTemplate** - Specialized for integration testing

#### Running the HTTP Client Module
```bash
./mvnw spring-boot:run -pl http-client
```

#### Testing the DemoApi
```bash
# Create a demo
curl -X POST http://localhost:8083/api/demos -H "Content-Type: application/json" -d '{"name":"Test Demo"}'

# Get all demos
curl http://localhost:8083/api/demos

# Get a specific demo
curl http://localhost:8083/api/demos/1

# Update a demo
curl -X PUT http://localhost:8083/api/demos/1 -H "Content-Type: application/json" -d '{"name":"Updated Demo"}'

# Delete a demo
curl -X DELETE http://localhost:8083/api/demos/1
```

#### Running Integration Tests
```bash
# Run all tests
./mvnw test -pl http-client

# Run specific test class
./mvnw test -pl http-client -Dtest=RestClientIntegrationTest
```

### 5. gRPC Module

The gRPC module showcases how to create and consume gRPC services in Spring Boot 4.0 using Protocol Buffers.

#### Features
- **Protocol Buffers Integration**
  - Define services and messages using `.proto` files
  - Automatic generation of Java classes from `.proto` definitions
  - Seamless integration with Spring Boot's build process

- **gRPC Service Example**
  - A simple gRPC service implementation
  - Bidirectional streaming and unary call examples
  - Error handling and metadata usage in gRPC

- **gRPC Client Example**
  - Consuming the gRPC service from a Spring Boot application
  - Asynchronous and blocking stub usage
  - Interceptors for logging and authentication

#### Running the gRPC Module
```bash
./mvnw spring-boot:run -pl grpc
```

### 6. GraphQL Module

The graphql module demonstrates building a GraphQL API with Spring Boot 4.0, featuring full CRUD operations, pagination support, and in-memory storage.

#### Features
- **GraphQL API with Spring for GraphQL**
  - Full CRUD operations for Product entities
  - GraphiQL interface for interactive testing
  - In-memory ConcurrentHashMap storage (no database required)

- **Pagination Support**
  - Page-based queries with customizable page size
  - Complete pagination metadata (totalElements, totalPages, hasNext, hasPrevious)
  - Efficient in-memory pagination

- **Product Entity**
  - ID, name, and flexible data structure
  - Nested data object for product specifications (color, capacity, generation)
  - UUID-based unique identifiers

#### Running the GraphQL Module
```bash
./mvnw spring-boot:run -pl graphql
```

### 7. JMS Module

The jms module demonstrates Spring Framework 7.0's new **JmsClient** API for simplified JMS messaging operations with embedded Apache ActiveMQ Artemis.

#### Features
- **JmsClient - Modern Fluent API**
  ```
  jmsClient.destination("demo-queue").send(message);
  ```
  - New fluent API following the design of JdbcClient and RestClient
  - Simplified message sending and receiving
  - Throws `MessagingException` for consistency with Spring's messaging abstraction

- **Embedded Artemis Broker**
  - Zero-configuration embedded JMS broker
  - Non-persistent mode for development
  - Automatic queue creation

- **Producer/Consumer Pattern**
  - REST API endpoint to trigger message sending
  - `@JmsListener` for message consumption
  - Demonstrates asynchronous message processing

#### Running the JMS Module
```bash
./mvnw spring-boot:run -pl jms
```

#### Sending Messages via REST API
```bash
# Send a message to the JMS queue
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from JmsClient!"}'
```

#### JmsClient vs JmsTemplate
- **Fluent API**: Chainable method calls for better readability
- **Modern Design**: Consistent with Spring 6.1+ client APIs
- **Simplified Operations**: Common operations are more straightforward
- **MessagingException**: Better exception handling alignment

### 8. Kafka Module

The kafka module demonstrates Apache Kafka integration with Spring Boot 4.0, showcasing **Kafka Share Groups** (new feature), modern messaging patterns, and Testcontainers for local development.

#### Features
- **Kafka Share Groups - New in Spring Boot 4.0**
  ```java
  @KafkaListener(
      topics = "${demo.kafka.topic}",
      containerFactory = "shareKafkaListenerContainerFactory",
      concurrency = "${spring.kafka.listener.concurrency}")
  public void listen(ConsumerRecord<String, DemoEvent> record) {
      // Automatic load balancing across consumers
  }
  ```
  - Automatic load balancing across consumer instances
  - No manual offset management required
  - Simplified consumer group coordination
  - `ShareKafkaListenerContainerFactory` and `ShareConsumerFactory` configuration

- **Producer & Consumer Pattern**
  - `KafkaTemplate` for message publishing
  - `@KafkaListener` with Share Groups for consumption
  - JSON serialization/deserialization out of the box
  - Separate DTOs for REST (`Demo`) and Kafka (`DemoEvent`)

- **Testcontainers Integration**
  - `@ServiceConnection` for automatic Kafka configuration
  - Confluent Kafka 8.1.0+ with Share Groups enabled
  - Zero-configuration local development
  - `TestKafkaApplication` for running with embedded Kafka

- **Auto-configured Topics**
  - `TopicBuilder` for declarative topic creation
  - Configurable partitions and replication factor
  - Automatic topic provisioning on startup

#### Running the Kafka Module
```bash
# Run with Testcontainers (recommended for local development)
./mvnw spring-boot:run -pl kafka

# Or run the test application directly
./mvnw test -pl kafka -Dtest=TestKafkaApplication
```

#### Sending Messages via REST API
```bash
# Send a demo message to Kafka
curl -X POST http://localhost:8080/api/demos \
  -H "Content-Type: application/json" \
  -d '{"id": "demo-123", "message": "Hello from Kafka Share Groups!"}'
```

#### Kafka Share Groups vs Traditional Consumer Groups
- **Share Groups**: Messages distributed across all instances automatically
- **Traditional**: Each consumer group gets all messages (fan-out)
- **Use Case**: Share Groups ideal for work queue patterns with load balancing
- **Spring Boot 4.0**: First-class support for Kafka Share Groups (KIP-932)

### 9. Testing Module

The testing module demonstrates Spring Boot 4.0 and Spring Framework 7's testing improvements, showcasing modern testing practices with JUnit Jupiter (JUnit 5), RestTestClient with AssertJ, context lifecycle management, bean overriding, and @Nested test support.

#### Features
- **JUnit 4 → JUnit 5 Migration**
  - Side-by-side comparison of JUnit 4 vs JUnit 5
  - Migration guide with before/after examples
  - Modern `@SpringJUnitConfig` annotations
  - Spring 7 deprecates JUnit 4 support

- **RestTestClient with AssertJ** (Spring Boot 4.0 RC2)
  - Live server testing with `@SpringBootTest`
  - MockMvc-backed testing
  - Fluent AssertJ integration for readable assertions

- **Context Pausing & Restarting** (Spring 7)
  - Automatic context lifecycle management
  - `SmartLifecycle#isPauseable()` demonstration
  - Performance improvements for test suites

- **Enhanced @Nested Test Support** (Spring 7)
  - Consistent dependency injection across all nesting levels
  - Method parameter injection in lifecycle methods
  - Better test organization and structure

- **Prototype Bean Mocking** (Spring 7)
  - `@MockitoBean` now works with prototype beans
  - `@TestBean` vs `@MockitoBean` comparison
  - Non-singleton bean override strategies

- **ApplicationEvents Testing** (Spring Framework 7)
  - `@RecordApplicationEvents` annotation
  - Event verification in tests
  - Simplified application event testing

- **Testcontainers 2.x Integration** (Spring Boot 4.0)
  - `@ServiceConnection` for automatic configuration
  - Container lifecycle management
  - Zero-configuration integration tests

#### Running the Testing Module
```bash
./mvnw spring-boot:run -pl test
```

#### Running Tests
```bash
# Run all tests
./mvnw test -pl test

# Run specific test class
./mvnw test -pl test -Dtest=JUnit5ComposedAnnotationsTest

# Run specific test method
./mvnw test -pl test -Dtest=JUnit5ComposedAnnotationsTest#shouldCreateProduct
```

### 10. [Future Modules]

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

- Spring Boot: 4.0.0
- Java: 21

## API Documentation

All REST API modules include comprehensive **OpenAPI 3.0** documentation using **Springdoc OpenAPI**.

### Accessing Documentation

After starting any module, you can access:

- **OpenAPI JSON Specification**: `http://localhost:<port>/v3/api-docs`
- **Swagger UI**: `http://localhost:<port>/swagger-ui.html`
- **Scalar UI**: `http://localhost:<port>/scalar`

### Module Ports

- **http-exchange**: `8081`
- **versioning**: `8082`
- **http-client**: `8083`
- **jms**: `8084`
- **kafka**: `8085`
- **test**: `8086`

### Features

- **Global API Metadata**: Title, version, description, contact, and license info
- **Complete Endpoint Documentation**: All operations include summaries, descriptions, and examples
- **Request/Response Models**: Full schema documentation with field descriptions and examples
- **Error Responses**: All possible HTTP status codes documented
- **Interactive Testing**: Use Swagger UI to test endpoints directly

### Example: Testing via Swagger UI

1. Start a module (e.g., http-client)
   ```bash
   ./mvnw spring-boot:run -pl http-client
   ```

2. Open Swagger UI in your browser
   ```
   http://localhost:8083/swagger-ui.html
   ```

3. Explore and test the API endpoints interactively
