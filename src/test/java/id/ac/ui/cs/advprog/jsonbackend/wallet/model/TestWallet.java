package id.ac.ui.cs.advprog.jsonbackend.wallet.model;

import id.ac.ui.cs.advprog.jsonbackend.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.wallet.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TestWallet {

    private Wallet wallet;
    private final String USER_ID = "user-123";

    @BeforeEach
    void setUp() {
        wallet = new Wallet(USER_ID);
    }

    @Test
    void testWalletConstructor_shouldSetUserIdAndZeroBalance() {
        assertEquals(USER_ID, wallet.getUserId());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testCreditSuccess_shouldIncreaseBalance() {
        BigDecimal creditAmount = BigDecimal.valueOf(100.50);
        BigDecimal expectedBalance = BigDecimal.valueOf(100.50);

        wallet.credit(creditAmount);
        assertEquals(expectedBalance, wallet.getBalance());
    }

    @Test
    void testCreditWithNullAmount_shouldThrowException() {
        assertThrows(InvalidAmountException.class, () -> wallet.credit(null));
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testCreditWithZeroAmount_shouldThrowException() {
        assertThrows(InvalidAmountException.class, () -> wallet.credit(BigDecimal.ZERO));
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testCreditWithNegativeAmount_shouldThrowException() {
        assertThrows(InvalidAmountException.class, () -> wallet.credit(BigDecimal.valueOf(-50)));
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }


    @Test
    void testDebitSuccess_shouldDecreaseBalance() {
        wallet.credit(BigDecimal.valueOf(200));
        BigDecimal debitAmount = BigDecimal.valueOf(50.25);
        BigDecimal expectedBalance = BigDecimal.valueOf(149.75);

        wallet.debit(debitAmount);

        assertEquals(expectedBalance, wallet.getBalance());
    }

    @Test
    void testDebitWithNullAmount_shouldThrowException() {
        assertThrows(InvalidAmountException.class, () -> wallet.debit(null));
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testDebitWithZeroAmount_shouldThrowException() {
        assertThrows(InvalidAmountException.class, () -> wallet.debit(BigDecimal.ZERO));
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testDebitWithNegativeAmount_shouldThrowException() {
        assertThrows(InvalidAmountException.class, () -> wallet.debit(BigDecimal.valueOf(-10)));
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }

    @Test
    void testDebitInsufficientBalance_shouldThrowException() {
        wallet.credit(BigDecimal.valueOf(100));

        assertThrows(InsufficientBalanceException.class, () -> wallet.debit(BigDecimal.valueOf(150)));
        assertEquals(BigDecimal.valueOf(100), wallet.getBalance());
    }
}