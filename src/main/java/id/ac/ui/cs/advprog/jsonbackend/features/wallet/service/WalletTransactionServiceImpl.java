package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public WalletTransactionServiceImpl(
            WalletService walletService,
            TransactionService transactionService
    ) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public Transaction requestTopUp(String userId, BigDecimal amount) {
        Wallet wallet = walletService.findWallet(userId);

        return transactionService.createTransaction(
                wallet,
                TransactionType.TOP_UP,
                amount,
                "Top Up Request"
        );
    }

    @Override
    public void confirmTopUp(String transactionId) {
        Transaction trx = transactionService.getTransactionById(transactionId);

        if (trx.getStatus() == TransactionStatus.SUCCESS) {
            return;
        }

        if (trx.getStatus() != TransactionStatus.PENDING) {
            throw new RuntimeException("Invalid transaction state");
        }

        walletService.credit(trx.getUserId().toString(), trx.getAmount());

        transactionService.markSuccess(transactionId);
    }

    @Override
    public void requestWithdraw(String userId, BigDecimal amount) {
        Wallet wallet = walletService.findWallet(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        Transaction trx = transactionService.createTransaction(
                wallet,
                TransactionType.WITHDRAW,
                amount,
                "Withdraw Request"
        );

        try {
            walletService.debit(userId, amount);
            transactionService.markSuccess(trx.getId().toString());

        } catch (Exception e) {
            transactionService.markFailed(trx.getId().toString());
            throw e;
        }
    }

    @Override
    public void refund(String userId, BigDecimal amount) {
        Wallet wallet = walletService.findWallet(userId);

        Transaction trx = transactionService.createTransaction(
                wallet,
                TransactionType.REFUND,
                amount,
                "Refund"
        );

        try {
            walletService.credit(userId, amount);
            transactionService.markSuccess(trx.getId().toString());
        } catch (Exception e) {
            transactionService.markFailed(trx.getId().toString());
            throw e;
        }
    }

    @Override
    public List<Transaction> getTransactionHistory(String userId) {
        return transactionService.getUserTransactions(userId);
    }

    @Override
    public Transaction requestPayment(String userId, String orderId, BigDecimal amount) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        Wallet wallet = walletService.findWallet(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        Transaction trx = transactionService.createTransaction(
                wallet,
                TransactionType.PAYMENT,
                amount,
                "Payment for order " + orderId
        );

        trx.setOrderId(UUID.fromString(orderId));

        try {
            walletService.debit(userId, amount);
            transactionService.markSuccess(trx.getId().toString());

            return trx;

        } catch (Exception e) {
            transactionService.markFailed(trx.getId().toString());
            throw e;
        }
    }
}
