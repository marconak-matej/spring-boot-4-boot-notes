package io.github.mm.soap.demo.soap;

import static org.assertj.core.api.Assertions.*;

import io.github.mm.soap.SoapTestConfiguration;
import io.github.mm.soap.gen.*;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SoapTestConfiguration.class)
@DisplayName("SoapDemoEndpoint Integration Tests")
class DemoEndpointIntegrationTest {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        webServiceTemplate.setDefaultUri("http://localhost:" + port + "/ws/demo-service");

        var request = new ListDemosRequest();
        var response = (ListDemosResponse) webServiceTemplate.marshalSendAndReceive(request);

        Objects.requireNonNull(response).getDemo().forEach(demo -> {
            var deleteRequest = new DeleteDemoRequest();
            deleteRequest.setId(demo.getId());
            webServiceTemplate.marshalSendAndReceive(deleteRequest);
        });
    }

    @Nested
    @DisplayName("createDemo")
    class CreateDemoIntegrationTests {

        @Test
        @DisplayName("should create a demo via SOAP endpoint")
        void shouldCreateDemoViaSOAP() {
            var request = new CreateDemoRequest();
            request.setName("Integration Test Demo");

            var response = (DemoResponse) webServiceTemplate.marshalSendAndReceive(request);

            assertThat(response).isNotNull();
            assertThat(response.getDemo()).isNotNull().satisfies(demo -> {
                assertThat(demo.getId()).isNotBlank();
                assertThat(demo.getName()).isEqualTo("Integration Test Demo");
            });
        }

        @Test
        @DisplayName("should reject create request with blank name")
        void shouldRejectBlankName() {
            var request = new CreateDemoRequest();
            request.setName("   ");

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(request))
                    .isInstanceOf(SoapFaultClientException.class);
        }

        @Test
        @DisplayName("should reject create request with name exceeding 50 characters")
        void shouldRejectNameTooLong() {
            var request = new CreateDemoRequest();
            request.setName("a".repeat(51));

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(request))
                    .isInstanceOf(SoapFaultClientException.class);
        }
    }

    @Nested
    @DisplayName("getDemo")
    class GetDemoIntegrationTests {

        @Test
        @DisplayName("should retrieve demo by ID via SOAP endpoint")
        void shouldGetDemoById() {
            // First, create a demo
            var createRequest = new CreateDemoRequest();
            createRequest.setName("Demo to Retrieve");

            var createResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(createRequest);

            assertThat(createResponse).isNotNull();
            var demoId = createResponse.getDemo().getId();

            // Then, retrieve the created demo
            var getRequest = new GetDemoRequest();
            getRequest.setId(demoId);

            var getResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(getRequest);

            assertThat(getResponse).isNotNull();
            assertThat(getResponse.getDemo()).isNotNull().satisfies(demo -> {
                assertThat(demo.getId()).isEqualTo(demoId);
                assertThat(demo.getName()).isEqualTo("Demo to Retrieve");
            });
        }

        @Test
        @DisplayName("should return SOAP fault when demo not found")
        void shouldReturnFaultWhenDemoNotFound() {
            var request = new GetDemoRequest();
            request.setId("non-existent-id");

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(request))
                    .isInstanceOf(SoapFaultClientException.class);
        }

        @Test
        @DisplayName("should reject get request with blank ID")
        void shouldRejectBlankId() {
            var request = new GetDemoRequest();
            request.setId("   ");

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(request))
                    .isInstanceOf(SoapFaultClientException.class);
        }
    }

    @Nested
    @DisplayName("updateDemo")
    class UpdateDemoIntegrationTests {

        @Test
        @DisplayName("should update demo via SOAP endpoint")
        void shouldUpdateDemoViaSOAP() {
            // Create a demo
            var createRequest = new CreateDemoRequest();
            createRequest.setName("Original Name");

            var createResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(createRequest);

            assertThat(createResponse).isNotNull();
            var demoId = createResponse.getDemo().getId();

            // Update the demo
            var updateRequest = new UpdateDemoRequest();
            updateRequest.setId(demoId);
            updateRequest.setName("Updated Name");

            var updateResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(updateRequest);

            assertThat(updateResponse).isNotNull();
            assertThat(updateResponse.getDemo()).isNotNull().satisfies(demo -> {
                assertThat(demo.getId()).isEqualTo(demoId);
                assertThat(demo.getName()).isEqualTo("Updated Name");
            });
        }

        @Test
        @DisplayName("should reject update request for non-existent demo")
        void shouldRejectUpdateNonExistent() {
            var request = new UpdateDemoRequest();
            request.setId("non-existent-id");
            request.setName("New Name");

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(request))
                    .isInstanceOf(SoapFaultClientException.class);
        }

        @Test
        @DisplayName("should reject update request with blank name")
        void shouldRejectBlankName() {
            // Create a demo first
            var createRequest = new CreateDemoRequest();
            createRequest.setName("Test Demo");

            var createResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(createRequest);

            assertThat(createResponse).isNotNull();
            var demoId = createResponse.getDemo().getId();

            // Try to update with blank name
            var updateRequest = new UpdateDemoRequest();
            updateRequest.setId(demoId);
            updateRequest.setName("");

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(updateRequest))
                    .isInstanceOf(SoapFaultClientException.class);
        }
    }

    @Nested
    @DisplayName("listDemos")
    class ListDemosIntegrationTests {

        @Test
        @DisplayName("should return empty list when no demos exist")
        void shouldReturnEmptyListInitially() {
            var request = new ListDemosRequest();

            var response = (ListDemosResponse) webServiceTemplate.marshalSendAndReceive(request);

            assertThat(response).isNotNull();
            assertThat(response.getDemo()).isEmpty();
        }

        @Test
        @DisplayName("should return all created demos")
        void shouldReturnAllDemos() {
            // Create multiple demos
            var names = new String[] {"Demo 1", "Demo 2", "Demo 3"};

            for (String name : names) {
                var createRequest = new CreateDemoRequest();
                createRequest.setName(name);
                webServiceTemplate.marshalSendAndReceive(createRequest);
            }

            // List all demos
            var listRequest = new ListDemosRequest();

            var response = (ListDemosResponse) webServiceTemplate.marshalSendAndReceive(listRequest);

            assertThat(response).isNotNull();
            assertThat(response.getDemo()).hasSize(3);
            assertThat(response.getDemo())
                    .extracting(Demo::getName)
                    .containsExactlyInAnyOrder("Demo 1", "Demo 2", "Demo 3");
        }
    }

    @Nested
    @DisplayName("deleteDemo")
    class DeleteDemoIntegrationTests {

        @Test
        @DisplayName("should delete demo via SOAP endpoint")
        void shouldDeleteDemoViaSOAP() {
            // Create a demo
            var createRequest = new CreateDemoRequest();
            createRequest.setName("Demo to Delete");

            var createResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(createRequest);

            assertThat(createResponse).isNotNull();
            var demoId = createResponse.getDemo().getId();

            // Delete the demo
            var deleteRequest = new DeleteDemoRequest();
            deleteRequest.setId(demoId);

            var deleteResponse = (DeleteDemoResponse) webServiceTemplate.marshalSendAndReceive(deleteRequest);

            assertThat(deleteResponse).isNotNull();
            assertThat(deleteResponse.isSuccess()).isTrue();

            // Verify it's deleted
            var getRequest = new GetDemoRequest();
            getRequest.setId(demoId);

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(getRequest))
                    .isInstanceOf(SoapFaultClientException.class);
        }

        @Test
        @DisplayName("should reject delete request for non-existent demo")
        void shouldRejectDeleteNonExistent() {
            var request = new DeleteDemoRequest();
            request.setId("non-existent-id");

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(request))
                    .isInstanceOf(SoapFaultClientException.class);
        }

        @Test
        @DisplayName("should reject delete request with blank ID")
        void shouldRejectBlankId() {
            var request = new DeleteDemoRequest();
            request.setId("   ");

            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(request))
                    .isInstanceOf(SoapFaultClientException.class);
        }
    }

    @Nested
    @DisplayName("End-to-End Workflow")
    class EndToEndWorkflowTests {

        @Test
        @DisplayName("should complete full CRUD workflow via SOAP")
        void shouldCompleteFullCRUDWorkflow() {
            // CREATE
            var createRequest = new CreateDemoRequest();
            createRequest.setName("Workflow Demo");

            var createResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(createRequest);

            assertThat(createResponse).isNotNull();
            assertThat(createResponse.getDemo()).isNotNull();
            var demoId = createResponse.getDemo().getId();

            // READ
            var getRequest = new GetDemoRequest();
            getRequest.setId(demoId);

            var getResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(getRequest);

            assertThat(getResponse).isNotNull();
            assertThat(getResponse.getDemo()).isNotNull();
            assertThat(getResponse.getDemo().getName()).isEqualTo("Workflow Demo");

            // UPDATE
            var updateRequest = new UpdateDemoRequest();
            updateRequest.setId(demoId);
            updateRequest.setName("Updated Workflow Demo");

            var updateResponse = (DemoResponse) webServiceTemplate.marshalSendAndReceive(updateRequest);

            assertThat(updateResponse).isNotNull();
            assertThat(updateResponse.getDemo().getName()).isEqualTo("Updated Workflow Demo");

            // LIST
            var listRequest = new ListDemosRequest();

            var listResponse = (ListDemosResponse) webServiceTemplate.marshalSendAndReceive(listRequest);

            assertThat(listResponse).isNotNull();
            assertThat(listResponse.getDemo()).extracting(Demo::getId).contains(demoId);

            // DELETE
            var deleteRequest = new DeleteDemoRequest();
            deleteRequest.setId(demoId);

            var deleteResponse = (DeleteDemoResponse) webServiceTemplate.marshalSendAndReceive(deleteRequest);

            assertThat(deleteResponse).isNotNull();
            assertThat(deleteResponse.isSuccess()).isTrue();

            // Verify deletion
            assertThatThrownBy(() -> webServiceTemplate.marshalSendAndReceive(getRequest))
                    .isInstanceOf(SoapFaultClientException.class);
        }
    }
}
