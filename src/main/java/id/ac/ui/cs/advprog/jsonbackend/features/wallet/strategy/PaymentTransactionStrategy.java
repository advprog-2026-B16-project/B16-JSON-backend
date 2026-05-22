package id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentTransactionStrategy implements WalletTransactionStrategy {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public PaymentTransactionStrategy(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.PAYMENT;
    }

    @Override
    public Transaction execute(String userId, String orderId, BigDecimal amount) {
        Wallet wallet = walletService.findWalletForUpdate(userId);

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
