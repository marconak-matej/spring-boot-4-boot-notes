package io.github.mm.test.order;

import io.github.mm.test.order.event.OrderCreatedEvent;
import io.github.mm.test.order.internal.OrderRepository;
import io.github.mm.test.order.internal.RequestIdGenerator;
import io.github.mm.test.order.model.Order;
import io.github.mm.test.order.model.OrderStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final RequestIdGenerator generator;
    private final ApplicationEventPublisher publisher;

    public OrderService(OrderRepository repository, RequestIdGenerator generator, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.generator = generator;
        this.publisher = publisher;
    }

    public Order createOrder(String customerId) {
        // Generate unique request ID using prototype bean
        var requestId = generator.generate();

        var order = new Order(null, customerId, OrderStatus.CREATED, requestId);
        var savedOrder = repository.save(order);

        // Publish domain event
        publisher.publishEvent(new OrderCreatedEvent(savedOrder.id(), savedOrder.customerId(), savedOrder.requestId()));

        return savedOrder;
    }
}
