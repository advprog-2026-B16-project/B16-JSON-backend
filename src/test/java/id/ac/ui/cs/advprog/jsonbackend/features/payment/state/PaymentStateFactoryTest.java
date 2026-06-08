package id.ac.ui.cs.advprog.jsonbackend.features.payment.state;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void factoryShouldReturnEveryTerminalPaymentState() {
        assertEquals(PaymentStatus.EXPIRED, PaymentStateFactory.getState(PaymentStatus.EXPIRED).getStatus());
        assertEquals(PaymentStatus.FAILED, PaymentStateFactory.getState(PaymentStatus.FAILED).getStatus());
        assertEquals(PaymentStatus.CANCELLED, PaymentStateFactory.getState(PaymentStatus.CANCELLED).getStatus());
    }

    @Test
    void factoryShouldRejectNullStatus() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PaymentStateFactory.getState(null));

        assertEquals("Unknown payment status: null", exception.getMessage());
    }

    @Test
    void factoryConstructorShouldRejectInstantiation() throws Exception {
        Constructor<PaymentStateFactory> constructor = PaymentStateFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void pendingPaymentStateShouldRejectInvalidTransition() {
        PaymentState state = PaymentStateFactory.getState(PaymentStatus.PENDING);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> state.validateTransition(PaymentStatus.PENDING));

        assertEquals("PENDING payment can only transition to SUCCESS, EXPIRED, FAILED, or CANCELLED",
                exception.getMessage());
    }
}
