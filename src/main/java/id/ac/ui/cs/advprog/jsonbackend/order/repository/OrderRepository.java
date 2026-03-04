package id.ac.ui.cs.advprog.jsonbackend.order.repository;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String orderId);
    List<Order> findAll();
    List<Order> findAllByTitipersId(String titipersId);
    List<Order> findAllByJastiperId(String jastiperId);
    List<Order> findAllByOrderStatus(OrderStatus status);
    List<Order> findAllByJastiperIdAndOrderStatus(String jastiperId, OrderStatus status);
}