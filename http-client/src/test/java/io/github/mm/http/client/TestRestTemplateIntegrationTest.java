package io.github.mm.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.mm.http.client.demo.rest.Demo;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DisplayName("TestRestTemplate Integration Tests")
class TestRestTemplateIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("Should create a new demo using TestRestTemplate")
    void shouldCreateDemo() {
        // Given
        var demo = fixture().defaultDemo();

        // When
        var response = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Test Demo");

        fixture().trackCreated(response.getBody());
    }

    @Test
    @DisplayName("Should update an existing demo using TestRestTemplate")
    void shouldUpdateDemo() {
        // Given - Create a demo first
        var demo = fixture().demoForUpdate();
        var createResponse = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);
        var created = createResponse.getBody();
        fixture().trackCreated(created);

        assertNotNull(created);
        var demoId = created.id();

        // When - Update the demo
        var updatedDemo = fixture().withCustomName(created, "Updated Name");
        var requestEntity = new HttpEntity<>(updatedDemo);
        var response = testRestTemplate.exchange(baseUrl() + "/" + demoId, HttpMethod.PUT, requestEntity, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Should retrieve all demos using TestRestTemplate")
    void shouldGetAllDemos() {
        // Given - Create some demos
        var demos = fixture().multipleDemos(2);
        for (var demo : demos) {
            var response = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);
            fixture().trackCreated(response.getBody());
        }

        // When
        @SuppressWarnings("NullableProblems")
        var response = testRestTemplate.exchange(
                baseUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Demo>>() {});

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should retrieve a specific demo by ID using TestRestTemplate")
    void shouldGetDemoById() {
        // Given - Create a demo
        var demo = fixture().demoWithSpecificName();
        var createResponse = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);
        var created = createResponse.getBody();
        fixture().trackCreated(created);

        assertNotNull(created);
        var demoId = created.id();

        // When
        var response = testRestTemplate.getForEntity(baseUrl() + "/" + demoId, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(demoId);
        assertThat(response.getBody().name()).isEqualTo("Specific Demo");
    }

    @Test
    @DisplayName("Should delete a demo using TestRestTemplate")
    void shouldDeleteDemo() {
        // Given - Create a demo
        var demo = fixture().demoForDeletion();
        var createResponse = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);
        var created = createResponse.getBody();

        assertNotNull(created);
        var demoId = created.id();
        // Note: Not tracking this one since we're deleting it in the test

        // When
        testRestTemplate.delete(baseUrl() + "/" + demoId);

        // Then - Verify it's deleted
        var response = testRestTemplate.getForEntity(baseUrl() + "/" + demoId, Demo.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 404 when demo is not found")
    void shouldReturn404WhenDemoNotFound() {
        // When
        var response = testRestTemplate.getForEntity(baseUrl() + "/999999", Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should create a demo using builder pattern")
    void shouldCreateDemoUsingBuilder() {
        // Given
        var demo = fixture().builder().name("Builder Demo").build();

        // When
        var response = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Builder Demo");

        fixture().trackCreated(response.getBody());
    }

    // Validation Tests

    @Test
    @DisplayName("Should reject demo with blank name")
    void shouldRejectBlankName() {
        // Given - Demo with blank name
        var demo = new Demo(null, "");

        // When
        var response = testRestTemplate.postForEntity(baseUrl(), demo, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //noinspection unchecked
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Name must not be blank");
    }

    @Test
    @DisplayName("Should reject demo with null name")
    void shouldRejectNullName() {
        // Given - Demo with null name
        var demo = new Demo(null, null);

        // When
        var response = testRestTemplate.postForEntity(baseUrl(), demo, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //noinspection unchecked
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Name must not be blank");
    }

    @Test
    @DisplayName("Should reject demo with whitespace-only name")
    void shouldRejectWhitespaceOnlyName() {
        // Given - Demo with whitespace-only name
        var demo = new Demo(null, "   ");

        // When
        var response = testRestTemplate.postForEntity(baseUrl(), demo, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //noinspection unchecked
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Name must not be blank");
    }

    @Test
    @DisplayName("Should reject demo with name exceeding 50 characters")
    void shouldRejectNameExceeding50Characters() {
        // Given - Demo with name longer than 50 characters
        var longName = "a".repeat(51);
        var demo = new Demo(null, longName);

        // When
        var response = testRestTemplate.postForEntity(baseUrl(), demo, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //noinspection unchecked
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Name must not exceed 50 characters");
    }

    @Test
    @DisplayName("Should accept demo with name of exactly 50 characters")
    void shouldAcceptNameWith50Characters() {
        // Given - Demo with exactly 50 characters
        var maxLengthName = "a".repeat(50);
        var demo = new Demo(null, maxLengthName);

        // When
        var response = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(maxLengthName);

        fixture().trackCreated(response.getBody());
    }

    @Test
    @DisplayName("Should reject blank name on update")
    void shouldRejectBlankNameOnUpdate() {
        // Given - Create a valid demo first
        var validDemo = new Demo(null, "Original Name");
        var createResponse = testRestTemplate.postForEntity(baseUrl(), validDemo, Demo.class);
        assertThat(createResponse.getBody()).isNotNull();
        var demoId = createResponse.getBody().id();
        fixture().trackCreated(createResponse.getBody());

        // When - Try to update with blank name
        var invalidUpdate = new Demo(demoId, "");
        var requestEntity = new HttpEntity<>(invalidUpdate);
        var response = testRestTemplate.exchange(baseUrl() + "/" + demoId, HttpMethod.PUT, requestEntity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //noinspection unchecked
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Name must not be blank");
    }

    @Test
    @DisplayName("Should reject name exceeding 50 characters on update")
    void shouldRejectNameExceeding50CharactersOnUpdate() {
        // Given - Create a valid demo first
        var validDemo = new Demo(null, "Original Name");
        var createResponse = testRestTemplate.postForEntity(baseUrl(), validDemo, Demo.class);
        assertThat(createResponse.getBody()).isNotNull();
        var demoId = createResponse.getBody().id();
        fixture().trackCreated(createResponse.getBody());

        // When - Try to update with name > 50 characters
        var longName = "a".repeat(51);
        var invalidUpdate = new Demo(demoId, longName);
        var requestEntity = new HttpEntity<>(invalidUpdate);
        var response = testRestTemplate.exchange(baseUrl() + "/" + demoId, HttpMethod.PUT, requestEntity, Map.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        //noinspection unchecked
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().toString()).contains("Name must not exceed 50 characters");
    }
}
