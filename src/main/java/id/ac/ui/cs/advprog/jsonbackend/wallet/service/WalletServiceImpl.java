package id.ac.ui.cs.advprog.jsonbackend.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.WalletTransaction;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.wallet.repository.WalletRepository;
import id.ac.ui.cs.advprog.jsonbackend.wallet.repository.WalletTransactionRepository;
import id.ac.ui.cs.advprog.jsonbackend.wallet.exception.WalletNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletServiceImpl(WalletRepository walletRepository, WalletTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Wallet createWallet(String userId) {
        Wallet wallet = new Wallet(userId);
        return walletRepository.save(wallet);
    }

    @Override
    public void topUp(String userId, BigDecimal amount) {
        Wallet wallet = findWallet(userId);

        WalletTransaction transaction = createTransaction(
                wallet,
                TransactionType.TOP_UP,
                amount,
                "Top Up"
        );

        wallet.credit(amount);
        transaction.markSuccess();
    }

    @Override
    public void withdraw(String userId, BigDecimal amount) {
        Wallet wallet = findWallet(userId);

        WalletTransaction transaction = createTransaction(
                wallet,
                TransactionType.WITHDRAW,
                amount,
                "Withdraw"
        );

        wallet.debit(amount);
        transaction.markSuccess();
    }

    @Override
    public BigDecimal getBalance(String userId) {
        Wallet wallet = findWallet(userId);
        return wallet.getBalance();
    }

    @Override
    public List<WalletTransaction> getTransactionHistory(String userId) {
        Wallet wallet = findWallet(userId);
        return transactionRepository.findByWalletId(wallet.getId());
    }

    private Wallet findWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));
    }

    private WalletTransaction createTransaction(Wallet wallet,
                                                TransactionType type,
                                                BigDecimal amount,
                                                String description) {

        WalletTransaction transaction = new WalletTransaction(
                wallet.getId(),
                type,
                amount,
                description
        );

        return transactionRepository.save(transaction);
    }
}