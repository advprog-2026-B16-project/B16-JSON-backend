package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaidStateTest {

    private final PaidState paidState = new PaidState();

    @Test
    void testGetStatusReturnsPaid() {
        assertEquals(OrderStatus.PAID, paidState.getStatus());
    }

    @Test
    void testValidTransitionToPurchased() {
        assertDoesNotThrow(() -> paidState.validateTransition(OrderStatus.PURCHASED));
    }

    @Test
    void testValidTransitionToCancelled() {
        assertDoesNotThrow(() -> paidState.validateTransition(OrderStatus.CANCELLED));
    }

    @Test
    void testInvalidTransitionToPending() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> paidState.validateTransition(OrderStatus.PENDING));
        assertEquals("PAID can only transition to PURCHASED or CANCELLED", ex.getMessage());
    }

    @Test
    void testInvalidTransitionToShipped() {
        assertThrows(IllegalStateException.class,
                () -> paidState.validateTransition(OrderStatus.SHIPPED));
    }

    @Test
    void testInvalidTransitionToCompleted() {
        assertThrows(IllegalStateException.class,
                () -> paidState.validateTransition(OrderStatus.COMPLETED));
    }
}

