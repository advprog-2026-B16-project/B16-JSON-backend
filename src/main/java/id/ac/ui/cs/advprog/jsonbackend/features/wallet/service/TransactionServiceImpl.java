package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.TransactionRepository;
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

    public TransactionServiceImpl(TransactionRepository tr, WalletService ws) {
        this.transactionRepository = tr;
        this.walletService = ws;
    }

    @Override
    public List<Transaction> getUserTransactions(String userId) {
        return transactionRepository.findByWalletId(walletService.findWallet(userId).getId());
    }

    @Override
    public Transaction createTransaction(Wallet wallet, TransactionType type, BigDecimal amount, String description) {
        return transactionRepository.save(new Transaction(wallet.getId(), type, amount, description));
    }

    @Override
    public void markSuccess(String id) {
        transactionRepository.findById(id).ifPresent(Transaction::markSuccess);
    }

    @Override
    public void markFailed(String id) {
        transactionRepository.findById(id).ifPresent(Transaction::markFailed);
    }
}