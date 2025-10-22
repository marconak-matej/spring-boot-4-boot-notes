package io.github.mm.grpc.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.mm.grpc.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DemoServiceTest {

    private DemoService service;

    @BeforeEach
    void setUp() {
        service = new DemoService();
    }

    @Test
    void shouldCreateDemo() {
        var demo = service.createDemo("Test Demo");

        assertThat(demo.id()).isNotNull();
        assertThat(demo.name()).isEqualTo("Test Demo");
    }

    @Test
    void shouldGetDemoById() {
        var created = service.createDemo("Test Demo");

        var demo = service.getDemoById(created.id());

        assertThat(demo.id()).isEqualTo(created.id());
        assertThat(demo.name()).isEqualTo("Test Demo");
    }

    @Test
    void shouldThrowExceptionWhenDemoNotFound() {
        assertThrows(NotFoundException.class, () -> service.getDemoById("non-existent-id"));
    }

    @Test
    void shouldUpdateDemo() {
        var created = service.createDemo("Original Name");

        var updated = service.updateDemo(created.id(), "Updated Name");

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.name()).isEqualTo("Updated Name");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentDemo() {
        assertThrows(NotFoundException.class, () -> service.updateDemo("non-existent-id", "New Name"));
    }

    @Test
    void shouldDeleteDemo() {
        var created = service.createDemo("Test Demo");

        service.deleteDemo(created.id());

        assertThrows(NotFoundException.class, () -> service.getDemoById(created.id()));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentDemo() {
        assertThrows(NotFoundException.class, () -> service.deleteDemo("non-existent-id"));
    }

    @Test
    void shouldGetAllDemos() {
        service.createDemo("Demo 1");
        service.createDemo("Demo 2");
        service.createDemo("Demo 3");

        var demos = service.getAllDemos();

        assertThat(demos).hasSize(3);
    }

    @Test
    void shouldValidateBlankName() {
        assertThrows(IllegalArgumentException.class, () -> service.createDemo(""));
        assertThrows(IllegalArgumentException.class, () -> service.createDemo("   "));
        assertThrows(IllegalArgumentException.class, () -> service.createDemo(null));
    }

    @Test
    void shouldValidateNameLength() {
        var longName = "a".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> service.createDemo(longName));
    }
}
