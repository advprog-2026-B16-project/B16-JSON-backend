package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public WalletTransactionServiceImpl(
            WalletService walletService,
            TransactionService transactionService
    ) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public void topUp(String userId, BigDecimal amount) {
        processTransaction(userId, amount, TransactionType.TOP_UP, "Top Up", true);
    }

    @Override
    public void withdraw(String userId, BigDecimal amount) {
        processTransaction(userId, amount, TransactionType.WITHDRAW, "Withdraw", false);
    }

    @Override
    public void refund(String userId, BigDecimal amount) {
        processTransaction(userId, amount, TransactionType.REFUND, "Refund", true);
    }

    @Override
    public List<Transaction> getTransactionHistory(String userId) {
        return transactionService.getUserTransactions(userId);
    }

    private void processTransaction(
            String userId,
            BigDecimal amount,
            TransactionType type,
            String description,
            boolean isCredit
    ) {
        Wallet wallet = walletService.findWallet(userId);
        Transaction transaction = transactionService.createTransaction(wallet, type, amount, description);

        try {
            if (isCredit) {
                walletService.credit(userId, amount);
            } else {
                walletService.debit(userId, amount);
            }
            transactionService.markSuccess(transaction.getId());
        } catch (RuntimeException exception) {
            if (transaction.getId() != null) {
                transactionService.markFailed(transaction.getId());
            }
            throw exception;
        }
    }
}
