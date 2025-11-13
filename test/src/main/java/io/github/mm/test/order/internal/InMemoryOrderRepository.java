package io.github.mm.test.order.internal;

import io.github.mm.test.order.model.Order;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ConcurrentHashMap<Long, Order> store = new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Order save(Order order) {
        var orderToSave = order;
        if (order.id() == null) {
            orderToSave =
                    new Order(idGenerator.getAndIncrement(), order.customerId(), order.status(), order.requestId());
        }
        store.put(orderToSave.id(), orderToSave);
        return orderToSave;
    }
}
