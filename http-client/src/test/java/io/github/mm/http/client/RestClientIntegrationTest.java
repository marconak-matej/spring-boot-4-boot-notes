package io.github.mm.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.mm.http.client.demo.Demo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("RestClient Integration Tests")
class RestClientIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RestClient restClient;

    @Test
    @DisplayName("Should create a new demo using RestClient")
    void shouldCreateDemo() {
        // Given
        var demo = fixture().demoWithName("RestClient Demo");

        // When
        var created = restClient
                .post()
                .uri(baseUrl())
                .body(demo)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, _) -> {
                    throw new RuntimeException("Failed to create demo");
                })
                .body(Demo.class);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("RestClient Demo");
    }

    @Test
    @DisplayName("Should update an existing demo using RestClient")
    void shouldUpdateDemo() {
        // Given - Create a demo first
        var demo = fixture().demoWithName("Original");
        var created = restClient.post().uri(baseUrl()).body(demo).retrieve().body(Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When - Update the demo
        var updatedDemo = fixture().demoWithId(demoId, "Updated via RestClient");
        var updated = restClient
                .put()
                .uri(baseUrl() + "/" + demoId)
                .body(updatedDemo)
                .retrieve()
                .body(Demo.class);

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Updated via RestClient");
    }

    @Test
    @DisplayName("Should retrieve all demos using RestClient")
    void shouldGetAllDemos() {
        // Given - Create some demos
        restClient
                .post()
                .uri(baseUrl())
                .body(fixture().demoWithName("Demo A"))
                .retrieve()
                .toBodilessEntity();
        restClient
                .post()
                .uri(baseUrl())
                .body(fixture().demoWithName("Demo B"))
                .retrieve()
                .toBodilessEntity();

        // When
        var demos = restClient.get().uri(baseUrl()).retrieve().body(new ParameterizedTypeReference<List<Demo>>() {});

        // Then
        assertThat(demos).isNotNull();
        assertThat(demos.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should retrieve a specific demo by ID using RestClient")
    void shouldGetDemoById() {
        // Given - Create a demo
        var demo = fixture().demoWithName("Specific RestClient Demo");
        var created = restClient.post().uri(baseUrl()).body(demo).retrieve().body(Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When
        var retrieved =
                restClient.get().uri(baseUrl() + "/" + demoId).retrieve().body(Demo.class);

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.id()).isEqualTo(demoId);
        assertThat(retrieved.name()).isEqualTo("Specific RestClient Demo");
    }

    @Test
    @DisplayName("Should delete a demo using RestClient")
    void shouldDeleteDemo() {
        // Given - Create a demo
        var demo = fixture().demoForDeletion();
        var created = restClient.post().uri(baseUrl()).body(demo).retrieve().body(Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When
        restClient.delete().uri(baseUrl() + "/" + demoId).retrieve().toBodilessEntity();

        // Then - Verify it's deleted (should return 404)
        restClient
                .get()
                .uri(baseUrl() + "/" + demoId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (_, _) -> {
                    // Expected 404
                })
                .toBodilessEntity();
    }

    @Test
    @DisplayName("Should handle 404 Not Found errors gracefully")
    void shouldHandleNotFound() {
        // When/Then
        restClient
                .get()
                .uri(baseUrl() + "/999999")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (_, response) -> assertThat(
                                response.getStatusCode().value())
                        .isEqualTo(404))
                .toBodilessEntity();
    }

    @TestConfiguration
    static class RestClientTestConfiguration {
        @Bean
        public RestClient restClient() {
            return RestClient.builder().build();
        }
    }
}
