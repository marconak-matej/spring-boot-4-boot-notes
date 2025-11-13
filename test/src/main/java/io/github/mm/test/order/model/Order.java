package io.github.mm.test.order.model;

public record Order(Long id, String customerId, OrderStatus status, String requestId) {

    public Order {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be blank");
        }
    }
}
