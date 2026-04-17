package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    // Checkout — verifikasi stok & saldo (TODO: integrasi Inventory & Wallet)
    OrderResponse checkout(CreateOrderRequest request);

    // Pembatalan oleh Jastiper — wajib trigger refund (TODO: integrasi Wallet)
    OrderResponse cancelOrder(String orderId, String cancellationReason) throws Throwable;

    // Sistem rating setelah COMPLETED — kirim ke Profil (TODO: integrasi Profil)
    OrderResponse submitRating(UUID orderId, RatingRequest request);

    List<Order> getAllOrder();

    List<Order> getOrderByTitipersId(String titipersId);

    List<Order> getOrderByJastiperId(String jastiperId);

    Optional<Order> getOrderByOrderIdAndStatus(UUID orderId, OrderStatus status);

    List<Order> getOrderByStatus(OrderStatus status);

    Order getOrderById(UUID orderId);

    OrderResponse updateOrderStatus(UUID orderId, OrderStatus status);

}
