package io.github.mm.test.order.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Order created event received: orderId={}, customerId={}", event.orderId(), event.customerId());
        // In real application: send notification, update analytics, etc.
    }
}
