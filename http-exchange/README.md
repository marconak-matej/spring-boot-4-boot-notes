# HTTP Exchange Module

This module demonstrates Spring Boot 4.0's declarative HTTP client capabilities using `@HttpExchange` annotations and the new `@ImportHttpServices` configuration approach.

## Overview

The HTTP Exchange feature allows you to define HTTP client interfaces in a declarative way, similar to how Spring Data repositories work. This eliminates the need for boilerplate HTTP client code and provides a clean, type-safe way to interact with REST APIs.

## Key Features

- **Declarative HTTP Clients**: Define HTTP operations using interface methods annotated with `@GetExchange`, `@PostExchange`, etc.
- **Simplified Configuration**: Use `@ImportHttpServices` to automatically register HTTP service clients
- **Type-Safe**: Strongly typed request and response handling with Java records
- **Integration with RestClient**: Built on Spring's modern `RestClient` infrastructure
- **Easy Testing**: Mock HTTP interactions using `MockRestServiceServer`

## Project Structure

```
http-exchange/
├── src/main/java/
│   └── io/github/mm/http/exchange/
│       ├── HttpExchangeApplication.java          # Main application
│       ├── client/product/
│       │   ├── Product.java                      # Product record (domain model)
│       │   └── ProductService.java               # HTTP Exchange interface
│       ├── config/
│       │   └── HttpServiceConfig.java            # HTTP services configuration
│       └── demo/
│           ├── ProductDemoApi.java               # REST controller for demo
│           └── ProductDemoService.java           # Service layer
└── src/test/java/
    └── io/github/mm/http/exchange/
        └── client/product/
            └── ProductServiceIntegrationTest.java # Integration tests
```

## Core Components

### 1. ProductService Interface

The heart of the HTTP Exchange pattern - a declarative HTTP client interface:

```java
public interface ProductService {

    @GetExchange("/objects")
    List<Product> getAllProducts();

    @GetExchange("/objects/{id}")
    Product getProductById(@PathVariable String id);
}
```

**Key Points:**
- No implementation needed - Spring generates the proxy at runtime
- Uses `@GetExchange` for GET requests (similar annotations exist for POST, PUT, DELETE, etc.)
- Supports path variables with `@PathVariable`
- Returns strongly typed responses

### 2. Product Record

A simple domain model using Java records:

```java
public record Product(String id, String name, Map<String, Object> data) {}
```

This record automatically handles JSON serialization/deserialization with flexible `data` field for dynamic properties.

### 3. HttpServiceConfig

Configuration to import and register HTTP services:

```java
@Configuration
@ImportHttpServices(group = "product", types = {ProductService.class})
public class HttpServiceConfig {
}
```

**Configuration Details:**
- `@ImportHttpServices` automatically creates Spring beans for the specified HTTP service interfaces
- `group = "product"` - associates this service with a configuration group
- `types = {ProductService.class}` - specifies which interfaces to register

### 4. Demo REST API

A controller that demonstrates how to use the HTTP Exchange client:

```java
@RestController
@RequestMapping("/api/demo")
public class ProductDemoApi {

    private final ProductDemoService service;

    public ProductDemoApi(ProductDemoService demoService) {
        this.service = demoService;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable String id) {
        return service.getProductById(id);
    }
}
```

## Configuration

The HTTP service needs to be configured with a base URL. This is typically done through application properties or programmatically.

**Expected Configuration** (add to `application.properties` or `application.yml`):

```properties
# HTTP Service Configuration
spring.http.services.product.base-url=https://api.restful-api.dev
```

## Running the Application

### Prerequisites
- Java 25
- Maven 3.6+
- Spring Boot 4.0.0-M3

### Start the Application

```bash
cd http-exchange
../mvnw spring-boot:run
```

Or from the root directory:

```bash
./mvnw spring-boot:run -pl http-exchange
```

### Testing the Demo Endpoints

Once running, you can test the demo API:

```bash
# Get all products
curl http://localhost:8080/api/demo/products

# Get a specific product by ID
curl http://localhost:8080/api/demo/products/1
```

### Running Tests

```bash
cd http-exchange
../mvnw test
```

Or from the root directory:

```bash
./mvnw test -pl http-exchange
```