package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.TransactionRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Override
    public List<Transaction> getUserTransactions(String userId) {
        Wallet wallet = walletService.findWallet(userId);
        return transactionRepository.findByWalletId(wallet.getId());
    }

    @Override
    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    @Override
    public Transaction createTransaction(Wallet wallet, TransactionType type, BigDecimal amount, String description) {

        Transaction transaction = new Transaction(
                wallet.getUserId(),
                wallet.getId(),
                type,
                amount,
                description
        );

        return transactionRepository.save(transaction);
    }

    @Override
    public void markSuccess(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
        transaction.markSuccess();
    }

    @Override
    public void markFailed(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
        transaction.markFailed();
    }
}
