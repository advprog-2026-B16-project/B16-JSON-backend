package id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletTransactionStrategyTest {

    @Test
    void paymentStrategyShouldDebitWalletAndAttachOrderId() {
        WalletService walletService = mock(WalletService.class);
        TransactionService transactionService = mock(TransactionService.class);
        PaymentTransactionStrategy strategy = new PaymentTransactionStrategy(walletService, transactionService);
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50000");
        Wallet wallet = new Wallet(userId);
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(new BigDecimal("100000"));
        Transaction transaction = new Transaction(wallet.getId(), userId, TransactionType.PAYMENT, amount, "Payment for order " + orderId);
        transaction.setId(UUID.randomUUID());

        when(walletService.findWalletForUpdate(userId.toString())).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.PAYMENT, amount, "Payment for order " + orderId))
                .thenReturn(transaction);

        Transaction result = strategy.execute(userId.toString(), orderId.toString(), amount);

        assertEquals(TransactionType.PAYMENT, strategy.getType());
        assertEquals(orderId, result.getOrderId());
        verify(walletService).debit(userId.toString(), amount);
        verify(transactionService).markSuccess(transaction.getId().toString());
    }

    @Test
    void topUpStrategyShouldCreatePendingTopUpWithoutCreditingWallet() {
        WalletService walletService = mock(WalletService.class);
        TransactionService transactionService = mock(TransactionService.class);
        TopUpTransactionStrategy strategy = new TopUpTransactionStrategy(walletService, transactionService);
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50000");
        Wallet wallet = new Wallet(userId);
        wallet.setId(UUID.randomUUID());
        Transaction transaction = new Transaction(wallet.getId(), userId, TransactionType.TOP_UP, amount, "Top Up Request");

        when(walletService.findWallet(userId.toString())).thenReturn(wallet);
        when(transactionService.createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up Request"))
                .thenReturn(transaction);

        Transaction result = strategy.execute(userId.toString(), null, amount);

        assertEquals(TransactionType.TOP_UP, strategy.getType());
        assertEquals(transaction, result);
        verify(transactionService).createTransaction(wallet, TransactionType.TOP_UP, amount, "Top Up Request");
    }
}
