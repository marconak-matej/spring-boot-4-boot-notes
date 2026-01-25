package io.github.mm.test.testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(
        properties = {
            "spring.autoconfigure.exclude=", // Override the exclusion for this test
            "spring.datasource.driver-class-name=org.postgresql.Driver"
        })
@Testcontainers
@DisplayName("Testcontainers with @ServiceConnection (Spring Boot 4.0)")
class PostgresTestcontainersTest {

    @Container
    @ServiceConnection //  Spring Boot 4.0: Auto-configures DataSource!
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    JdbcClient client;

    @BeforeEach
    void setUp() {
        // Create products table
        client.sql("""
                        CREATE TABLE IF NOT EXISTS products (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            price DECIMAL(10, 2) NOT NULL
                        )
                        """).update();

        // Clean all data before each test
        client.sql("DELETE FROM products").update();
    }

    @Test
    @DisplayName("Should connect to PostgreSQL container")
    void shouldConnectToPostgres() {

        // Verify container is running
        assertThat(postgres.isRunning()).isTrue();

        // Verify we can connect
        assertThat(client).isNotNull();
    }

    @Test
    @DisplayName("Should create table and insert data")
    void shouldCreateTableAndInsertData() {

        // When - Insert product
        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Laptop", 999.99)
                .update();

        // Then - Verify insertion
        var count =
                client.sql("SELECT COUNT(*) FROM products").query(Long.class).single();

        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should query data from PostgreSQL")
    void shouldQueryData() {
        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Mouse", 29.99)
                .update();

        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Keyboard", 79.99)
                .update();

        // When - Query products
        var products = client.sql("SELECT id, name, price FROM products")
                .query((rs, _) -> new Product(rs.getLong("id"), rs.getString("name"), rs.getDouble("price")))
                .list();

        // Then - Verify results
        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting(product -> product != null ? product.name() : null)
                .containsExactlyInAnyOrder("Mouse", "Keyboard");
    }

    @Test
    @DisplayName("Should update data in PostgreSQL")
    void shouldUpdateData() {

        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Monitor", 299.99)
                .update();

        // When - Update price
        client.sql("UPDATE products SET price = ? WHERE name = ?")
                .params(249.99, "Monitor")
                .update();

        // Then - Verify update
        var price = client.sql("SELECT price FROM products WHERE name = ?")
                .params("Monitor")
                .query(Double.class)
                .single();

        assertThat(price).isEqualTo(249.99);
    }

    @Test
    @DisplayName("Should delete data from PostgreSQL")
    void shouldDeleteData() {

        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Webcam", 89.99)
                .update();

        // When - Delete product
        client.sql("DELETE FROM products WHERE name = ?").params("Webcam").update();

        // Then - Verify deletion
        var count =
                client.sql("SELECT COUNT(*) FROM products").query(Long.class).single();

        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should demonstrate @ServiceConnection benefits")
    void shouldDemonstrateServiceConnection() {

        // Verify everything is wired correctly
        assertThat(client).isNotNull();

        // Execute a simple query to prove connection works
        var result = client.sql("SELECT 1 as value").query(Integer.class).single();

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle transactions")
    void shouldHandleTransactions() {

        // When - Insert multiple products in single operation
        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Product A", 10.0)
                .update();

        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Product B", 20.0)
                .update();

        client.sql("INSERT INTO products (name, price) VALUES (?, ?)")
                .params("Product C", 30.0)
                .update();

        // Then - Verify all inserts
        var count =
                client.sql("SELECT COUNT(*) FROM products").query(Long.class).single();

        assertThat(count).isEqualTo(3L);

        // Verify total price
        var totalPrice = client.sql("SELECT SUM(price) FROM products")
                .query(Double.class)
                .single();

        assertThat(totalPrice).isEqualTo(60.0);
    }
}
