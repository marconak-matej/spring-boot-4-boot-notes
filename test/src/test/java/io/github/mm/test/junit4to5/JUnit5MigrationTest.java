package io.github.mm.test.junit4to5;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JUnit5MigrationTest { //  Package-private class

    @Autowired
    ProductService service; //  Package-private field (no private needed)

    @BeforeEach
    //  JUnit Jupiter lifecycle method
    void setUp() { //  Package-private method
        service.clearAll();
    }

    @Test
    //  org.junit.jupiter.api.Test
    void shouldCreateProduct() { //  Package-private, descriptive name
        // Given
        var productName = "Laptop";
        var price = 999.99;

        // When
        var product = service.createProduct(productName, price);

        // Then
        assertThat(product).isNotNull();
        assertThat(product.name()).isEqualTo(productName);
        assertThat(product.price()).isEqualTo(price);
    }

    @Test
    void shouldFindProductById() {
        // Given
        var created = service.createProduct("Mouse", 29.99);

        // When
        var found = service.getProductById(created.id());

        // Then
        assertThat(found).isEqualTo(created);
    }

    @Test
    void shouldGetAllProducts() {
        // Given
        service.createProduct("Laptop", 999.99);
        service.createProduct("Mouse", 29.99);

        // When
        var products = service.getAllProducts();

        // Then
        assertThat(products).hasSize(2);
    }
}
