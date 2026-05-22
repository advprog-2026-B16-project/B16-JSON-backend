package id.ac.ui.cs.advprog.jsonbackend.features.transaction.service;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.repository.TransactionRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByTypeAndStatus(TransactionType type, TransactionStatus status) {
        return transactionRepository.findByTypeAndStatusOrderByCreatedAtDesc(type, status);
    }

    @Override
    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    @Override
    public Transaction getTransactionByIdForUpdate(String transactionId) {
        return transactionRepository.findByIdForUpdate(UUID.fromString(transactionId))
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    @Override
    public Transaction createTransaction(Wallet wallet, TransactionType type, BigDecimal amount, String description) {

        Transaction transaction = new Transaction(
                wallet.getId(),
                wallet.getUserId(),
                type,
                amount,
                description
        );

        return transactionRepository.save(transaction);
    }

    @Override
    public void markSuccess(String transactionId) {
        Transaction transaction = transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
        transaction.markSuccess();
    }

    @Override
    public void markFailed(String transactionId) {
        Transaction transaction = transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
        transaction.markFailed();
    }
}
