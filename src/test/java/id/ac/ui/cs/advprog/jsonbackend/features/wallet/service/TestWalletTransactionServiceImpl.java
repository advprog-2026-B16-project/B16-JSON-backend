package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testRequestTopUp() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("100");
        Wallet wallet = new Wallet(userId);

        Transaction trx = new Transaction(userId, wallet.getId(), TransactionType.TOP_UP, amount, "Top Up Request");

        when(walletService.findWallet(userId)).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up Request"))
                .thenReturn(trx);

        Transaction result = walletTransactionService.requestTopUp(userId, amount);

        assertEquals(trx, result);
        verify(transactionService).createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up Request");

        verify(walletService, never()).credit(any(), any());
    }

    @Test
    void testConfirmTopUpSuccess() {
        String trxId = "tx-1";

        Transaction trx = mock(Transaction.class);
        when(trx.getStatus()).thenReturn(TransactionStatus.PENDING);
        when(trx.getUserId()).thenReturn("user1");
        when(trx.getAmount()).thenReturn(new BigDecimal("100"));

        when(transactionService.getTransactionById(trxId)).thenReturn(trx);

        walletTransactionService.confirmTopUp(trxId);

        verify(walletService).credit("user1", new BigDecimal("100"));
        verify(transactionService).markSuccess(trxId);
    }

    @Test
    void testConfirmTopUpAlreadySuccess() {
        String trxId = "tx-1";

        Transaction trx = mock(Transaction.class);
        when(trx.getStatus()).thenReturn(TransactionStatus.SUCCESS);

        when(transactionService.getTransactionById(trxId)).thenReturn(trx);

        walletTransactionService.confirmTopUp(trxId);

        verify(walletService, never()).credit(any(), any());
        verify(transactionService, never()).markSuccess(any());
    }

    @Test
    void testRequestWithdrawSuccess() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("50");

        Wallet wallet = mock(Wallet.class);
        when(wallet.getBalance()).thenReturn(new BigDecimal("100"));

        Transaction trx = new Transaction(userId, "wallet1", TransactionType.WITHDRAW, amount, "Withdraw Request");
        trx.setId("tx-2");

        when(walletService.findWallet(userId)).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.WITHDRAW, amount, "Withdraw Request"))
                .thenReturn(trx);

        walletTransactionService.requestWithdraw(userId, amount);

        verify(walletService).debit(userId, amount);
        verify(transactionService).markSuccess("tx-2");
    }

    @Test
    void testRequestWithdrawInsufficientBalance() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("200");

        Wallet wallet = mock(Wallet.class);
        when(wallet.getBalance()).thenReturn(new BigDecimal("100"));

        when(walletService.findWallet(userId)).thenReturn(wallet);

        assertThrows(InsufficientBalanceException.class,
                () -> walletTransactionService.requestWithdraw(userId, amount));

        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void testGetTransactionHistory() {
        String userId = "user1";

        Transaction trx = new Transaction(userId, "wallet1", TransactionType.TOP_UP, new BigDecimal("100"), "Top Up");

        when(transactionService.getUserTransactions(userId)).thenReturn(List.of(trx));

        List<Transaction> result = walletTransactionService.getTransactionHistory(userId);

        assertEquals(1, result.size());
        verify(transactionService).getUserTransactions(userId);
    }
}