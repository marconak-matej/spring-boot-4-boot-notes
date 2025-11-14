package io.github.mm.test.resttestclient;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.product.ProductService;
import io.github.mm.test.product.model.Product;
import io.github.mm.test.product.rest.ProductApi;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DisplayName("RestTestClient - Live Server Testing (Spring Boot 4.0 RC2)")
class ProductApiLiveServerTest {

    @Autowired
    ProductService service;

    @Autowired
    RestTestClient client;

    @BeforeEach
    void setUp() {

        service.clearAll();
    }

    @Test
    @DisplayName("Should create product via HTTP POST")
    void shouldCreateProduct() {
        // Given
        var request = new ProductApi.CreateProductRequest("Laptop", 999.99);

        // When - Make HTTP call
        var response = client.post()
                .uri("/api/products")
                .body(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Product.class);

        // Then
        var product = response.getResponseBody();
        assertThat(product).isNotNull();
        assertThat(product.name()).isEqualTo("Laptop");
        assertThat(product.price()).isEqualTo(999.99);
    }

    @Test
    @DisplayName("Should get product by ID via HTTP GET")
    void shouldGetProductById() {
        // Given - Create a product first
        var createRequest = new ProductApi.CreateProductRequest("Mouse", 29.99);
        var createResponse = client.post()
                .uri("/api/products")
                .body(createRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Product.class);

        var createdProduct = createResponse.getResponseBody();
        assertThat(createdProduct).isNotNull();
        Long productId = createdProduct.id();

        // When - Get the product
        var getResponse = client.get()
                .uri("/api/products/{id}", productId)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Product.class);

        // Then
        var product = getResponse.getResponseBody();
        assertThat(product).isNotNull();
        assertThat(product.id()).isEqualTo(productId);
        assertThat(product.name()).isEqualTo("Mouse");
    }

    @Test
    @DisplayName("Should get all products via HTTP GET")
    void shouldGetAllProducts() {
        // Given - Create multiple products
        client.post()
                .uri("/api/products")
                .body(new ProductApi.CreateProductRequest("Keyboard", 79.99))
                .exchange()
                .expectStatus()
                .isCreated();

        client.post()
                .uri("/api/products")
                .body(new ProductApi.CreateProductRequest("Monitor", 299.99))
                .exchange()
                .expectStatus()
                .isCreated();

        // When - Get all products
        var response = client.get()
                .uri("/api/products")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<@NotNull List<Product>>() {});

        // Then
        var products = response.getResponseBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should handle 404 Not Found")
    void shouldHandleNotFound() {
        // When - Try to get non-existent product
        client.get()
                .uri("/api/products/{id}", 999999L)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("Should update product price via HTTP PUT")
    void shouldUpdateProductPrice() {
        // Given - Create a product
        var createRequest = new ProductApi.CreateProductRequest("Headphones", 149.99);
        var createResponse = client.post()
                .uri("/api/products")
                .body(createRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Product.class);

        var createdProduct = createResponse.getResponseBody();
        assertThat(createdProduct).isNotNull();
        var productId = createdProduct.id();

        // When - Update price
        var updateRequest = new ProductApi.UpdatePriceRequest(129.99);
        var updateResponse = client.put()
                .uri("/api/products/{id}", productId)
                .body(updateRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Product.class);

        // Then
        var product = updateResponse.getResponseBody();
        assertThat(product).isNotNull();
        assertThat(product.price()).isEqualTo(129.99);
    }

    @Test
    @DisplayName("Should delete product via HTTP DELETE")
    void shouldDeleteProduct() {
        // Given - Create a product
        var createRequest = new ProductApi.CreateProductRequest("Webcam", 89.99);
        var createResponse = client.post()
                .uri("/api/products")
                .body(createRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Product.class);

        var createdProduct = createResponse.getResponseBody();
        assertThat(createdProduct).isNotNull();
        var productId = createdProduct.id();

        // When - Delete the product
        client.delete()
                .uri("/api/products/{id}", productId)
                .exchange()
                .expectStatus()
                .isNoContent();

        // Then - Product no longer exists
        client.get()
                .uri("/api/products/{id}", productId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
