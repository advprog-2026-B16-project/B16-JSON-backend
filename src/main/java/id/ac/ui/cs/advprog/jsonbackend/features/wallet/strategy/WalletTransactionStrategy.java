package id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;

import java.math.BigDecimal;

public interface WalletTransactionStrategy {
    TransactionType getType();

    Transaction execute(String userId, String orderId, BigDecimal amount);
}
