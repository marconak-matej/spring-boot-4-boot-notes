package io.github.mm.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.mm.http.client.demo.rest.Demo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("WebClient Integration Tests")
class WebClientIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebClient webClient;

    @Test
    @DisplayName("Should create a new demo using WebClient")
    void shouldCreateDemo() {
        // Given
        var demo = fixture().demoWithName("WebClient Demo");

        // When
        var created = webClient
                .post()
                .uri(baseUrl())
                .bodyValue(demo)
                .retrieve()
                .bodyToMono(Demo.class)
                .block();

        // Then
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("WebClient Demo");
    }

    @Test
    @DisplayName("Should update an existing demo using WebClient")
    void shouldUpdateDemo() {
        // Given - Create a demo first
        var demo = fixture().demoWithName("Original WebClient");
        var created = webClient
                .post()
                .uri(baseUrl())
                .bodyValue(demo)
                .retrieve()
                .bodyToMono(Demo.class)
                .block();

        assertNotNull(created);
        var demoId = created.id();

        // When - Update the demo
        var updatedDemo = fixture().demoWithId(demoId, "Updated via WebClient");
        var updated = webClient
                .put()
                .uri(baseUrl() + "/{id}", demoId)
                .bodyValue(updatedDemo)
                .retrieve()
                .bodyToMono(Demo.class)
                .block();

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Updated via WebClient");
    }

    @Test
    @DisplayName("Should retrieve all demos using WebClient")
    void shouldGetAllDemos() {
        // Given - Create some demos
        webClient
                .post()
                .uri(baseUrl())
                .bodyValue(fixture().demoWithName("Demo X"))
                .retrieve()
                .toBodilessEntity()
                .block();
        webClient
                .post()
                .uri(baseUrl())
                .bodyValue(fixture().demoWithName("Demo Y"))
                .retrieve()
                .toBodilessEntity()
                .block();

        // When
        @SuppressWarnings("NullableProblems") var demos = webClient
                .get()
                .uri(baseUrl())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Demo>>() {})
                .block();

        // Then
        assertThat(demos).isNotNull();
        assertThat(demos.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should retrieve a specific demo by ID using WebClient")
    void shouldGetDemoById() {
        // Given - Create a demo
        var demo = fixture().demoWithName("Specific WebClient Demo");
        var created = webClient
                .post()
                .uri(baseUrl())
                .bodyValue(demo)
                .retrieve()
                .bodyToMono(Demo.class)
                .block();

        assertNotNull(created);
        var demoId = created.id();

        // When
        var retrieved = webClient
                .get()
                .uri(baseUrl() + "/{id}", demoId)
                .retrieve()
                .bodyToMono(Demo.class)
                .block();

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.id()).isEqualTo(demoId);
        assertThat(retrieved.name()).isEqualTo("Specific WebClient Demo");
    }

    @Test
    @DisplayName("Should delete a demo using WebClient")
    void shouldDeleteDemo() {
        // Given - Create a demo
        var demo = fixture().demoForDeletion();
        var created = webClient
                .post()
                .uri(baseUrl())
                .bodyValue(demo)
                .retrieve()
                .bodyToMono(Demo.class)
                .block();

        assertNotNull(created);
        var demoId = created.id();

        // When
        webClient
                .delete()
                .uri(baseUrl() + "/{id}", demoId)
                .retrieve()
                .toBodilessEntity()
                .block();

        // Then - Verify it's deleted (should return 404)
        webClient
                .get()
                .uri(baseUrl() + "/{id}", demoId)
                .retrieve()
                .onStatus(status -> status.value() == 404, _ -> Mono.empty())
                .toBodilessEntity()
                .block();
    }

    @Test
    @DisplayName("Should handle 404 Not Found errors gracefully")
    void shouldHandleNotFound() {
        // When/Then
        webClient
                .get()
                .uri(baseUrl() + "/{id}", 999999)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    return Mono.empty();
                })
                .toBodilessEntity()
                .block();
    }

    @Test
    @DisplayName("Should demonstrate reactive approach with Mono.zip for parallel operations")
    void shouldUseReactiveApproach() {
        // Given
        var demo1 = fixture().demoWithName("Reactive Demo 1");
        var demo2 = fixture().demoWithName("Reactive Demo 2");

        // When - Create demos reactively
        var created1 =
                webClient.post().uri(baseUrl()).bodyValue(demo1).retrieve().bodyToMono(Demo.class);
        var created2 =
                webClient.post().uri(baseUrl()).bodyValue(demo2).retrieve().bodyToMono(Demo.class);

        // Combine both operations
        var createdDemos = Mono.zip(created1, created2, List::of).block();

        // Then
        assertThat(createdDemos).hasSize(2);
        assertThat(createdDemos.get(0).name()).isEqualTo("Reactive Demo 1");
        assertThat(createdDemos.get(1).name()).isEqualTo("Reactive Demo 2");
    }

    @TestConfiguration
    static class WebClientTestConfiguration {
        @Bean
        public WebClient webClient() {
            return WebClient.builder().build();
        }
    }
}
