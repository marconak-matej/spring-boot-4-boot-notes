package io.github.mm.http.exchange.product.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

class ProductServiceIntegrationTest {

    private ProductService productService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        var restClientBuilder = RestClient.builder().baseUrl("https://api.restful-api.dev");

        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        var restClient = restClientBuilder.build();

        var factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build();

        productService = factory.createClient(ProductService.class);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        // Given
        var jsonResponse = """
                [
                  {
                    "id": "1",
                    "name": "Google Pixel 6 Pro",
                    "data": {
                      "color": "Cloudy White",
                      "capacity": "128 GB"
                    }
                  },
                  {
                    "id": "2",
                    "name": "Apple iPhone 12 Mini",
                    "data": {
                      "color": "Purple",
                      "capacity": "256 GB"
                    }
                  }
                ]
                """;

        mockServer
                .expect(requestTo("https://api.restful-api.dev/objects"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        var products = productService.getAllProducts();

        // Then
        assertThat(products).hasSize(2);
        assertThat(products.getFirst().id()).isEqualTo("1");
        assertThat(products.get(0).name()).isEqualTo("Google Pixel 6 Pro");
        assertThat(products.get(0).data()).containsEntry("color", "Cloudy White");
        assertThat(products.get(1).id()).isEqualTo("2");
        assertThat(products.get(1).name()).isEqualTo("Apple iPhone 12 Mini");

        mockServer.verify();
    }

    @Test
    void getProductById_shouldReturnSingleProduct() {
        // Given
        var productId = "3";
        var jsonResponse = """
                {
                  "id": "3",
                  "name": "Samsung Galaxy Z Fold",
                  "data": {
                    "color": "Phantom Black",
                    "capacity": "512 GB",
                    "generation": "3rd"
                  }
                }
                """;

        mockServer
                .expect(requestTo("https://api.restful-api.dev/objects/" + productId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // When
        var product = productService.getProductById(productId);

        // Then
        assertThat(product).isNotNull();
        assertThat(product.id()).isEqualTo("3");
        assertThat(product.name()).isEqualTo("Samsung Galaxy Z Fold");
        assertThat(product.data())
                .containsEntry("color", "Phantom Black")
                .containsEntry("capacity", "512 GB")
                .containsEntry("generation", "3rd");

        mockServer.verify();
    }
}
