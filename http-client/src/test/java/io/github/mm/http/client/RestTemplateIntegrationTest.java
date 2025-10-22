package io.github.mm.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.mm.http.client.demo.rest.Demo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("RestTemplate Integration Tests")
class RestTemplateIntegrationTest extends AbstractIntegrationTest {

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplateBuilder().build();
    }

    @Test
    @DisplayName("Should create a new demo using postForEntity")
    void shouldCreateDemo() {
        // Given
        var demo = fixture().demoWithName("RestTemplate Demo");

        // When
        var response = restTemplate.postForEntity(baseUrl(), demo, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("RestTemplate Demo");
    }

    @Test
    @DisplayName("Should create a new demo using postForObject")
    void shouldCreateDemoUsingPostForObject() {
        // Given
        var demo = fixture().demoWithName("PostForObject Demo");

        // When
        var created = restTemplate.postForObject(baseUrl(), demo, Demo.class);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("PostForObject Demo");
    }

    @Test
    @DisplayName("Should update an existing demo using exchange")
    void shouldUpdateDemo() {
        // Given - Create a demo first
        var demo = fixture().demoWithName("Original RestTemplate");
        var createResponse = restTemplate.postForEntity(baseUrl(), demo, Demo.class);

        assertNotNull(createResponse.getBody());
        var demoId = createResponse.getBody().id();

        // When - Update the demo
        var updatedDemo = fixture().demoWithId(demoId, "Updated via RestTemplate");
        var requestEntity = new HttpEntity<>(updatedDemo);
        var response = restTemplate.exchange(baseUrl() + "/" + demoId, HttpMethod.PUT, requestEntity, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Updated via RestTemplate");
    }

    @Test
    @DisplayName("Should update an existing demo using put method")
    void shouldUpdateDemoUsingPut() {
        // Given - Create a demo first
        var demo = fixture().demoWithName("Original");
        var created = restTemplate.postForObject(baseUrl(), demo, Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When - Update using put method
        var updatedDemo = fixture().demoWithId(demoId, "Updated using put");
        restTemplate.put(baseUrl() + "/" + demoId, updatedDemo);

        // Then - Verify the update
        var retrieved = restTemplate.getForObject(baseUrl() + "/" + demoId, Demo.class);
        assertNotNull(retrieved);
        assertThat(retrieved.name()).isEqualTo("Updated using put");
    }

    @Test
    @DisplayName("Should retrieve all demos using exchange")
    void shouldGetAllDemos() {
        // Given - Create some demos
        restTemplate.postForObject(baseUrl(), fixture().demoWithName("Demo 1"), Demo.class);
        restTemplate.postForObject(baseUrl(), fixture().demoWithName("Demo 2"), Demo.class);

        // When
        var response = restTemplate.exchange(baseUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(((List<?>) response.getBody()).size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should retrieve a specific demo by ID using getForEntity")
    void shouldGetDemoById() {
        // Given - Create a demo
        var demo = fixture().demoWithName("Specific RestTemplate Demo");
        var created = restTemplate.postForObject(baseUrl(), demo, Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When
        var response = restTemplate.getForEntity(baseUrl() + "/" + demoId, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(demoId);
        assertThat(response.getBody().name()).isEqualTo("Specific RestTemplate Demo");
    }

    @Test
    @DisplayName("Should retrieve a specific demo by ID using getForObject")
    void shouldGetDemoByIdUsingGetForObject() {
        // Given - Create a demo
        var demo = fixture().demoWithName("GetForObject Demo");
        var created = restTemplate.postForObject(baseUrl(), demo, Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When
        var retrieved = restTemplate.getForObject(baseUrl() + "/" + demoId, Demo.class);

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.id()).isEqualTo(demoId);
        assertThat(retrieved.name()).isEqualTo("GetForObject Demo");
    }

    @Test
    @DisplayName("Should delete a demo using delete method")
    void shouldDeleteDemo() {
        // Given - Create a demo
        var demo = fixture().demoForDeletion();
        var created = restTemplate.postForObject(baseUrl(), demo, Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When
        restTemplate.delete(baseUrl() + "/" + demoId);

        // Then - Verify it's deleted
        try {
            restTemplate.getForObject(baseUrl() + "/" + demoId, Demo.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should delete a demo using exchange")
    void shouldDeleteDemoUsingExchange() {
        // Given - Create a demo
        var demo = fixture().demoWithName("Delete with Exchange");
        var created = restTemplate.postForObject(baseUrl(), demo, Demo.class);

        assertNotNull(created);
        var demoId = created.id();

        // When
        var response = restTemplate.exchange(baseUrl() + "/" + demoId, HttpMethod.DELETE, null, Void.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should handle 404 Not Found errors with getForObject")
    void shouldHandleNotFound() {
        // When/Then
        try {
            restTemplate.getForObject(baseUrl() + "/999999", Demo.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    @DisplayName("Should handle 404 Not Found errors with exchange")
    void shouldHandleNotFoundWithExchange() {
        // When
        try {
            restTemplate.exchange(baseUrl() + "/999999", HttpMethod.GET, null, Demo.class);
        } catch (HttpClientErrorException e) {
            // Then
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
