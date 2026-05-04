package id.ac.ui.cs.advprog.jsonbackend.features.wallet.model;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TestTransaction {

    private Transaction transaction;
    private final String WALLET_ID = "wallet-abc";
    private final String USER_ID = "user1";
    private final TransactionType TYPE = TransactionType.TOP_UP;
    private final BigDecimal AMOUNT = BigDecimal.valueOf(250);
    private final String DESCRIPTION = "Top up initial balance";

    @BeforeEach
    void setUp() {
        transaction = new Transaction(WALLET_ID, USER_ID, TYPE, AMOUNT, DESCRIPTION);
    }

    @Test
    void testTransactionConstructor_shouldInitializeFieldsCorrectly() {
        assertEquals(WALLET_ID, transaction.getWalletId());
        assertEquals(USER_ID, transaction.getUserId());
        assertEquals(TYPE, transaction.getType());
        assertEquals(AMOUNT, transaction.getAmount());
        assertEquals(DESCRIPTION, transaction.getDescription());

        assertEquals(TransactionStatus.PENDING, transaction.getStatus());

        assertNotNull(transaction.getCreatedAt());
        assertTrue(transaction.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testMarkSuccess_shouldChangeStatusToSuccess() {
        transaction.markSuccess();

        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    void testMarkFailed_shouldChangeStatusToFailed() {
        transaction.markFailed();

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
    }
}