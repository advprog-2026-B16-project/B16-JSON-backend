package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PurchasedStateTest {

    private final PurchasedState purchasedState = new PurchasedState();

    @Test
    void testGetStatusReturnsPurchased() {
        assertEquals(OrderStatus.PURCHASED, purchasedState.getStatus());
    }

    @Test
    void testValidTransitionToShipped() {
        assertDoesNotThrow(() -> purchasedState.validateTransition(OrderStatus.SHIPPED));
    }

    @Test
    void testValidTransitionToCancelled() {
        assertDoesNotThrow(() -> purchasedState.validateTransition(OrderStatus.CANCELLED));
    }

    @Test
    void testInvalidTransitionToPending() {
        assertThrows(IllegalStateException.class,
                () -> purchasedState.validateTransition(OrderStatus.PENDING));
    }

    @Test
    void testInvalidTransitionToPaid() {
        assertThrows(IllegalStateException.class,
                () -> purchasedState.validateTransition(OrderStatus.PAID));
    }

    @Test
    void testInvalidTransitionToCompleted() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> purchasedState.validateTransition(OrderStatus.COMPLETED));
        assertEquals("PURCHASED can only transition to SHIPPED or CANCELLED", ex.getMessage());
    }
}

