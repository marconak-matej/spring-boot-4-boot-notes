package io.github.mm.test.product.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class OpenApiDocumentationTest {

    @Autowired
    RestTestClient client;

    @Test
    void shouldExposeOpenApiDocumentation() {
        var response = client.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);

        String body = response.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body).contains("\"title\":\"Spring Boot 4.0 Test Module API\"");
        assertThat(body).contains("\"version\":\"1.0.0\"");
        assertThat(body).contains("/api/products");
    }

    @Test
    void shouldExposeSwaggerUi() {
        client.get().uri("/swagger-ui/index.html").exchange().expectStatus().isOk();
    }

    @Test
    void openApiDocumentationShouldContainAllEndpoints() {
        var response = client.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);

        String apiDocs = response.getResponseBody();
        assertThat(apiDocs).isNotNull();

        // Verify all endpoints are documented
        assertThat(apiDocs).contains("\"post\""); // Create product
        assertThat(apiDocs).contains("\"get\""); // Get products
        assertThat(apiDocs).contains("\"put\""); // Update price
        assertThat(apiDocs).contains("\"delete\""); // Delete product

        // Verify schemas
        assertThat(apiDocs).contains("CreateProductRequest");
        assertThat(apiDocs).contains("UpdatePriceRequest");
        assertThat(apiDocs).contains("Product");
        assertThat(apiDocs).contains("ErrorResponse");

        // Verify descriptions
        assertThat(apiDocs).contains("Product Management");
        assertThat(apiDocs).contains("Create a new product");
        assertThat(apiDocs).contains("Get product by ID");
        assertThat(apiDocs).contains("Get all products");
        assertThat(apiDocs).contains("Update product price");
        assertThat(apiDocs).contains("Delete a product");
    }

    @Test
    void openApiDocumentationShouldContainGlobalMetadata() {
        var response = client.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class);

        String apiDocs = response.getResponseBody();
        assertThat(apiDocs).isNotNull();

        // Verify global metadata
        assertThat(apiDocs).contains("Spring Boot 4.0 Test Module API");
        assertThat(apiDocs).contains("Apache 2.0");
        assertThat(apiDocs).contains("demo@example.com");
        assertThat(apiDocs).contains("Local Development Server");
    }
}
