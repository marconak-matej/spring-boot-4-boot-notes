package io.github.mm.test.events;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mm.test.order.OrderService;
import io.github.mm.test.order.event.OrderCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents // Spring 7: Records all events during test
@DisplayName("ApplicationEvents Testing (Spring Framework 7)")
class ApplicationEventsTest {

    @Autowired
    OrderService service;

    @Test
    @DisplayName("Should record and verify OrderCreatedEvent")
    void shouldRecordOrderCreatedEvent(ApplicationEvents events) {
        // Spring 7: ApplicationEvents injected as method parameter

        // Given
        var customerId = "CUST-001";

        // When - Execute business logic that publishes events
        var order = service.createOrder(customerId);

        // Then - Verify event was published
        var eventCount = events.stream(OrderCreatedEvent.class).count();

        assertThat(eventCount)
                .as("Exactly one OrderCreatedEvent should be published")
                .isEqualTo(1);

        // Verify event details
        var event = events.stream(OrderCreatedEvent.class)
                .findFirst()
                .orElseThrow(() -> new AssertionError("OrderCreatedEvent not found"));

        assertThat(event.orderId()).as("Event should contain correct order ID").isEqualTo(order.id());

        assertThat(event.customerId())
                .as("Event should contain correct customer ID")
                .isEqualTo(customerId);

        assertThat(event.requestId())
                .as("Event should contain request ID")
                .isNotNull()
                .startsWith("REQ-");
    }

    @Test
    @DisplayName("Should record multiple events")
    void shouldRecordMultipleEvents(ApplicationEvents events) {

        // When - Create multiple orders
        service.createOrder("CUST-001");
        service.createOrder("CUST-002");
        service.createOrder("CUST-003");

        // Then - All events should be recorded
        var eventCount = events.stream(OrderCreatedEvent.class).count();

        assertThat(eventCount).as("Three OrderCreatedEvent should be published").isEqualTo(3);

        // Verify all customer IDs
        var customerIds = events.stream(OrderCreatedEvent.class)
                .map(OrderCreatedEvent::customerId)
                .toList();

        assertThat(customerIds).containsExactly("CUST-001", "CUST-002", "CUST-003");
    }

    @Test
    @DisplayName("Should filter events by type")
    void shouldFilterEventsByType(ApplicationEvents events) {
        // When - Create orders
        service.createOrder("CUST-001");
        service.createOrder("CUST-002");

        // Then - Filter only OrderCreatedEvent (not other event types)
        var orderEvents = events.stream(OrderCreatedEvent.class).toList();

        assertThat(orderEvents)
                .as("Should contain only OrderCreatedEvent instances")
                .hasSize(2);

        assertThat(orderEvents).allMatch(_ -> true);
    }

    @Test
    @DisplayName("Should verify event data correctness")
    void shouldVerifyEventData(ApplicationEvents events) {
        // Given
        String customerId = "IMPORTANT-CUSTOMER";

        // When
        var order = service.createOrder(customerId);

        // Then - Detailed event data verification
        var event = events.stream(OrderCreatedEvent.class).findFirst().orElseThrow();

        // Verify all event fields
        assertThat(event)
                .extracting(OrderCreatedEvent::orderId, OrderCreatedEvent::customerId)
                .containsExactly(order.id(), customerId);

        assertThat(event.requestId()).matches("REQ-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    @DisplayName("Should support event stream operations")
    void shouldSupportStreamOperations(ApplicationEvents events) {
        // When
        service.createOrder("CUST-A");
        service.createOrder("CUST-B");
        service.createOrder("CUST-C");

        // Then - Use Stream API operations
        var customerIds = events.stream(OrderCreatedEvent.class)
                .map(OrderCreatedEvent::customerId)
                .filter(id -> id.startsWith("CUST-"))
                .sorted()
                .toList();

        assertThat(customerIds).containsExactly("CUST-A", "CUST-B", "CUST-C");

        // Count events
        var count = events.stream(OrderCreatedEvent.class).count();
        assertThat(count).isEqualTo(3);

        // Check if any event exists
        var hasEvents = events.stream(OrderCreatedEvent.class).findAny().isPresent();

        assertThat(hasEvents).isTrue();
    }
}
