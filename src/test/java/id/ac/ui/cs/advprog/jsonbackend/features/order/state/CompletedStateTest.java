package id.ac.ui.cs.advprog.jsonbackend.features.order.state;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.state.CompletedState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompletedStateTest {

    private final CompletedState completedState = new CompletedState();

    @Test
    void testGetStatusReturnsCompleted() {
        assertEquals(OrderStatus.COMPLETED, completedState.getStatus());
    }

    @Test
    void testValidateTransitionAllowsDone() {
        assertDoesNotThrow(() -> completedState.validateTransition(OrderStatus.DONE));
    }

    @Test
    void testValidateTransitionRejectsNonDoneStatus() {
        for (OrderStatus status : OrderStatus.values()) {
            if (status == OrderStatus.DONE) {
                continue;
            }
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> completedState.validateTransition(status));
            assertEquals("COMPLETED can only transition to DONE", ex.getMessage());
        }
    }
}

