package io.github.mm.grpc.demo.api.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.mm.grpc.demo.DemoFixture;
import io.github.mm.grpc.proto.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootTest(
        properties = {
            "spring.grpc.server.port=0",
            "spring.grpc.client.channels.demo.address=static://localhost:${local.grpc.port}"
        })
@ImportGrpcClients(types = DemoServiceGrpc.DemoServiceBlockingStub.class)
@DisplayName("gRPC Demo Service Integration Tests")
class GrpcDemoServiceIntegrationTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private DemoServiceGrpc.DemoServiceBlockingStub blockingStub;

    private DemoFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new DemoFixture();

        // Clear all demos before each test to ensure test isolation
        var request = ListDemosRequest.newBuilder().build();
        var response = blockingStub.listDemos(request);
        for (var demo : response.getDemosList()) {
            var deleteRequest =
                    DeleteDemoRequest.newBuilder().setId(demo.getId()).build();
            //noinspection ResultOfMethodCallIgnored
            blockingStub.deleteDemo(deleteRequest);
        }

        fixture.clearTrackedIds();
    }

    @Test
    @DisplayName("Should create a demo with valid name")
    void shouldCreateDemo() {
        var request = fixture.defaultDemoRequest();

        var response = blockingStub.createDemo(request);

        assertThat(response.getDemo().getId()).isNotNull();
        assertThat(response.getDemo().getName()).isEqualTo("Test Demo");
        fixture.trackCreated(response.getDemo());
    }

    @Test
    @DisplayName("Should fail to create demo with blank name")
    void shouldFailToCreateDemoWithBlankName() {
        var request = fixture.blankNameRequest();

        var exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.createDemo(request));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).contains("Name must not be blank");
    }

    @Test
    @DisplayName("Should fail to create demo with name exceeding 50 characters")
    void shouldFailToCreateDemoWithTooLongName() {
        var request = fixture.tooLongNameRequest();

        var exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.createDemo(request));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).contains("Name must not exceed 50 characters");
    }

    @Test
    @DisplayName("Should get demo by ID")
    void shouldGetDemo() {
        var createRequest = fixture.defaultDemoRequest();
        var createResponse = blockingStub.createDemo(createRequest);
        var demoId = createResponse.getDemo().getId();
        fixture.trackCreated(createResponse.getDemo());

        var getRequest = GetDemoRequest.newBuilder().setId(demoId).build();
        var response = blockingStub.getDemo(getRequest);

        assertThat(response.getDemo().getId()).isEqualTo(demoId);
        assertThat(response.getDemo().getName()).isEqualTo("Test Demo");
    }

    @Test
    @DisplayName("Should fail to get non-existent demo")
    void shouldFailToGetNonExistentDemo() {
        var request = GetDemoRequest.newBuilder().setId("non-existent-id").build();

        var exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.getDemo(request));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    @DisplayName("Should update demo with new name")
    void shouldUpdateDemo() {
        var createRequest = fixture.originalNameRequest();
        var createResponse = blockingStub.createDemo(createRequest);
        var demoId = createResponse.getDemo().getId();
        fixture.trackCreated(createResponse.getDemo());

        var updateRequest = UpdateDemoRequest.newBuilder()
                .setId(demoId)
                .setName("Updated Name")
                .build();
        var response = blockingStub.updateDemo(updateRequest);

        assertThat(response.getDemo().getId()).isEqualTo(demoId);
        assertThat(response.getDemo().getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Should fail to update non-existent demo")
    void shouldFailToUpdateNonExistentDemo() {
        var request = UpdateDemoRequest.newBuilder()
                .setId("non-existent-id")
                .setName("New Name")
                .build();

        var exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.updateDemo(request));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    @DisplayName("Should list all demos")
    void shouldListDemos() {
        var requests = fixture.multipleDemoRequests(3);
        for (var request : requests) {
            var response = blockingStub.createDemo(request);
            fixture.trackCreated(response.getDemo());
        }

        var listRequest = ListDemosRequest.newBuilder().build();
        var response = blockingStub.listDemos(listRequest);

        assertThat(response.getDemosList()).hasSize(3);
        assertThat(response.getDemosList())
                .extracting(Demo::getName)
                .containsExactlyInAnyOrder("Demo 1", "Demo 2", "Demo 3");
    }

    @Test
    @DisplayName("Should delete demo by ID")
    void shouldDeleteDemo() {
        var createRequest = fixture.demoRequestForDeletion();
        var createResponse = blockingStub.createDemo(createRequest);
        var demoId = createResponse.getDemo().getId();
        fixture.trackCreated(createResponse.getDemo());

        var deleteRequest = DeleteDemoRequest.newBuilder().setId(demoId).build();
        var response = blockingStub.deleteDemo(deleteRequest);

        assertThat(response.getSuccess()).isTrue();

        var getRequest = GetDemoRequest.newBuilder().setId(demoId).build();
        var exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.getDemo(getRequest));
        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    @DisplayName("Should fail to delete non-existent demo")
    void shouldFailToDeleteNonExistentDemo() {
        var request = DeleteDemoRequest.newBuilder().setId("non-existent-id").build();

        var exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.deleteDemo(request));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
    }
}
