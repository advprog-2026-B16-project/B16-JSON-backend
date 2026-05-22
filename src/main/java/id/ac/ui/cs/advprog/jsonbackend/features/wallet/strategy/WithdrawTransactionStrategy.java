package id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WithdrawTransactionStrategy implements WalletTransactionStrategy {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public WithdrawTransactionStrategy(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WITHDRAW;
    }

    @Override
    public Transaction execute(String userId, String orderId, BigDecimal amount) {
        Wallet wallet = walletService.findWallet(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        Transaction trx = transactionService.createTransaction(
                wallet,
                TransactionType.WITHDRAW,
                amount,
                "Withdraw Request"
        );

        try {
            walletService.debit(userId, amount);
            transactionService.markSuccess(trx.getId().toString());
            return trx;
        } catch (Exception e) {
            transactionService.markFailed(trx.getId().toString());
            throw e;
        }
    }
}
