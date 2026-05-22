package id.ac.ui.cs.advprog.jsonbackend.features.wallet.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TestWallet {

    private Wallet wallet;
    private UUID USER_ID;

    @BeforeEach
    void setUp() {
        USER_ID = UUID.randomUUID();
        wallet = new Wallet(USER_ID);
    }

    @Test
    void testWalletCreation() {
        assertEquals(USER_ID, wallet.getUserId());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testCreditSuccess() {
        wallet.credit(new BigDecimal("100"));

        assertEquals(new BigDecimal("100"), wallet.getBalance());
    }

    @Test
    void testCreditMultipleTimes() {
        wallet.credit(new BigDecimal("100"));
        wallet.credit(new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), wallet.getBalance());
    }

    @Test
    void testDebitSuccess() {
        wallet.credit(new BigDecimal("200"));
        wallet.debit(new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), wallet.getBalance());
    }

    @Test
    void testDebitExactBalance() {
        wallet.credit(new BigDecimal("100"));
        wallet.debit(new BigDecimal("100"));

        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testDebitInsufficientBalance() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            wallet.debit(new BigDecimal("100"));
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void testCreditZero() {
        wallet.credit(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testDebitZero() {
        wallet.credit(new BigDecimal("100"));
        wallet.debit(BigDecimal.ZERO);

        assertEquals(new BigDecimal("100"), wallet.getBalance());
    }
}