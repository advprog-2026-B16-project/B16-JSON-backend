package id.ac.ui.cs.advprog.jsonbackend.features.order.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.service.JastiperOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JastiperOrderControllerTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TITIPERS_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JASTIPER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private JastiperOrderService jastiperOrderService;

    private JastiperOrderController controller;

    @BeforeEach
    void setUp() {
        controller = new JastiperOrderController(jastiperOrderService);
    }

    @Test
    void getMyOrdersShouldDelegateToService() {
        User jastiper = user();
        Order order = order(OrderStatus.PURCHASED);
        when(jastiperOrderService.getMyOrders(jastiper)).thenReturn(List.of(order));

        ResponseEntity<List<Order>> response = controller.getMyOrders(jastiper);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(order), response.getBody());
        verify(jastiperOrderService).getMyOrders(jastiper);
    }

    @Test
    void markAsShippedShouldDelegateToService() {
        User jastiper = user();
        OrderResponse expected = orderResponse(OrderStatus.SHIPPED);
        when(jastiperOrderService.markAsShipped(jastiper, ORDER_ID)).thenReturn(expected);

        ResponseEntity<OrderResponse> response = controller.markAsShipped(jastiper, ORDER_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(OrderStatus.SHIPPED, response.getBody().getOrderStatus());
        verify(jastiperOrderService).markAsShipped(jastiper, ORDER_ID);
    }

    @Test
    void markAsCompletedShouldDelegateToService() {
        User jastiper = user();
        OrderResponse expected = orderResponse(OrderStatus.COMPLETED);
        when(jastiperOrderService.markAsCompleted(jastiper, ORDER_ID)).thenReturn(expected);

        ResponseEntity<OrderResponse> response = controller.markAsCompleted(jastiper, ORDER_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(OrderStatus.COMPLETED, response.getBody().getOrderStatus());
        verify(jastiperOrderService).markAsCompleted(jastiper, ORDER_ID);
    }

    private static User user() {
        return User.builder()
                .id(JASTIPER_ID)
                .username("jastiper")
                .email("jastiper@example.com")
                .password("secret")
                .role(UserRole.JASTIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private static Order order(OrderStatus status) {
        Order order = new Order(ORDER_ID, "prod-abc-123", TITIPERS_ID, JASTIPER_ID, 2, "Jl. Margonda Raya No. 1");
        order.setOrderStatus(status);
        return order;
    }

    private static OrderResponse orderResponse(OrderStatus status) {
        return OrderResponse.builder()
                .orderId(ORDER_ID)
                .productId("prod-abc-123")
                .quantity(2)
                .totalAmount(BigDecimal.valueOf(250000L))
                .shippingAddress("Jl. Margonda Raya No. 1")
                .orderStatus(status)
                .createdAt(LocalDateTime.of(2026, 3, 6, 14, 30))
                .updatedAt(LocalDateTime.of(2026, 3, 6, 14, 31))
                .titipersId(TITIPERS_ID)
                .jastiperId(JASTIPER_ID)
                .build();
    }
}
