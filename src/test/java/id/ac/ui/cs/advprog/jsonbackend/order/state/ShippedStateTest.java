package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippedStateTest {

    private final ShippedState shippedState = new ShippedState();

    @Test
    void testGetStatusReturnsShipped() {
        assertEquals(OrderStatus.SHIPPED, shippedState.getStatus());
    }

    @Test
    void testValidTransitionToCompleted() {
        assertDoesNotThrow(() -> shippedState.validateTransition(OrderStatus.COMPLETED));
    }

    @Test
    void testInvalidTransitionToPending() {
        assertThrows(IllegalStateException.class,
                () -> shippedState.validateTransition(OrderStatus.PENDING));
    }

    @Test
    void testInvalidTransitionToPaid() {
        assertThrows(IllegalStateException.class,
                () -> shippedState.validateTransition(OrderStatus.PAID));
    }

    @Test
    void testInvalidTransitionToPurchased() {
        assertThrows(IllegalStateException.class,
                () -> shippedState.validateTransition(OrderStatus.PURCHASED));
    }

    @Test
    void testInvalidTransitionToCancelledThrowsWithMessage() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> shippedState.validateTransition(OrderStatus.CANCELLED));
        assertEquals("SHIPPED can only transition to COMPLETED", ex.getMessage());
    }
}

