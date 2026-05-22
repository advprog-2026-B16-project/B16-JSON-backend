package id.ac.ui.cs.advprog.jsonbackend.features.payment.model;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void constructorSetsPendingPaymentFields() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        Payment payment = new Payment(orderId, userId, walletId, "PAY-REF", new BigDecimal("10000"), expiresAt);

        assertEquals(orderId, payment.getOrderId());
        assertEquals(userId, payment.getUserId());
        assertEquals(walletId, payment.getWalletId());
        assertEquals("PAY-REF", payment.getReferenceCode());
        assertEquals(new BigDecimal("10000"), payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(expiresAt, payment.getExpiresAt());
        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
    }

    @Test
    void isExpiredOnlyForPendingPaymentAtOrAfterExpiration() {
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "PAY-REF", BigDecimal.ONE, LocalDateTime.now());

        assertTrue(payment.isExpired(payment.getExpiresAt()));
        assertFalse(payment.isExpired(payment.getExpiresAt().minusNanos(1)));

        payment.markSuccess(UUID.randomUUID());

        assertFalse(payment.isExpired(payment.getExpiresAt().plusMinutes(1)));
    }

    @Test
    void markSuccessSetsTransactionAndPaidAt() {
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "PAY-REF", BigDecimal.ONE, LocalDateTime.now().plusMinutes(1));
        UUID transactionId = UUID.randomUUID();

        payment.markSuccess(transactionId);

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(transactionId, payment.getTransactionId());
        assertNotNull(payment.getPaidAt());
        assertEquals(payment.getPaidAt(), payment.getUpdatedAt());
    }

    @Test
    void pendingPaymentCanTransitionToExpiredOrFailed() {
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "PAY-REF", BigDecimal.ONE, LocalDateTime.now().plusMinutes(1));

        payment.markExpired();
        assertEquals(PaymentStatus.EXPIRED, payment.getStatus());

        Payment anotherPayment = new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "PAY-REF-2", BigDecimal.ONE, LocalDateTime.now().plusMinutes(1));
        anotherPayment.markFailed();
        assertEquals(PaymentStatus.FAILED, anotherPayment.getStatus());
    }

    @Test
    void terminalPaymentCannotTransitionAgain() {
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "PAY-REF", BigDecimal.ONE, LocalDateTime.now().plusMinutes(1));

        payment.markExpired();

        IllegalStateException exception = assertThrows(IllegalStateException.class, payment::markFailed);
        assertEquals("EXPIRED payment cannot transition to another status", exception.getMessage());
    }
}
