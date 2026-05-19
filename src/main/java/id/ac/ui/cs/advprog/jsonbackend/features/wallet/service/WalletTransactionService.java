package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface WalletTransactionService {

    Transaction requestTopUp(String userId, BigDecimal amount);
    List<Transaction> getPendingTopUpRequests();
    void confirmTopUp(String transactionId);
    void rejectTopUp(String transactionId);

    void requestWithdraw(String userId, BigDecimal amount);

    void refund(String userId, BigDecimal amount);

    List<Transaction> getTransactionHistory(String userId);

    Transaction requestPayment(String userId, String orderId, BigDecimal amount);
}
