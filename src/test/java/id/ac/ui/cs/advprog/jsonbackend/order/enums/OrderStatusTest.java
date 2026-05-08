package id.ac.ui.cs.advprog.jsonbackend.order.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void testPendingValue() {
        assertEquals("PENDING", OrderStatus.PENDING.getValue());
    }

    @Test
    void testPaidValue() {
        assertEquals("PAID", OrderStatus.PAID.getValue());
    }

    @Test
    void testPurchasedValue() {
        assertEquals("PURCHASED", OrderStatus.PURCHASED.getValue());
    }

    @Test
    void testShippedValue() {
        assertEquals("SHIPPED", OrderStatus.SHIPPED.getValue());
    }

    @Test
    void testCompletedValue() {
        assertEquals("COMPLETED", OrderStatus.COMPLETED.getValue());
    }

    @Test
    void testCancelledValue() {
        assertEquals("CANCELLED", OrderStatus.CANCELLED.getValue());
    }

    @Test
    void testEnumContainsSixValues() {
        assertEquals(6, OrderStatus.values().length);
    }

    @Test
    void testValueOfPending() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
    }

    @Test
    void testValueOfCompleted() {
        assertEquals(OrderStatus.COMPLETED, OrderStatus.valueOf("COMPLETED"));
    }
}

