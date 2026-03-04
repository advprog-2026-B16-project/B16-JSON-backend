package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse checkout(CreateOrderRequest request) {
        // TODO: Verify stock availability via Inventory Module
        // inventoryClient.reserveStock(request.getProductId(), request.getQuantity());

        // TODO: Verify wallet balance via Wallet Module
        // walletClient.deductBalance(request.getTitipersId(), totalPrice);

        Order order = new Order(
                UUID.randomUUID().toString(),
                request.getProductId(),
                request.getTitipersId(),
                request.getJastiperId(),
                request.getQuantity(),
                request.getShippingAddress()
        );

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = findOrderOrThrow(orderId);
        order.updateStatus(request.getNextStatus());
        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancelOrder(String orderId, String cancellationReason) {
        Order order = findOrderOrThrow(orderId);
        order.cancel(cancellationReason);

        // TODO: Trigger refund via Wallet Module
        // walletClient.refund(order.getTitipersId(), totalPrice);

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse submitRating(String orderId, RatingRequest request) {
        Order order = findOrderOrThrow(orderId);
        order.submitRating(request.getJastiperRating(), request.getProductRating());

        // TODO: Send rating data to Profile Module
        // profileClient.updateJastiperRating(order.getJastiperId(), request.getJastiperRating());
        // profileClient.updateProductRating(order.getProductId(), request.getProductRating());

        return mapToResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        return mapToResponse(findOrderOrThrow(orderId));
    }

    @Override
    public List<OrderResponse> getOrdersByTitipersId(String titipersId) {
        return orderRepository.findAllByTitipersId(titipersId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<OrderResponse> getOrdersByJastiperId(String jastiperId) {
        return orderRepository.findAllByJastiperId(jastiperId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<OrderResponse> getOrdersByJastiperIdAndStatus(String jastiperId, OrderStatus status) {
        return orderRepository.findAllByJastiperIdAndOrderStatus(jastiperId, status)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findAllByOrderStatus(status)
                .stream().map(this::mapToResponse).toList();
    }

    private Order findOrderOrThrow(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .titipersId(order.getTitipersId())
                .jastiperId(order.getJastiperId())
                .quantity(order.getQuantity())
                .shippingAddress(order.getShippingAddress())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .jastiperRating(order.getJastiperRating())
                .productRating(order.getProductRating())
                .cancellationReason(order.getCancellationReason())
                .build();
    }
}