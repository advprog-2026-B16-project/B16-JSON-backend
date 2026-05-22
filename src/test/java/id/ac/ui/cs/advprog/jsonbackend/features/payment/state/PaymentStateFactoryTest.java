package id.ac.ui.cs.advprog.jsonbackend.features.payment.state;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentStateFactoryTest {

    @Test
    void pendingPaymentStateShouldAllowValidTransitions() {
        PaymentState state = PaymentStateFactory.getState(PaymentStatus.PENDING);

        assertEquals(PaymentStatus.PENDING, state.getStatus());
        assertDoesNotThrow(() -> state.validateTransition(PaymentStatus.SUCCESS));
        assertDoesNotThrow(() -> state.validateTransition(PaymentStatus.EXPIRED));
        assertDoesNotThrow(() -> state.validateTransition(PaymentStatus.FAILED));
        assertDoesNotThrow(() -> state.validateTransition(PaymentStatus.CANCELLED));
    }

    @Test
    void terminalPaymentStateShouldRejectTransitions() {
        PaymentState state = PaymentStateFactory.getState(PaymentStatus.SUCCESS);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> state.validateTransition(PaymentStatus.CANCELLED));

        assertEquals("SUCCESS payment cannot transition to another status", exception.getMessage());
    }

    @Test
    void factoryShouldRejectNullStatus() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PaymentStateFactory.getState(null));

        assertEquals("Unknown payment status: null", exception.getMessage());
    }
}
