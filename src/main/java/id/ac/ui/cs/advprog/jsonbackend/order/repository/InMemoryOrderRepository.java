package id.ac.ui.cs.advprog.jsonbackend.order.repository;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * InMemoryOrderRepository adalah implementasi sementara dari OrderRepository.
 *
 * ⚠️ TEMPORARY IMPLEMENTATION — HANYA UNTUK DEVELOPMENT & TESTING
 * Implementasi ini menggunakan ConcurrentHashMap sebagai penyimpanan data di memori (RAM).
 * Data akan hilang setiap kali aplikasi di-restart.
 *
 * TODO: Replace dengan implementasi database (PostgreSQL via Supabase) setelah skema database siap.
 * Cara replace:
 * 1. Buat class JpaOrderRepository yang implements OrderRepository
 * 2. Hubungkan ke entity Order dengan @Entity dan @Table
 * 3. Hapus atau nonaktifkan @Repository di class ini agar Spring tidak konflik
 *
 * Menggunakan ConcurrentHashMap untuk thread-safety saat ada banyak request bersamaan
 * (sesuai requirement "War Engine" — lonjakan permintaan pembuatan pesanan).
 */

@Repository
public class InMemoryOrderRepository implements OrderRepository {

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

