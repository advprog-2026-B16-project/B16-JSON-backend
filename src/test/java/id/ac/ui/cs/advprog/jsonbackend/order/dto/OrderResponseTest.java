package id.ac.ui.cs.advprog.jsonbackend.order.dto;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {

    @Test
    void testBuilderCreatesOrderResponseWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        OrderResponse response = OrderResponse.builder()
                .orderId("order-001")
                .productId("prod-001")
                .titipersId("titipers-001")
                .jastiperId("jastiper-001")
                .quantity(2)
                .shippingAddress("Jl. Margonda No.1")
                .orderStatus(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(null)
                .jastiperRating(null)
                .productRating(null)
                .cancellationReason(null)
                .build();

        assertEquals("order-001", response.getOrderId());
        assertEquals("prod-001", response.getProductId());
        assertEquals("titipers-001", response.getTitipersId());
        assertEquals("jastiper-001", response.getJastiperId());
        assertEquals(2, response.getQuantity());
        assertEquals("Jl. Margonda No.1", response.getShippingAddress());
        assertEquals(OrderStatus.PENDING, response.getOrderStatus());
        assertEquals(now, response.getCreatedAt());
        assertNull(response.getUpdatedAt());
        assertNull(response.getJastiperRating());
        assertNull(response.getProductRating());
        assertNull(response.getCancellationReason());
    }

    @Test
    void testBuilderWithRatingFields() {
        OrderResponse response = OrderResponse.builder()
                .orderId("order-001")
                .orderStatus(OrderStatus.COMPLETED)
                .jastiperRating(5)
                .productRating(4)
                .build();

        assertEquals(5, response.getJastiperRating());
        assertEquals(4, response.getProductRating());
        assertEquals(OrderStatus.COMPLETED, response.getOrderStatus());
    }

    @Test
    void testBuilderWithCancellationReason() {
        OrderResponse response = OrderResponse.builder()
                .orderId("order-001")
                .orderStatus(OrderStatus.CANCELLED)
                .cancellationReason("Out of stock")
                .build();

        assertEquals(OrderStatus.CANCELLED, response.getOrderStatus());
        assertEquals("Out of stock", response.getCancellationReason());
    }
}

