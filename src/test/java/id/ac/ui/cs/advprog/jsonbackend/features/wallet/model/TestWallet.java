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

    @Test
    void testBuilderSettersEqualsHashCodeAndToString() {
        UUID walletId = UUID.randomUUID();
        Wallet builtWallet = Wallet.builder()
                .id(walletId)
                .userId(USER_ID)
                .balance(new BigDecimal("250"))
                .version(1L)
                .build();
        Wallet sameWallet = new Wallet(walletId, USER_ID, new BigDecimal("250"), 1L);

        assertEquals(walletId, builtWallet.getId());
        assertEquals(USER_ID, builtWallet.getUserId());
        assertEquals(new BigDecimal("250"), builtWallet.getBalance());
        assertEquals(1L, builtWallet.getVersion());
        assertEquals(builtWallet, sameWallet);
        assertEquals(builtWallet.hashCode(), sameWallet.hashCode());
        assertTrue(builtWallet.toString().contains("250"));
    }
}
