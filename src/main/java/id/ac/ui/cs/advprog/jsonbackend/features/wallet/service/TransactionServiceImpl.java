package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
@Service @Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepo;
    private final WalletService walletService;
    public TransactionServiceImpl(TransactionRepository tr, WalletService ws) { this.transactionRepo = tr; this.walletService = ws; }
    @Override public List<Transaction> getUserTransactions(String userId) { return transactionRepo.findByWalletId(walletService.findWallet(userId).getId().toString()); }
    @Override public Transaction createTransaction(Wallet wallet, TransactionType type, BigDecimal amount, String description) { return transactionRepo.save(new Transaction(wallet.getId().toString(), type, amount, description)); }
    @Override public void markSuccess(String id) { transactionRepo.findById(id).ifPresent(Transaction::markSuccess); }
    @Override public void markFailed(String id) { transactionRepo.findById(id).ifPresent(Transaction::markFailed); }
}