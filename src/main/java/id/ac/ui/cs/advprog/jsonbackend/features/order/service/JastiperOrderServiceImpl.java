package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.state.OrderStateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JastiperOrderServiceImpl implements JastiperOrderService {

    private final OrderRepository orderRepository;
    private final OrderPricingService orderPricingService;

    @Override
    @Transactional(readOnly = true)
    public List<Order> getMyOrders(User authenticatedUser) {
        User jastiper = requireJastiper(authenticatedUser);
        return orderRepository.findByJastiperId(jastiper.getId());
    }

    @Override
    @Transactional
    public OrderResponse markAsShipped(User authenticatedUser, UUID orderId) {
        return updateJastiperOrderStatus(authenticatedUser, orderId, OrderStatus.SHIPPED);
    }

    @Override
    @Transactional
    public OrderResponse markAsCompleted(User authenticatedUser, UUID orderId) {
        return updateJastiperOrderStatus(authenticatedUser, orderId, OrderStatus.COMPLETED);
    }

    private OrderResponse updateJastiperOrderStatus(User authenticatedUser, UUID orderId, OrderStatus nextStatus) {
        User jastiper = requireJastiper(authenticatedUser);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));

        if (!jastiper.getId().equals(order.getJastiperId())) {
            throw new IllegalStateException("Jastiper hanya boleh mengubah status order miliknya sendiri");
        }

        OrderStateFactory.getState(order.getOrderStatus()).validateTransition(nextStatus);
        order.updateStatus(nextStatus);
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    private User requireJastiper(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authentication is required");
        }
        if (authenticatedUser.getRole() != UserRole.JASTIPER) {
            throw new IllegalStateException("Only jastiper can access this order workflow");
        }
        return authenticatedUser;
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
