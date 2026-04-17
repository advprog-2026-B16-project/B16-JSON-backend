package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight in-memory fixture used by order tests.
 *
 * Note: this is intentionally not an OrderRepository implementation because
 * OrderRepository extends JpaRepository and carries many framework methods.
 */
class StubOrderRepository {

    private final List<Order> storage = new ArrayList<>();

    void add(Order order) {
        storage.add(order);
    }

    List<Order> getAll() {
        return new ArrayList<>(storage);
    }
}
