package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PendingStateTest {

    private final PendingState pendingState = new PendingState();

    @Test
    void testGetStatusReturnsPending() {
        assertEquals(OrderStatus.PENDING, pendingState.getStatus());
    }

    @Test
    void testValidTransitionToPaid() {
        assertDoesNotThrow(() -> pendingState.validateTransition(OrderStatus.PAID));
    }

    @Test
    void testValidTransitionToCancelled() {
        assertDoesNotThrow(() -> pendingState.validateTransition(OrderStatus.CANCELLED));
    }

    @Test
    void testInvalidTransitionToPurchased() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> pendingState.validateTransition(OrderStatus.PURCHASED));
        assertEquals("PENDING can only transition to PAID or CANCELLED", ex.getMessage());
    }

    @Test
    void testInvalidTransitionToShipped() {
        assertThrows(IllegalStateException.class,
                () -> pendingState.validateTransition(OrderStatus.SHIPPED));
    }

    @Test
    void testInvalidTransitionToCompleted() {
        assertThrows(IllegalStateException.class,
                () -> pendingState.validateTransition(OrderStatus.COMPLETED));
    }
}

