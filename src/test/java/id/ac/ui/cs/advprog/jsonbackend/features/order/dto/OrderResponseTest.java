package id.ac.ui.cs.advprog.jsonbackend.features.order.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderResponseTest {

    @Test
    void testBuilderCreatesOrderResponseWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        UUID orderId = UUID.randomUUID();
        UUID titipersId = UUID.randomUUID();
        UUID jastiperId = UUID.randomUUID();
        BigDecimal totalAmount = new BigDecimal("125000.00");

        OrderResponse response = OrderResponse.builder()
                .orderId(orderId)
                .productId("prod-001")
                .titipersId(titipersId)
                .jastiperId(jastiperId)
                .quantity(2)
                .shippingAddress("Jl. Margonda No.1")
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .createdAt(now)
                .updatedAt(null)
                .jastiperRating(null)
                .productRating(null)
                .cancellationReason(null)
                .build();

        assertEquals(orderId, response.getOrderId());
        assertEquals("prod-001", response.getProductId());
        assertEquals(titipersId, response.getTitipersId());
        assertEquals(jastiperId, response.getJastiperId());
        assertEquals(2, response.getQuantity());
        assertEquals("Jl. Margonda No.1", response.getShippingAddress());
        assertEquals(OrderStatus.PENDING, response.getOrderStatus());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(now, response.getCreatedAt());
        assertNull(response.getUpdatedAt());
        assertNull(response.getJastiperRating());
        assertNull(response.getProductRating());
        assertNull(response.getCancellationReason());
    }

    @Test
    void testBuilderWithMinimalFieldsKeepsOptionalValuesNullOrDefault() {
        OrderResponse response = OrderResponse.builder()
                .orderId(UUID.randomUUID())
                .orderStatus(OrderStatus.COMPLETED)
                .build();

        assertEquals(0, response.getQuantity());
        assertNull(response.getProductId());
        assertNull(response.getTotalAmount());
        assertNull(response.getShippingAddress());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
        assertNull(response.getTitipersId());
        assertNull(response.getJastiperId());
        assertNull(response.getJastiperRating());
        assertNull(response.getProductRating());
        assertNull(response.getCancellationReason());
        assertEquals(OrderStatus.COMPLETED, response.getOrderStatus());
    }

    @Test
    void testBuilderWithRatingFields() {
        OrderResponse response = OrderResponse.builder()
                .orderId(UUID.randomUUID())
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
                .orderId(UUID.randomUUID())
                .orderStatus(OrderStatus.CANCELLED)
                .cancellationReason("Out of stock")
                .build();

        assertEquals(OrderStatus.CANCELLED, response.getOrderStatus());
        assertEquals("Out of stock", response.getCancellationReason());
    }
}
