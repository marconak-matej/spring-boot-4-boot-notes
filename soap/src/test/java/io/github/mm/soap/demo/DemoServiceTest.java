package io.github.mm.soap.demo;

import static org.assertj.core.api.Assertions.*;

import io.github.mm.soap.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DemoService")
class DemoServiceTest {

    private DemoService service;

    @BeforeEach
    void setUp() {
        service = new DemoService();
    }

    @Nested
    @DisplayName("createDemo")
    class CreateDemoTests {

        @Test
        @DisplayName("should create a demo with valid name")
        void shouldCreateDemo() {
            var demo = service.createDemo("Test Demo");

            assertThat(demo).isNotNull().satisfies(d -> {
                assertThat(d.id()).isNotBlank();
                assertThat(d.name()).isEqualTo("Test Demo");
            });
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when name is null")
        void shouldThrowWhenNameIsNull() {
            assertThatThrownBy(() -> service.createDemo(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name must not be blank");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when name is blank")
        void shouldThrowWhenNameIsBlank() {
            assertThatThrownBy(() -> service.createDemo("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name must not be blank");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when name exceeds 50 characters")
        void shouldThrowWhenNameTooLong() {
            String longName = "a".repeat(51);
            assertThatThrownBy(() -> service.createDemo(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name must not exceed 50 characters");
        }
    }

    @Nested
    @DisplayName("updateDemo")
    class UpdateDemoTests {

        @Test
        @DisplayName("should update demo with valid name")
        void shouldUpdateDemo() {
            var created = service.createDemo("Original");
            var updated = service.updateDemo(created.id(), "Updated");

            assertThat(updated).satisfies(d -> {
                assertThat(d.id()).isEqualTo(created.id());
                assertThat(d.name()).isEqualTo("Updated");
            });
        }

        @Test
        @DisplayName("should throw NotFoundException when demo does not exist")
        void shouldThrowWhenDemoNotFound() {
            assertThatThrownBy(() -> service.updateDemo("non-existent-id", "New Name"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when name is blank")
        void shouldThrowWhenNameIsBlank() {
            var created = service.createDemo("Original");
            assertThatThrownBy(() -> service.updateDemo(created.id(), ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name must not be blank");
        }
    }

    @Nested
    @DisplayName("getDemoById")
    class GetDemoByIdTests {

        @Test
        @DisplayName("should retrieve demo by id")
        void shouldGetDemo() {
            var created = service.createDemo("Test Demo");
            var retrieved = service.getDemoById(created.id());

            assertThat(retrieved).isEqualTo(created);
        }

        @Test
        @DisplayName("should throw NotFoundException when demo does not exist")
        void shouldThrowWhenDemoNotFound() {
            assertThatThrownBy(() -> service.getDemoById("non-existent-id"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("getAllDemos")
    class GetAllDemosTests {

        @Test
        @DisplayName("should return empty list when no demos exist")
        void shouldReturnEmptyList() {
            var demos = service.getAllDemos();

            assertThat(demos).isEmpty();
        }

        @Test
        @DisplayName("should return all created demos")
        void shouldReturnAllDemos() {
            service.createDemo("Demo 1");
            service.createDemo("Demo 2");
            service.createDemo("Demo 3");

            var demos = service.getAllDemos();

            assertThat(demos).hasSize(3);
        }
    }

    @Nested
    @DisplayName("deleteDemo")
    class DeleteDemoTests {

        @Test
        @DisplayName("should delete demo by id")
        void shouldDeleteDemo() {
            var created = service.createDemo("Test Demo");
            service.deleteDemo(created.id());

            assertThatThrownBy(() -> service.getDemoById(created.id())).isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("should throw NotFoundException when demo does not exist")
        void shouldThrowWhenDemoNotFound() {
            assertThatThrownBy(() -> service.deleteDemo("non-existent-id"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }
}
