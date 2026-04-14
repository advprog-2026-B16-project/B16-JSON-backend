package id.ac.ui.cs.advprog.jsonbackend.order.repository;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByTitipersId(UUID titipersId);
    List<Order> findByJastiperId(UUID jastiperId);
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findByOrderIdAndOrderStatus(UUID orderId, OrderStatus orderStatus);
}