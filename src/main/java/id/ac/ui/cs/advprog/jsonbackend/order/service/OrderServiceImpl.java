package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.jsonbackend.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.order.dto.RatingRequest;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.order.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderPricingService orderPricingService;

    @Override
    @Transactional
    public OrderResponse checkout(CreateOrderRequest request) {
        validateCheckoutRequest(request);

        Order order = new Order(
                null,
                request.getProductId(),
                request.getTitipersId(),
                request.getJastiperId(),
                request.getQuantity(),
                request.getShippingAddress()
        );

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId, String cancellationReason) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));

        order.cancel(cancellationReason == null || cancellationReason.isBlank()
                ? "No reason provided"
                : cancellationReason);

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse submitRating(UUID orderId, RatingRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));

        order.submitRating(request.getJastiperRating(), request.getProductRating());

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrderByTitipersId(String titipersId) {
        return orderRepository.findByTitipersId(UUID.fromString(titipersId));
    }

    @Override
    public List<Order> getOrderByJastiperId(String jastiperId) {
        return orderRepository.findByJastiperId(UUID.fromString(jastiperId));
    }

    @Override
    public Optional<Order> getOrderByOrderIdAndStatus(UUID orderId, OrderStatus status) {
        return orderRepository.findByOrderIdAndOrderStatus(orderId, status);
    }

    @Override
    public List<Order> getOrderByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));

        order.updateStatus(status);
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    private void validateCheckoutRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request checkout tidak boleh null");
        }
        if (request.getProductId() == null || request.getProductId().isBlank()) {
            throw new IllegalArgumentException("productId wajib diisi");
        }
        if (request.getTitipersId() == null) {
            throw new IllegalArgumentException("titipersId wajib diisi");
        }
        if (request.getTitipersId().equals(request.getJastiperId())) {
            throw new IllegalArgumentException("Jastiper tidak boleh membeli barang dirinya sendiri");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity harus lebih dari 0");
        }
        if (request.getShippingAddress() == null || request.getShippingAddress().isBlank()) {
            throw new IllegalArgumentException("shippingAddress wajib diisi");
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(orderPricingService.calculateTotal(order))
                .shippingAddress(order.getShippingAddress())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .titipersId(order.getTitipersId())
                .jastiperId(order.getJastiperId())
                .jastiperRating(order.getJastiperRating())
                .productRating(order.getProductRating())
                .cancellationReason(order.getCancellationReason())
                .build();
    }
}
