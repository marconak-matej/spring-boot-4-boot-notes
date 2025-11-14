package io.github.mm.test.resttestclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.github.mm.test.product.ProductNotFoundException;
import io.github.mm.test.product.ProductService;
import io.github.mm.test.product.model.Product;
import io.github.mm.test.product.rest.ProductApi;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@WebMvcTest(ProductApi.class)
@AutoConfigureRestTestClient
@DisplayName("RestTestClient - MockMvc Backend Testing (Spring Boot 4.0 RC2)")
class ProductApiMockMvcTest {

    @MockitoBean
    ProductService service;

    @Autowired
    RestTestClient client;

    @Test
    @DisplayName("Should get product by ID with mocked service")
    void shouldGetProductById() {
        // Given - Mock the service
        var mockProduct = new Product(1L, "Laptop", 999.99);
        given(service.getProductById(1L)).willReturn(mockProduct);

        // When - RestTestClient uses MockMvc under the hood
        var response = client.get()
                .uri("/api/products/{id}", 1L)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Product.class);

        // Then
        var product = response.getResponseBody();
        assertThat(product).isNotNull();
        assertThat(product.id()).isEqualTo(1L);
        assertThat(product.name()).isEqualTo("Laptop");
        assertThat(product.price()).isEqualTo(999.99);

        // Verify service was called
        verify(service).getProductById(1L);
    }

    @Test
    @DisplayName("Should get all products with mocked service")
    void shouldGetAllProducts() {
        // Given
        var mockProducts = List.of(
                new Product(1L, "Laptop", 999.99), new Product(2L, "Mouse", 29.99), new Product(3L, "Keyboard", 79.99));
        given(service.getAllProducts()).willReturn(mockProducts);

        // When
        var response = client.get()
                .uri("/api/products")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<@NotNull List<Product>>() {});

        // Then
        var products = response.getResponseBody();
        assertThat(products).isNotNull();
        assertThat(products).hasSize(3);
        assertThat(products.getFirst().name()).isEqualTo("Laptop");

        verify(service).getAllProducts();
    }

    @Test
    @DisplayName("Should create product with mocked service")
    void shouldCreateProduct() {
        // Given
        var createdProduct = new Product(1L, "Mouse", 29.99);
        given(service.createProduct(any(), any())).willReturn(createdProduct);

        var request = new ProductApi.CreateProductRequest("Mouse", 29.99);

        // When
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
        assertThat(product.name()).isEqualTo("Mouse");

        verify(service).createProduct("Mouse", 29.99);
    }

    @Test
    @DisplayName("Should update product price with mocked service")
    void shouldUpdatePrice() {
        // Given
        var updatedProduct = new Product(1L, "Keyboard", 69.99);
        given(service.updatePrice(1L, 69.99)).willReturn(updatedProduct);

        var request = new ProductApi.UpdatePriceRequest(69.99);

        // When
        var response = client.put()
                .uri("/api/products/{id}", 1L)
                .body(request)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Product.class);

        // Then
        var product = response.getResponseBody();
        assertThat(product).isNotNull();
        assertThat(product.price()).isEqualTo(69.99);

        verify(service).updatePrice(1L, 69.99);
    }

    @Test
    @DisplayName("Should delete product with mocked service")
    void shouldDeleteProduct() {
        // When
        client.delete().uri("/api/products/{id}", 1L).exchange().expectStatus().isNoContent();

        // Then
        verify(service).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should handle product not found exception")
    void shouldHandleNotFoundException() {
        // Given - Service throws exception
        given(service.getProductById(999L)).willThrow(new ProductNotFoundException("Product not found with id: 999"));

        // When
        var response = client.get()
                .uri("/api/products/{id}", 999L)
                .exchange()
                .expectStatus()
                .isNotFound()
                .returnResult(ProductApi.ErrorResponse.class);

        // Then - Exception handler returns 404
        var error = response.getResponseBody();
        assertThat(error).isNotNull();
        assertThat(error.message()).contains("Product not found");

        verify(service).getProductById(999L);
    }
}
