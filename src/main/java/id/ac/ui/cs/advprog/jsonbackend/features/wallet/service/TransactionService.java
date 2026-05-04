package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Wallet wallet, TransactionType type, BigDecimal amount, String description);

    Transaction getTransactionById(String transactionId);

    void markSuccess(String transactionId);
    void markFailed(String transactionId);

    List<Transaction> getUserTransactions(String userId);
}
