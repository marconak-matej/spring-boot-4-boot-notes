package io.github.mm.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.mm.http.client.demo.Demo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TestRestTemplateIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
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
    void shouldGetAllDemos() {
        // Given - Create some demos
        var demos = fixture().multipleDemos(2);
        for (var demo : demos) {
            var response = testRestTemplate.postForEntity(baseUrl(), demo, Demo.class);
            fixture().trackCreated(response.getBody());
        }

        // When
        var response = testRestTemplate.exchange(
                baseUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Demo>>() {});

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
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
    void shouldReturn404WhenDemoNotFound() {
        // When
        var response = testRestTemplate.getForEntity(baseUrl() + "/999999", Demo.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
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
}
