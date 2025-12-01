# jOOQ Module

The jOOQ module demonstrates Spring Boot 4.0 integration with jOOQ (Java Object Oriented Querying) for type-safe SQL database operations, combined with Flyway migrations and PostgreSQL.

## Overview

This module showcases how to build a RESTful API for product catalog management using jOOQ for database access, providing type-safe SQL queries with compile-time verification.

## Features

- **jOOQ Type-Safe SQL**
  - Compile-time SQL validation
  - Type-safe query construction using generated classes
  - Fluent API for complex queries
  - Automatic code generation from database schema

- **Flyway Database Migrations**
  - Versioned schema migrations
  - Multi-schema support (dbo, flyway)
  - Automatic migration on startup
  - Integration with Testcontainers for code generation

- **PostgreSQL Database**
  - PostgreSQL 16 with Alpine image
  - Efficient indexing strategy
  - Audit columns (created_at, updated_at, created_by, updated_by)
  - Sequence-based ID generation starting at 101

## Database Schema

### Products Table

```sql
CREATE TABLE dbo.products (
    id             BIGINT PRIMARY KEY,
    name           VARCHAR(200) NOT NULL,
    description    TEXT,
    price          DECIMAL(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    sku            VARCHAR(50),
    category       VARCHAR(50),
    status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    CONSTRAINT chk_products_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED'))
);
```

### Indexes

- `idx_products_status` - For filtering by status
- `idx_products_category` - For filtering by category
- `idx_products_sku` - For SKU lookups

## Running the Module

### Prerequisites

- Maven 3.8+
- Docker (for Testcontainers during build)

### Generate jOOQ Sources

First, generate the jOOQ classes from the database schema:

```bash
./mvnw generate-sources -pl jooq
```

This will:
1. Start a PostgreSQL container via Testcontainers
2. Run Flyway migrations to create the schema
3. Generate jOOQ classes in `target/generated-sources/jooq`

### Build and Run

```bash
# Build the module
./mvnw clean install -pl jooq

# Run the application
./mvnw spring-boot:run -pl jooq
```

The application will start on port `8087`.

## API Endpoints

### Create Product

```bash
POST http://localhost:8087/api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop with 16GB RAM",
  "price": 1299.99,
  "stockQuantity": 50,
  "sku": "LAP-001",
  "category": "Electronics",
  "status": "ACTIVE"
}
```

### Get Product by ID

```bash
GET http://localhost:8087/api/products/101
```

### List All Products

```bash
# Basic list with pagination
GET http://localhost:8087/api/products

# Filter by status
GET http://localhost:8087/api/products?status=ACTIVE

# Filter by category
GET http://localhost:8087/api/products?category=Electronics

# Pagination
GET http://localhost:8087/api/products?page=0&size=10
```

### Update Product

```bash
PUT http://localhost:8087/api/products/101
Content-Type: application/json

{
  "name": "Gaming Laptop",
  "price": 1599.99,
  "stockQuantity": 30,
  "status": "ACTIVE"
}
```

### Delete Product

```bash
DELETE http://localhost:8087/api/products/101
```

## API Documentation

### Swagger UI

Access the interactive Swagger UI:

```
http://localhost:8087/swagger-ui.html
```

### Scalar UI

Modern API documentation interface:

```
http://localhost:8087/scalar
```

### OpenAPI JSON

Raw OpenAPI specification:

```
http://localhost:8087/v3/api-docs
```

## Testing

### Run All Tests

```bash
./mvnw test -pl jooq
```

### Test Coverage

- Integration tests with Testcontainers and PostgreSQL
- Repository layer tests with jOOQ

## jOOQ Code Generation

### Configuration

The jOOQ code generation is configured in `pom.xml` using the `testcontainers-jooq-codegen-maven-plugin`:

```xml
<plugin>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-jooq-codegen-maven-plugin</artifactId>
    <configuration>
        <database>
            <type>POSTGRES</type>
            <containerImage>postgres:16-alpine</containerImage>
        </database>
        <flyway>
            <locations>filesystem:src/main/resources/db/migration/v1</locations>
        </flyway>
        <jooq>
            <generator>
                <database>
                    <includes>.*</includes>
                    <excludes>flyway_schema_history</excludes>
                    <inputSchema>dbo</inputSchema>
                </database>
                <target>
                    <packageName>io.github.mm.jooq.generated</packageName>
                    <directory>target/generated-sources/jooq</directory>
                </target>
            </generator>
        </jooq>
    </configuration>
</plugin>
```

### Generated Classes

After running `./mvnw generate-sources -pl jooq`, you'll find:

```
target/generated-sources/jooq/
└── io/github/mm/jooq/generated/
    ├── DefaultCatalog.java
    ├── Keys.java
    ├── tables/
    │   └── Products.java
    └── tables/records/
        └── ProductsRecord.java
```

## Configuration

## Technology Stack

- **Spring Boot**: 4.0.0
- **Spring Framework**: 7.x
- **jOOQ**: 3.18.3
- **Flyway**: 9.16.3
- **PostgreSQL**: 16 (Alpine)
- **Java**: 25
- **Testcontainers**: 2.x
- **Springdoc OpenAPI**: 3.x

## Benefits of jOOQ

1. **Type Safety**: Compile-time verification of SQL queries
2. **IDE Support**: Auto-completion and refactoring support
3. **Database First**: Schema-driven development approach
4. **Performance**: No runtime query parsing overhead
5. **Flexibility**: Direct SQL access when needed
6. **Maintainability**: Breaking changes detected at compile time

## Common jOOQ Operations

### Select with Conditions

```java
dsl.selectFrom(PRODUCTS)
   .where(PRODUCTS.STATUS.eq("ACTIVE")
   .and(PRODUCTS.CATEGORY.eq("Electronics")))
   .fetch();
```

### Insert with Returning

```java
dsl.insertInto(PRODUCTS)
   .set(PRODUCTS.NAME, "New Product")
   .returningResult(PRODUCTS)
   .fetchOne();
```

### Update

```java
dsl.update(PRODUCTS)
   .set(PRODUCTS.PRICE, newPrice)
   .where(PRODUCTS.ID.eq(id))
   .execute();
```

### Pagination

```java
dsl.selectFrom(PRODUCTS)
   .limit(pageSize)
   .offset(page * pageSize)
   .fetch();
```
