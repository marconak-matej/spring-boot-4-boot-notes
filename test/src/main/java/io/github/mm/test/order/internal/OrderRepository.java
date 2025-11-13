package io.github.mm.test.order.internal;

import io.github.mm.test.order.model.Order;

public interface OrderRepository {

    Order save(Order order);
}
