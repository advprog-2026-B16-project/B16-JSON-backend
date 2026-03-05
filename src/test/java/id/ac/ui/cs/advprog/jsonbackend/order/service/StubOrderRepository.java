package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.order.repository.OrderRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * StubOrderRepository adalah implementasi stub dari OrderRepository untuk keperluan unit test.
 * Tidak menggunakan Mockito — murni plain Java.
 */
class StubOrderRepository implements OrderRepository {

    private final Map<String, Order> storage = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        storage.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(storage.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Order> findAllByTitipersId(String titipersId) {
        return storage.values().stream()
                .filter(o -> titipersId.equals(o.getTitipersId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAllByJastiperId(String jastiperId) {
        return storage.values().stream()
                .filter(o -> jastiperId.equals(o.getJastiperId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAllByOrderStatus(OrderStatus status) {
        return storage.values().stream()
                .filter(o -> status == o.getOrderStatus())
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAllByJastiperIdAndOrderStatus(String jastiperId, OrderStatus status) {
        return storage.values().stream()
                .filter(o -> jastiperId.equals(o.getJastiperId()) && status == o.getOrderStatus())
                .collect(Collectors.toList());
    }
}

