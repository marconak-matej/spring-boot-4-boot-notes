# GraphQL Server with CRUD Operations and Pagination

A Spring Boot GraphQL server implementation with full CRUD operations and pagination support using in-memory ConcurrentHashMap storage.

## Features

- ✅ GraphQL API with Spring Boot 4.0
- ✅ Full CRUD operations for Product entities
- ✅ Pagination support for list queries
- ✅ In-memory ConcurrentHashMap storage (no database required)
- ✅ GraphiQL interface for testing

## Product Entity

```json
{
  "id": "3",
  "name": "Samsung Galaxy Z Fold",
  "data": {
    "color": "Phantom Black",
    "capacity": "512 GB",
    "generation": "3rd"
  }
}
```

## Running the Application

```bash
cd graphql
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## GraphiQL Interface

Access the GraphiQL interface at: `http://localhost:8080/graphiql`

## GraphQL Endpoint

The GraphQL endpoint is available at: `http://localhost:8080/graphql`

## Example Queries and Mutations

### Query a single product by ID

```graphql
query {
  product(id: "1") {
    id
    name
    data {
      color
      capacity
      generation
    }
  }
}
```

### Query all products with pagination

```graphql
query {
  products(page: 0, size: 10) {
    content {
      id
      name
      data {
        color
        capacity
        generation
      }
    }
    totalElements
    totalPages
    pageNumber
    pageSize
    hasNext
    hasPrevious
  }
}
```

### Create a new product

```graphql
mutation {
  createProduct(input: {
    name: "Samsung Galaxy Z Fold"
    data: {
      color: "Phantom Black"
      capacity: "512 GB"
      generation: "3rd"
    }
  }) {
    product {
      id
      name
      data {
        color
        capacity
        generation
      }
    }
  }
}
```

### Update an existing product

```graphql
mutation {
  updateProduct(id: "3", input: {
    name: "Samsung Galaxy Z Fold 5"
    data: {
      color: "Phantom Black"
      capacity: "1 TB"
      generation: "5th"
    }
  }) {
    product {
      id
      name
      data {
        color
        capacity
        generation
      }
    }
  }
}
```

### Delete a product

```graphql
mutation {
  deleteProduct(id: "3") {
    success
  }
}
```

## Pagination Parameters

- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 10)

The `ProductPage` response includes:
- `content`: List of products on the current page
- `totalElements`: Total number of products
- `totalPages`: Total number of pages
- `pageNumber`: Current page number
- `pageSize`: Size of the page
- `hasNext`: Whether there's a next page
- `hasPrevious`: Whether there's a previous page

## Using curl to Call the GraphQL API

All GraphQL requests are sent as POST requests to `/graphql` with a JSON payload containing the `query` field.

### Query a Product by ID

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { product(id: \"1\") { id name data { color capacity generation } } }"
  }' | jq
```

### Query Products with Pagination

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { products(page: 0, size: 5) { content { id name data { color capacity generation } } totalElements pageNumber pageSize hasNext hasPrevious } }"
  }' | jq
```

### Create a Product

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createProduct(input: { name: \"Samsung Galaxy Z Fold\", data: { color: \"Phantom Black\", capacity: \"512 GB\", generation: \"3rd\" } }) { product { id name data { color capacity generation } } } }"
  }' | jq
```

### Update a Product

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { updateProduct(id: \"3\", input: { name: \"Samsung Galaxy Z Fold 5\", data: { color: \"Icy Blue\", capacity: \"1 TB\", generation: \"5th\" } }) { product { id name data { color capacity generation } } } }"
  }' | jq
```

### Delete a Product

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { deleteProduct(id: \"12bb9546-9d98-4302-9a43-06e85cf73452\") { success } }"
  }' | jq
```

### Pretty Print JSON Output

Use `jq` to format the JSON response:

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { products(page: 0, size: 10) { content { id name data { color capacity generation } } totalElements } }"
  }' | jq
```

### Using Variables in curl

    "query": "query GetProduct($id: ID!) { product(id: $id) { id name data { color capacity generation } } }",

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query GetProduct($id: String!) { productById(id: $id) { id name data { color capacity generation } } }",
    "variables": {
      "id": "1"
    }
  }' | jq
```

## Testing

Run tests with:

```bash
mvn test
```

The project includes:
- Unit tests with `@GraphQlTest`
- Integration tests with `@SpringBootTest`
- Full CRUD lifecycle tests
- Pagination tests

## Technologies Used

- Spring Boot 4.0.0-RC1
- Spring for GraphQL
- ConcurrentHashMap for in-memory storage
- Java 21


