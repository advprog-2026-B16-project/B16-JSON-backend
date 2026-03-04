package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import java.util.List;

public interface OrderService {
    // Checkout — verifikasi stok & saldo (TODO: integrasi Inventory & Wallet)
    OrderResponse checkout(CreateOrderRequest request);

    // Status management
    OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request);

    // Pembatalan oleh Jastiper — wajib trigger refund (TODO: integrasi Wallet)
    OrderResponse cancelOrder(String orderId, String cancellationReason);

    // Sistem rating setelah COMPLETED — kirim ke Profil (TODO: integrasi Profil)
    OrderResponse submitRating(String orderId, RatingRequest request);

    // Monitoring Titipers
    List<OrderResponse> getOrdersByTitipersId(String titipersId);

    // Monitoring Jastiper (to-do list, in-progress, done)
    List<OrderResponse> getOrdersByJastiperId(String jastiperId);
    List<OrderResponse> getOrdersByJastiperIdAndStatus(String jastiperId, OrderStatus status);

    // Monitoring Admin
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderResponse getOrderById(String orderId);

}
