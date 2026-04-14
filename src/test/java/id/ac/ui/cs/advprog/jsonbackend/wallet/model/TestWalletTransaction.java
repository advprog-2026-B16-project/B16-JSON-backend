package id.ac.ui.cs.advprog.jsonbackend.wallet.model;

import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TestWalletTransaction {

    private WalletTransaction transaction;
    private final String WALLET_ID = "wallet-abc";
    private final TransactionType TYPE = TransactionType.TOP_UP;
    private final BigDecimal AMOUNT = BigDecimal.valueOf(250);
    private final String DESCRIPTION = "Top up initial balance";

    @BeforeEach
    void setUp() {
        transaction = new WalletTransaction(WALLET_ID, TYPE, AMOUNT, DESCRIPTION);
    }

    @Test
    void testWalletTransactionConstructor_shouldInitializeFieldsCorrectly() {
        assertEquals(WALLET_ID, transaction.getWalletId());
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