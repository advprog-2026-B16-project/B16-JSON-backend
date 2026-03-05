package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancelledStateTest {

    private final CancelledState cancelledState = new CancelledState();

    @Test
    void testGetStatusReturnsCancelled() {
        assertEquals(OrderStatus.CANCELLED, cancelledState.getStatus());
    }

    @Test
    void testValidateTransitionThrowsForAnyStatus() {
        for (OrderStatus status : OrderStatus.values()) {
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> cancelledState.validateTransition(status));
            assertEquals("Cancelled order cannot change state", ex.getMessage());
        }
    }
}

