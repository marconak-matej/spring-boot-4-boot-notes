package io.github.mm.grpc.demo;

import io.github.mm.grpc.proto.CreateDemoRequest;
import io.github.mm.grpc.proto.Demo;
import java.util.ArrayList;
import java.util.List;

public class DemoFixture {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<String> createdDemoIds = new ArrayList<>();

    // Factory methods for common test scenarios

    public CreateDemoRequest defaultDemoRequest() {
        return CreateDemoRequest.newBuilder().setName("Test Demo").build();
    }

    public CreateDemoRequest demoRequestForDeletion() {
        return CreateDemoRequest.newBuilder().setName("Demo to Delete").build();
    }

    public CreateDemoRequest blankNameRequest() {
        return CreateDemoRequest.newBuilder().setName("").build();
    }

    public CreateDemoRequest tooLongNameRequest() {
        return CreateDemoRequest.newBuilder().setName("a".repeat(51)).build();
    }

    // Lifecycle management

    public void trackCreated(Demo demo) {
        if (demo != null && !demo.getId().isEmpty()) {
            createdDemoIds.add(demo.getId());
        }
    }

    public void clearTrackedIds() {
        createdDemoIds.clear();
    }

    // Batch creation methods
    public List<CreateDemoRequest> multipleDemoRequests(int count) {
        var requests = new ArrayList<CreateDemoRequest>();
        for (int i = 1; i <= count; i++) {
            requests.add(CreateDemoRequest.newBuilder().setName("Demo " + i).build());
        }
        return requests;
    }

    // Helper methods for common demo names
    public CreateDemoRequest originalNameRequest() {
        return CreateDemoRequest.newBuilder().setName("Original Name").build();
    }
}
