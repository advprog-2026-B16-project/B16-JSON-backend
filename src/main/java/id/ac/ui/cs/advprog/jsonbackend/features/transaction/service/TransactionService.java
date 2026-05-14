package id.ac.ui.cs.advprog.jsonbackend.features.transaction.service;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Wallet wallet, TransactionType type, BigDecimal amount, String description);

    Transaction getTransactionById(String transactionId);

    Transaction getTransactionByIdForUpdate(String transactionId);

    void markSuccess(String transactionId);
    void markFailed(String transactionId);

    List<Transaction> getUserTransactions(String userId);
}
