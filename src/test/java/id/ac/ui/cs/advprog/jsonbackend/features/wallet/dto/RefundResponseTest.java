package id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefundResponseTest {

    @Test
    void constructorShouldMapAllRefundFields() {
        UUID walletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID originalTransactionId = UUID.randomUUID();
        UUID refundTransactionId = UUID.randomUUID();
        Transaction originalTransaction = new Transaction(walletId, userId, TransactionType.PAYMENT, new BigDecimal("125000"), "Payment");
        originalTransaction.setId(originalTransactionId);
        originalTransaction.setOrderId(orderId);
        Refund refund = new Refund(originalTransaction, "Barang rusak");
        UUID refundId = UUID.randomUUID();
        refund.setId(refundId);
        refund.markSuccess(refundTransactionId);

        RefundResponse response = new RefundResponse(refund);

        assertEquals(refundId, response.getId());
        assertEquals(originalTransactionId, response.getOriginalTransactionId());
        assertEquals(refundTransactionId, response.getRefundTransactionId());
        assertEquals(orderId, response.getOrderId());
        assertEquals(new BigDecimal("125000"), response.getAmount());
        assertEquals("Barang rusak", response.getReason());
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());
    }
}
