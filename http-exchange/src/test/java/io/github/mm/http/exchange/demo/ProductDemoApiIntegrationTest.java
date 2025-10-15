package io.github.mm.http.exchange.demo;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.http.exchange.client.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class ProductDemoApiIntegrationTest {

    @Autowired
    private ProductDemoApi api;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToController(api).build();
    }

    @Test
    void getProductById_shouldReturnSingleProduct() {
        // Given - Use a known product ID from the external API
        var productId = "1";

        // When - Call our REST endpoint using RestClient
        var response = client.get()
                .uri("/api/demo/products/{id}", productId)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Product.class);

        // Then - Verify the response
        assertThat(response.getResponseBody()).isNotNull();

        var product = response.getResponseBody();
        assertThat(product.id()).isEqualTo(productId);
        assertThat(product.name()).isNotNull();
        assertThat(product.name()).isNotEmpty();
    }
}
