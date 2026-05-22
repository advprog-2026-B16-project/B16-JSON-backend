package id.ac.ui.cs.advprog.jsonbackend.features.payment.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentResponseTest {

    @Test
    void constructorShouldMapAllPaymentFields() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        Payment payment = new Payment(orderId, userId, walletId, "PAY-REF", new BigDecimal("125000"), expiresAt);
        UUID paymentId = UUID.randomUUID();
        payment.setId(paymentId);
        payment.markSuccess(transactionId);

        PaymentResponse response = new PaymentResponse(payment);

        assertEquals(paymentId, response.getId());
        assertEquals(orderId, response.getOrderId());
        assertEquals(transactionId, response.getTransactionId());
        assertEquals("PAY-REF", response.getReferenceCode());
        assertEquals(new BigDecimal("125000"), response.getAmount());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(expiresAt, response.getExpiresAt());
        assertEquals(payment.getPaidAt(), response.getPaidAt());
    }
}
