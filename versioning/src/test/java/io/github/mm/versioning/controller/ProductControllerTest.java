package io.github.mm.versioning.controller;

import static io.github.mm.versioning.product.domain.Currency.USD;
import static io.github.mm.versioning.product.domain.ProductV2Status.IN_STOCK;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.versioning.product.domain.Product;
import io.github.mm.versioning.product.domain.ProductV2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webtestclient.AutoConfigureWebTestClient;
import org.springframework.boot.webtestclient.WebTestClientBuilderCustomizer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.ApiVersionInserter;

@SpringBootTest
@AutoConfigureWebTestClient
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetProductVersion1_0() {
        webTestClient
                .get()
                .uri("/api/products/456")
                .apiVersion(1.0)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .value(product -> {
                    assertThat(product).isNotNull();
                    assertThat(product.id()).isEqualTo("456");
                    assertThat(product.title()).isEqualTo("Coffee Maker");
                    assertThat(product.priceUsd()).isEqualTo(49.99);
                    assertThat(product.isAvailable()).isTrue();
                });
    }

    @Test
    void testGetProductVersion2_0() {
        webTestClient
                .get()
                .uri("/api/products/456")
                .apiVersion(2.0)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductV2.class)
                .value(product -> {
                    assertThat(product).isNotNull();
                    assertThat(product.id()).isEqualTo("456");
                    assertThat(product.title()).isEqualTo("Coffee Maker");
                    assertThat(product.price()).isEqualTo(4999);
                    assertThat(product.currency()).isEqualTo(USD);
                    assertThat(product.status()).isEqualTo(IN_STOCK);
                });
    }

    @Test
    void testGetProductWithoutVersion() {
        webTestClient.get().uri("/api/products/456").exchange().expectStatus().isBadRequest();
    }

    @Test
    void testGetProductWithUnsupportedVersion() {
        webTestClient
                .get()
                .uri("/api/products/456")
                .apiVersion(3.0)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void testGetProductWithVersion1_1() {
        // Version 1.1 is not supported, so should return 400 (bad request)
        webTestClient
                .get()
                .uri("/api/products/456")
                .apiVersion(1.1)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @TestConfiguration
    static class TestConfig implements WebTestClientBuilderCustomizer {

        @Override
        public void customize(WebTestClient.Builder builder) {
            builder.apiVersionInserter(ApiVersionInserter.useHeader("API-Version"));
        }
    }
}
