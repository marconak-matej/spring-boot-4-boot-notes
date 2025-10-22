package io.github.mm.http.client.demo;

import io.github.mm.http.client.demo.rest.Demo;
import java.util.ArrayList;
import java.util.List;

public class DemoFixture {

    private final List<String> createdDemoIds = new ArrayList<>();

    // Factory methods for common test scenarios

    public Demo defaultDemo() {
        return new Demo(null, "Test Demo");
    }

    public Demo demoWithName(String name) {
        return new Demo(null, name);
    }

    public Demo demoWithId(String id, String name) {
        return new Demo(id, name);
    }

    public Demo demoForUpdate() {
        return new Demo(null, "Demo to Update");
    }

    public Demo demoForDeletion() {
        return new Demo(null, "Demo to Delete");
    }

    public Demo demoWithSpecificName() {
        return new Demo(null, "Specific Demo");
    }

    // Customization methods

    public Demo withCustomName(Demo demo, String name) {
        return new Demo(demo.id(), name);
    }

    // Builder-style methods

    public DemoBuilder builder() {
        return new DemoBuilder();
    }

    // Lifecycle management

    public void trackCreated(Demo demo) {
        if (demo != null && demo.id() != null) {
            createdDemoIds.add(demo.id());
        }
    }

    public List<String> getCreatedDemoIds() {
        return new ArrayList<>(createdDemoIds);
    }

    public void clearTrackedIds() {
        createdDemoIds.clear();
    }

    // Helper methods

    // Builder class for more complex scenarios
    public static class DemoBuilder {
        private String name = "Default Demo";

        public DemoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Demo build() {
            return new Demo(null, name);
        }
    }

    // Batch creation methods
    public List<Demo> multipleDemos(int count) {
        var demos = new ArrayList<Demo>();
        for (int i = 1; i <= count; i++) {
            demos.add(new Demo(null, "Demo " + i));
        }
        return demos;
    }
}
