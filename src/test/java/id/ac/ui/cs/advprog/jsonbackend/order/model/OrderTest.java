package id.ac.ui.cs.advprog.jsonbackend.order.model;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;


    @BeforeEach
    void setUp() {
        UUID orderId = UUID.randomUUID();
        order = new Order(
                orderId,
                "product-001",
                UUID.randomUUID(),
                UUID.randomUUID(),
                2,
                "Jl. Margonda No.1"
        );
    }

    @Test
    void testOrderCreationSetsDefaultStatusToPending() {
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
    }

    @Test
    void testOrderCreationSetsAllFields() {
        assertEquals("order-001", order.getOrderId());
        assertEquals("product-001", order.getProductId());
        assertEquals("titipers-001", order.getTitipersId());
        assertEquals("jastiper-001", order.getJastiperId());
        assertEquals(2, order.getQuantity());
        assertEquals("Jl. Margonda No.1", order.getShippingAddress());
    }

    @Test
    void testOrderCreationSetsCreatedAt() {
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void testOrderCreationUpdatedAtIsNull() {
        assertNull(order.getUpdatedAt());
    }

    @Test
    void testOrderCreationRatingFieldsAreNull() {
        assertNull(order.getJastiperRating());
        assertNull(order.getProductRating());
    }

    @Test
    void testOrderCreationCancellationReasonIsNull() {
        assertNull(order.getCancellationReason());
    }

    @Test
    void testUpdateStatusChangesOrderStatus() {
        order.updateStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }

    @Test
    void testUpdateStatusSetsUpdatedAt() {
        order.updateStatus(OrderStatus.PAID);
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void testCancelSetsStatusToCancelled() {
        order.cancel("Out of stock");
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
    }

    @Test
    void testCancelSetsCancellationReason() {
        order.cancel("Out of stock");
        assertEquals("Out of stock", order.getCancellationReason());
    }

    @Test
    void testSubmitRatingSucceedsWhenCompleted() {
        order.updateStatus(OrderStatus.PAID);
        order.updateStatus(OrderStatus.PURCHASED);
        order.updateStatus(OrderStatus.SHIPPED);
        order.updateStatus(OrderStatus.COMPLETED);
        order.submitRating(5, 4);

        assertEquals(5, order.getJastiperRating());
        assertEquals(4, order.getProductRating());
    }

    @Test
    void testSubmitRatingThrowsWhenNotCompleted() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> order.submitRating(5, 4));

        assertEquals("Rating can only be submitted for COMPLETED orders", ex.getMessage());
    }

    @Test
    void testSubmitRatingThrowsWhenStatusIsPaid() {
        order.updateStatus(OrderStatus.PAID);
        assertThrows(IllegalStateException.class, () -> order.submitRating(5, 4));
    }

    @Test
    void testSetterChangesOrderId() {
        UUID newOrderId = UUID.randomUUID();
        order.setOrderId(newOrderId);
        assertEquals("order-999", order.getOrderId());
    }

    @Test
    void testSetterChangesQuantity() {
        order.setQuantity(10);
        assertEquals(10, order.getQuantity());
    }
}

