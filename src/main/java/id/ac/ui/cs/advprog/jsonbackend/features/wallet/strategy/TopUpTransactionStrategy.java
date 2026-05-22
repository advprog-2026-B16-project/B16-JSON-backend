package id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TopUpTransactionStrategy implements WalletTransactionStrategy {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public TopUpTransactionStrategy(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TOP_UP;
    }

    @Override
    public Transaction execute(String userId, String orderId, BigDecimal amount) {
        Wallet wallet = walletService.findWallet(userId);
        return transactionService.createTransaction(
                wallet,
                TransactionType.TOP_UP,
                amount,
                "Top Up Request"
        );
    }
}
