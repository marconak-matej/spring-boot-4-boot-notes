package io.github.mm.test.order.event;

public record OrderCreatedEvent(Long orderId, String customerId, String requestId) {}
