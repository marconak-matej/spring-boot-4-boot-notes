# Flyway Database Migrations Module

This module demonstrates **Flyway database migration management with Spring Boot 4.0**, showcasing versioned migrations, baseline migrations, callbacks, multi-schema support, and comprehensive testing strategies.

## Features

- ✅ **Flyway Integration** with Spring Boot 4.0
- ✅ **Versioned Migrations** with organized version structure (V1, V2, V3, etc.)
- ✅ **Baseline Migrations** for resetting database state
- ✅ **Multi-Schema Support** (flyway, dbo schemas)
- ✅ **Java & SQL Callbacks** for migration lifecycle events
- ✅ **Profile-Based Strategy** (migrate vs validate)
- ✅ **Comprehensive Test Coverage** with Flyway-specific integration tests
- ✅ **Spring Data JDBC** for data access
- ✅ **REST API** with OpenAPI documentation
- ✅ **Testcontainers** for PostgreSQL testing

## Technology Stack

- **Spring Boot**: 4.0.0
- **Flyway**: 10.x (via Spring Boot)
- **PostgreSQL**: 16+ (via Testcontainers)
- **Spring Data JDBC**: For repository layer

## Running the Application

### Prerequisites
Set environment variables for database connection:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/product
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=pass123
```

### Start PostgreSQL (Docker)
```bash
docker run -d \
  --name flyway-postgres \
  -e POSTGRES_DB=product \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=pass123 \
  -p 5432:5432 \
  postgres:16-alpine
```

### Run Application
```bash
# From the project root
./mvnw spring-boot:run -pl flyway

# From the flyway directory
cd flyway
../mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Run with Test Profile
```bash
# Uses Testcontainers for PostgreSQL
./mvnw spring-boot:run -pl flyway -Dspring-boot.run.profiles=test
```

## API Endpoints

### Product Management

#### Get All Products (Paginated)
```bash
curl "http://localhost:8080/api/products?page=0&size=10&sort=name,asc"
```

#### Get Product by ID
```bash
curl http://localhost:8080/api/products/1
```

#### Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Headphones",
    "description": "Premium noise-cancelling headphones",
    "price": 299.99,
    "stockQuantity": 50,
    "sku": "PROD-WH-001",
    "category": "ELECTRONICS",
    "status": "ACTIVE"
  }'
```

#### Update Product
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Product Name",
    "description": "Updated description",
    "price": 349.99,
    "stockQuantity": 75,
    "sku": "PROD-WH-001",
    "category": "ELECTRONICS",
    "status": "ACTIVE"
  }'
```

#### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

## API Documentation

### Swagger UI
Access the interactive API documentation:
```
http://localhost:8080/swagger-ui.html
```

### Scalar UI
Modern alternative to Swagger UI:
```
http://localhost:8080/scalar
```

### OpenAPI JSON
Raw OpenAPI specification:
```
http://localhost:8080/v3/api-docs
```

## Testing

### Run All Tests
```bash
./mvnw test -pl flyway
```

### Run Specific Test Classes
```bash
# Flyway migration tests
./mvnw test -pl flyway -Dtest=FlywayMigrationIntegrationTest

# Callback tests
./mvnw test -pl flyway -Dtest=FlywayCallbackIntegrationTest

# Schema validation tests
./mvnw test -pl flyway -Dtest=FlywaySchemaValidationTest

# API integration tests
./mvnw test -pl flyway -Dtest=ProductApiIntegrationTest
```

## Key Features Demonstrated

### 1. Versioned Migrations
Each migration has a version number and follows the naming convention:
```
V{version}__{description}.sql
```
Example: `V5.1__create_tags_tables.sql`

### 2. Baseline Migrations
Baseline migrations allow resetting to a known state:
```
B{version}__baseline.sql
```
Example: `B7__baseline.sql` includes the complete schema

### 3. Java Callbacks
```java
@Component
public class FlywayMigrationCallback implements Callback {
    @Override
    public void handle(Event event, Context context) {
        switch (event) {
            case BEFORE_MIGRATE -> log.info("→ Starting migration...");
            case AFTER_MIGRATE -> log.info("✓ Migration completed");
            // ...
        }
    }
}
```

### 4. SQL Callbacks
```sql
-- beforeMigrate.sql
DO $$
BEGIN
    RAISE NOTICE '→ Starting Flyway migration at %', NOW();
END$$;
```

### 5. Enum Mapping
Java enum (`ProductStatus`) maps to VARCHAR with CHECK constraint:
```java
public enum ProductStatus {
    ACTIVE, INACTIVE, DISCONTINUED
}
```
```sql
status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
CONSTRAINT chk_products_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED'))
```

## Migration Best Practices

1. **One Change Per Migration**: Each migration file should contain one logical change
2. **Never Modify Applied Migrations**: Once applied, migrations should never be modified
3. **Baseline Strategy**: Use baseline migrations for fresh starts or major schema resets
4. **Version Organization**: Group related migrations by version (v1, v2, v3)
5. **Test Migrations**: Always test migrations in a non-production environment first
6. **Validate Before Deploying**: Use `validate-on-migrate: true` in production
7. **Clean Disabled**: Always keep `clean-disabled: true` in production

## Common Tasks

### Check Migration Status
```bash
./mvnw flyway:info -pl flyway
```

### Validate Migrations
```bash
./mvnw flyway:validate -pl flyway
```

### Create New Migration
```bash
# Follow naming convention
touch src/main/resources/db/migration/v7/V7.1__add_new_feature.sql
```

### Reset Database (Development Only)
```bash
# WARNING: This will delete all data
./mvnw flyway:clean -pl flyway
./mvnw flyway:migrate -pl flyway
```

## Troubleshooting

### Migration Fails
1. Check the migration SQL syntax
2. Verify database connectivity
3. Check Flyway schema history table: `flyway.flyway_schema_history`
4. Review application logs for detailed error messages

### Checksum Mismatch
If a migration was modified after being applied:
```bash
# Option 1: Repair (use with caution)
./mvnw flyway:repair -pl flyway

# Option 2: Create a new migration to fix the issue
```
