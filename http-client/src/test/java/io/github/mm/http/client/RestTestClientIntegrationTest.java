package io.github.mm.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.mm.http.client.demo.Demo;
import io.github.mm.http.client.demo.api.rest.DemoApi;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("RestTestClient Integration Tests")
class RestTestClientIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private DemoApi demoApi;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToController(demoApi).build();
    }

    @Test
    @DisplayName("Should create a new demo using RestTestClient")
    void shouldCreateDemo() {
        // Given
        var demo = fixture().demoWithName("RestTestClient Demo");

        // When
        var response = client.post()
                .uri("/api/demos")
                .body(demo)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Demo.class);

        // Then
        var created = response.getResponseBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("RestTestClient Demo");
    }

    @Test
    @DisplayName("Should update an existing demo using RestTestClient")
    void shouldUpdateDemo() {
        // Given - Create a demo first
        var demo = fixture().demoWithName("Original");
        var createResponse = client.post()
                .uri("/api/demos")
                .body(demo)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Demo.class);
        var created = createResponse.getResponseBody();

        assertNotNull(created);
        var demoId = created.id();

        // When - Update the demo
        var updatedDemo = fixture().demoWithId(demoId, "Updated via RestTestClient");
        var updateResponse = client.put()
                .uri("/api/demos/{id}", demoId)
                .body(updatedDemo)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Demo.class);

        // Then
        var updated = updateResponse.getResponseBody();
        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Updated via RestTestClient");
    }

    @Test
    @DisplayName("Should retrieve all demos using RestTestClient")
    void shouldGetAllDemos() {
        // Given - Create some demos
        client.post()
                .uri("/api/demos")
                .body(fixture().demoWithName("Demo A"))
                .exchange()
                .expectStatus()
                .isCreated();
        client.post()
                .uri("/api/demos")
                .body(fixture().demoWithName("Demo B"))
                .exchange()
                .expectStatus()
                .isCreated();

        // When
        @SuppressWarnings("NullableProblems")
        var response = client.get()
                .uri("/api/demos")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(new ParameterizedTypeReference<List<Demo>>() {});

        // Then
        var demos = response.getResponseBody();
        assertThat(demos).isNotNull();
        assertThat(demos.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should retrieve a specific demo by ID using RestTestClient")
    void shouldGetDemoById() {
        // Given - Create a demo
        var demo = fixture().demoWithName("Specific RestTestClient Demo");
        var createResponse = client.post()
                .uri("/api/demos")
                .body(demo)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Demo.class);
        var created = createResponse.getResponseBody();

        assertNotNull(created);
        var demoId = created.id();

        // When
        var response = client.get()
                .uri("/api/demos/{id}", demoId)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Demo.class);

        // Then
        var retrieved = response.getResponseBody();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.id()).isEqualTo(demoId);
        assertThat(retrieved.name()).isEqualTo("Specific RestTestClient Demo");
    }
}
