package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    Wallet createWallet(String userId);
    void credit(String userId, BigDecimal amount);
    void debit(String userId, BigDecimal amount);
    BigDecimal getBalance(String userId);
    Wallet findWallet(String userId);
}
