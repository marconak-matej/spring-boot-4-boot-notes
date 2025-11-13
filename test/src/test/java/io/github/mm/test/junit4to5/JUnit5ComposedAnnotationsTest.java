package io.github.mm.test.junit4to5;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.product.ProductService;
import io.github.mm.test.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig // Composed annotation: @ExtendWith(SpringExtension) + @ContextConfiguration
@SpringBootTest
@DisplayName("Product Service Tests (JUnit Jupiter with Composed Annotations)")
class JUnit5ComposedAnnotationsTest {

    @Autowired
    ProductService service;

    @BeforeEach
    void setUp() {
        service.clearAll();
    }

    @Test
    @DisplayName("Should create product with valid name and price")
    void shouldCreateProductWithValidNameAndPrice() {
        // Given
        var productName = "Laptop";
        var price = 999.99;

        // When
        var product = service.createProduct(productName, price);

        // Then
        assertThat(product)
                .isNotNull()
                .extracting(Product::name, Product::price)
                .containsExactly(productName, price);
    }

    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductById() {
        // Given
        var created = service.createProduct("Mouse", 29.99);

        // When
        var found = service.getProductById(created.id());

        // Then
        assertThat(found).isEqualTo(created);
    }

    @Test
    @DisplayName("Should update product price")
    void shouldUpdateProductPrice() {
        // Given
        var created = service.createProduct("Keyboard", 79.99);
        var newPrice = 69.99;

        // When
        var updated = service.updatePrice(created.id(), newPrice);

        // Then
        assertThat(updated.price()).isEqualTo(newPrice);
        assertThat(updated.name()).isEqualTo(created.name());
    }

    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        // Given
        var created = service.createProduct("Monitor", 299.99);

        // When
        service.deleteProduct(created.id());

        // Then
        assertThat(service.getAllProducts()).isEmpty();
    }

    @Test
    @DisplayName("Should get all products")
    void shouldGetAllProducts() {
        // Given
        service.createProduct("Laptop", 999.99);
        service.createProduct("Mouse", 29.99);
        service.createProduct("Keyboard", 79.99);

        // When
        var products = service.getAllProducts();

        // Then
        assertThat(products)
                .hasSize(3)
                .extracting(Product::name)
                .containsExactlyInAnyOrder("Laptop", "Mouse", "Keyboard");
    }
}
