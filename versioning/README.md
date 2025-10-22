# API Versioning Module

This module demonstrates Spring Boot 4.0's first-class API versioning support using a classic e-commerce Product catalog API.

## Overview

Spring Framework 7 (Spring Boot 4.0) introduces native API versioning support through the `version` attribute in request mapping annotations. This eliminates the need for custom solutions and provides a standardized way to version APIs.

## E-Commerce Product API Example

This module demonstrates a real-world scenario: evolving an e-commerce product API with breaking changes.

### Version 1.0: Classic Product Representation
```json
{
  "id": "456",
  "title": "Coffee Maker",
  "price_usd": 49.99,
  "is_available": true
}
```

### Version 2.0: Breaking Changes
```json
{
  "id": "456",
  "title": "Coffee Maker",
  "price": 4999,
  "currency": "USD",
  "status": "IN_STOCK"
}
```

### What Changed?

**Breaking Changes from V1 to V2:**

1. **Price Representation**
   - **V1**: `price_usd` (Double) - decimal format `49.99`
   - **V2**: `price` (Integer) - cents format `4999` + separate `currency` field
   - **Reason**: Avoid floating-point precision issues in financial calculations

2. **Availability Status**
   - **V1**: `is_available` (Boolean) - simple true/false flag
   - **V2**: `status` (String) - richer status enum (`IN_STOCK`, `OUT_OF_STOCK`, `DISCONTINUED`, etc.)
   - **Reason**: More granular inventory management

3. **Currency Handling**
   - **V1**: Currency embedded in field name (`price_usd`)
   - **V2**: Separate `currency` field for internationalization
   - **Reason**: Support multiple currencies without field proliferation

## Features Demonstrated

### 1. Header-Based Versioning
The module uses the `API-Version` header to resolve API versions:
```bash
curl -H "API-Version: 1.0" http://localhost:8080/api/products/456
```

### 2. Breaking Changes Handling
```java
// Version 1.0 - Original contract
@GetMapping(path = "/{id}", version = "1.0")
public Product getProductV1(@PathVariable String id) {
    return new Product(id, "Coffee Maker", 49.99, true);
}

// Version 2.0 - New contract with breaking changes
@GetMapping(path = "/{id}", version = "2.0")
public ProductV2 getProductV2(@PathVariable String id) {
    return new ProductV2(id, "Coffee Maker", 4999, "USD", "IN_STOCK");
}
```

### 3. Type Safety
- **V1**: Uses `Double` for price and `Boolean` for availability
- **V2**: Uses `Integer` for price (cents) and `String` for status enum
- Different domain models ensure compile-time safety

## Configuration

The API versioning is configured in `ApiVersionConfig`:

```java
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {
    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.useRequestHeader("API-Version")
                .setVersionRequired(true)
                .addSupportedVersions("1.0", "2.0");
    }
}
```

### Configuration Options

- **useRequestHeader(name)**: Resolve version from HTTP header
- **setVersionRequired(boolean)**: Make version mandatory or optional
- **addSupportedVersions(...)**: Define explicitly supported versions

Other resolution strategies available:
- `useRequestParameter(name)`: Query parameter (e.g., `?version=1.0`)
- `usePathSegment(index)`: URL path (e.g., `/api/v1/products`)
- `useMediaType(parameterName)`: Media type parameter (e.g., `Accept: application/json; version=1.0`)

## Running the Module

1. Build and run:
```bash
./mvnw spring-boot:run -pl versioning
```

2. Test the endpoints:

```bash
# Product V1.0 - Classic format
curl -H "API-Version: 1.0" http://localhost:8080/api/products/456

# Response:
# {
#   "id": "456",
#   "title": "Coffee Maker",
#   "price_usd": 49.99,
#   "is_available": true
# }

# Product V2.0 - Enhanced format with breaking changes
curl -H "API-Version: 2.0" http://localhost:8080/api/products/456

# Response:
# {
#   "id": "456",
#   "title": "Coffee Maker",
#   "price": 4999,
#   "currency": "USD",
#   "status": "IN_STOCK"
# }
```

## Testing

Run the tests:
```bash
./mvnw test -pl versioning
```
