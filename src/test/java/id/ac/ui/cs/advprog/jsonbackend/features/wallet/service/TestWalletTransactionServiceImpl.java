package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InvalidAmountException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestWalletTransactionServiceImpl {

    private WalletService walletService;
    private TransactionService transactionService;
    private WalletTransactionServiceImpl walletTransactionService;

    @BeforeEach
    void setUp() {
        walletService = mock(WalletService.class);
        transactionService = mock(TransactionService.class);
        walletTransactionService = new WalletTransactionServiceImpl(walletService, transactionService);
    }

    @Test
    void testTopUp() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("100");
        Wallet wallet = new Wallet(userId);
        Transaction transaction = new Transaction(wallet.getId(), TransactionType.TOP_UP, amount, "Top Up");
        transaction.setId("tx-1");

        when(walletService.findWallet(userId)).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up"))
                .thenReturn(transaction);

        walletTransactionService.topUp(userId, amount);

        verify(walletService).credit(userId, amount);
        verify(transactionService).markSuccess("tx-1");
    }

    @Test
    void testWithdraw() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("50");
        Wallet wallet = new Wallet(userId);
        Transaction transaction = new Transaction(wallet.getId(), TransactionType.WITHDRAW, amount, "Withdraw");
        transaction.setId("tx-2");

        when(walletService.findWallet(userId)).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.WITHDRAW, amount, "Withdraw"))
                .thenReturn(transaction);

        walletTransactionService.withdraw(userId, amount);

        verify(walletService).debit(userId, amount);
        verify(transactionService).markSuccess("tx-2");
    }

    @Test
    void testGetTransactionHistory() {
        String userId = "user1";
        Transaction transaction = new Transaction("wallet1", TransactionType.TOP_UP, new BigDecimal("100"), "Top Up");

        when(transactionService.getUserTransactions(userId)).thenReturn(List.of(transaction));

        List<Transaction> history = walletTransactionService.getTransactionHistory(userId);

        assertEquals(1, history.size());
        verify(transactionService).getUserTransactions(userId);
    }

    @Test
    void testFailedTransactionIsMarkedFailed() {
        String userId = "user1";
        BigDecimal amount = BigDecimal.ZERO;
        Wallet wallet = new Wallet(userId);
        Transaction transaction = new Transaction(wallet.getId(), TransactionType.TOP_UP, amount, "Top Up");
        transaction.setId("tx-3");

        when(walletService.findWallet(userId)).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up"))
                .thenReturn(transaction);

        when(transactionService.createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up"))
                .thenReturn(transaction);

        org.mockito.Mockito.doThrow(new InvalidAmountException())
                .when(walletService).credit(userId, amount);

        assertThrows(InvalidAmountException.class, () -> walletTransactionService.topUp(userId, amount));
        verify(transactionService).markFailed("tx-3");
    }
}
