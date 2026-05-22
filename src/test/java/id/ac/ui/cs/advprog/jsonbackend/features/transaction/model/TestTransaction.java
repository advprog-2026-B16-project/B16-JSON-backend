package id.ac.ui.cs.advprog.jsonbackend.features.transaction.model;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TestTransaction {

    private Transaction transaction;
    private static final UUID WALLET_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final TransactionType TYPE = TransactionType.TOP_UP;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.0);
    private static final String DESCRIPTION = "Top up transaction";

    @BeforeEach
    void setUp() {
        transaction = new Transaction(WALLET_ID, USER_ID, TYPE, AMOUNT, DESCRIPTION);
    }

    @Test
    void testTransactionCreation() {
        assertEquals(WALLET_ID, transaction.getWalletId());
        assertEquals(TYPE, transaction.getType());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals(DESCRIPTION, transaction.getDescription());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
        assertNotNull(transaction.getCreatedAt());
    }

    @Test
    void testMarkSuccess() {
        transaction.markSuccess();
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    void testMarkFailed() {
        transaction.markFailed();
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
    }

    @Test
    void testSettersEqualsHashCodeAndToString() {
        UUID transactionId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Transaction other = new Transaction(WALLET_ID, USER_ID, TYPE, AMOUNT, DESCRIPTION);

        transaction.setId(transactionId);
        transaction.setOrderId(orderId);
        other.setId(transactionId);
        other.setOrderId(orderId);

        assertEquals(transactionId, transaction.getId());
        assertEquals(USER_ID, transaction.getUserId());
        assertEquals(orderId, transaction.getOrderId());
        assertEquals(transaction, other);
        assertEquals(transaction.hashCode(), other.hashCode());
        assertTrue(transaction.toString().contains("Top up transaction"));
    }
}
