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
import java.util.UUID;

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
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100");

        Wallet wallet = new Wallet(userId);

        Transaction trx = new Transaction(
                walletId,
                userId,
                TransactionType.TOP_UP,
                amount,
                "Top Up Request"
        );

        when(walletService.findWallet(userId.toString())).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up Request"))
                .thenReturn(trx);

        Transaction result = walletTransactionService.requestTopUp(userId.toString(), amount);

        assertEquals(trx, result);
        verify(transactionService).createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up Request");
        verify(walletService, never()).credit(any(), any());
    }

    @Test
    void testConfirmTopUpSuccess() {
        UUID trxId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Transaction trx = mock(Transaction.class);

        when(trx.getStatus()).thenReturn(TransactionStatus.PENDING);
        when(trx.getUserId()).thenReturn(userId);
        when(trx.getAmount()).thenReturn(new BigDecimal("100"));

        when(transactionService.getTransactionById(trxId.toString())).thenReturn(trx);

        walletTransactionService.confirmTopUp(trxId.toString());

        verify(walletService).credit(userId.toString(), new BigDecimal("100"));
        verify(transactionService).markSuccess(trxId.toString());
    }

    @Test
    void testConfirmTopUpAlreadySuccess() {
        UUID trxId = UUID.randomUUID();

        Transaction trx = mock(Transaction.class);
        when(trx.getStatus()).thenReturn(TransactionStatus.SUCCESS);

        when(transactionService.getTransactionById(trxId.toString())).thenReturn(trx);

        walletTransactionService.confirmTopUp(trxId.toString());

        verify(walletService, never()).credit(any(), any());
        verify(transactionService, never()).markSuccess(any());
    }

    @Test
    void testRequestWithdrawSuccess() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50");

        Wallet wallet = mock(Wallet.class);
        when(wallet.getBalance()).thenReturn(new BigDecimal("100"));

        Transaction trx = new Transaction(
                walletId,
                userId,
                TransactionType.WITHDRAW,
                amount,
                "Withdraw Request"
        );
        UUID trxId = UUID.randomUUID();
        trx.setId(trxId);

        when(walletService.findWallet(userId.toString())).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.WITHDRAW, amount, "Withdraw Request"))
                .thenReturn(trx);

        walletTransactionService.requestWithdraw(userId.toString(), amount);

        verify(walletService).debit(userId.toString(), amount);
        verify(transactionService).markSuccess(trxId.toString());
    }

    @Test
    void testRequestWithdrawInsufficientBalance() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("200");

        Wallet wallet = mock(Wallet.class);
        when(wallet.getBalance()).thenReturn(new BigDecimal("100"));

        when(walletService.findWallet(userId.toString())).thenReturn(wallet);

        assertThrows(InsufficientBalanceException.class,
                () -> walletTransactionService.requestWithdraw(userId.toString(), amount));

        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void testGetTransactionHistory() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();

        Transaction trx = new Transaction(
                walletId,
                userId,
                TransactionType.TOP_UP,
                new BigDecimal("100"),
                "Top Up"
        );

        when(transactionService.getUserTransactions(userId.toString())).thenReturn(List.of(trx));

        List<Transaction> result = walletTransactionService.getTransactionHistory(userId.toString());

        assertEquals(1, result.size());
        verify(transactionService).getUserTransactions(userId.toString());
    }

    @Test
    void testRequestPaymentSuccess() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID trxId = UUID.randomUUID();

        String orderId = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal("50");

        Wallet wallet = mock(Wallet.class);
        when(wallet.getBalance()).thenReturn(new BigDecimal("100"));

        Transaction trx = new Transaction(
                walletId,
                userId,
                TransactionType.PAYMENT,
                amount,
                "Payment for order " + orderId
        );
        trx.setId(trxId);

        when(walletService.findWallet(userId.toString())).thenReturn(wallet);
        when(transactionService.createTransaction(
                wallet,
                TransactionType.PAYMENT,
                amount,
                "Payment for order " + orderId
        )).thenReturn(trx);

        Transaction result = walletTransactionService.requestPayment(
                userId.toString(),
                orderId,
                amount
        );

        assertEquals(trx, result);
        assertEquals(orderId, result.getOrderId().toString());

        verify(walletService).debit(userId.toString(), amount);
        verify(transactionService).markSuccess(trxId.toString());
    }

    @Test
    void testRequestPaymentInsufficientBalance() {
        UUID userId = UUID.randomUUID();
        String orderId = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal("200");

        Wallet wallet = mock(Wallet.class);
        when(wallet.getBalance()).thenReturn(new BigDecimal("100"));

        when(walletService.findWallet(userId.toString())).thenReturn(wallet);

        assertThrows(InsufficientBalanceException.class,
                () -> walletTransactionService.requestPayment(
                        userId.toString(),
                        orderId,
                        amount
                ));

        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
        verify(walletService, never()).debit(any(), any());
    }

    @Test
    void testRequestPaymentInvalidOrderId() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50");

        assertThrows(IllegalArgumentException.class,
                () -> walletTransactionService.requestPayment(
                        userId.toString(),
                        "",
                        amount
                ));

        verifyNoInteractions(walletService);
        verifyNoInteractions(transactionService);
    }
}