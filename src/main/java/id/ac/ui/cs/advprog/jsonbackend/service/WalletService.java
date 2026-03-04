package id.ac.ui.cs.advprog.jsonbackend.service;

import id.ac.ui.cs.advprog.jsonbackend.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.model.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    Wallet createWallet(String userId);
    void topUp(String userId, BigDecimal amount);
    void withdraw(String userId, BigDecimal amount);
    BigDecimal getBalance(String userId);
    List<WalletTransaction> getTransactionHistory(String userId);
}
