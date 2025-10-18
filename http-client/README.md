# HTTP Client Module

This module demonstrates different ways to make HTTP requests in Spring Boot without using `@HttpExchange`.

## API Endpoints

The `DemoApi` provides a simple REST API for managing demo records:

- **POST** `/api/demos` - Create a new demo
- **PUT** `/api/demos/{id}` - Update an existing demo
- **GET** `/api/demos` - Get all demos
- **GET** `/api/demos/{id}` - Get a specific demo by ID
- **DELETE** `/api/demos/{id}` - Delete a demo

## Demo Record

```java
public record Demo(Long id, String name) {}
```

## HTTP Client Examples

This module includes comprehensive integration tests demonstrating four different HTTP client approaches:

### 1. RestClient (Spring 6.1+)
- Modern, fluent API introduced in Spring Framework 6.1
- Synchronous, blocking client
- Located in: `RestClientIntegrationTest.java`

### 2. WebClient (Spring WebFlux)
- Reactive, non-blocking client
- Supports both synchronous and asynchronous operations
- Uses Project Reactor
- Located in: `WebClientIntegrationTest.java`

### 3. RestTemplate
- Classic Spring HTTP client
- Synchronous, blocking client
- Still widely used but considered in maintenance mode
- Located in: `RestTemplateIntegrationTest.java`

### 4. TestRestTemplate
- Specialized version of RestTemplate for testing
- Automatically configured in Spring Boot tests
- Located in: `TestRestTemplateIntegrationTest.java`

## Running the Tests

```bash
mvn test
```

## Key Differences

| Client | Type | Best For | Status |
|--------|------|----------|--------|
| RestClient | Synchronous | Modern Spring apps, simple HTTP calls | ✅ Recommended |
| WebClient | Reactive | Reactive apps, high concurrency | ✅ Recommended |
| RestTemplate | Synchronous | Legacy apps | ⚠️ Maintenance mode |
| TestRestTemplate | Synchronous | Integration testing | ✅ Testing only |

## Features Demonstrated

Each test class demonstrates:
- Creating resources (POST)
- Updating resources (PUT)
- Retrieving all resources (GET)
- Retrieving single resource by ID (GET)
- Deleting resources (DELETE)
- Error handling (404 Not Found)
- Different API patterns for each client


