package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy.PaymentTransactionStrategy;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy.RefundTransactionStrategy;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy.TopUpTransactionStrategy;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy.WalletTransactionStrategy;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.strategy.WithdrawTransactionStrategy;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InvalidWalletTransactionException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InvalidAmountException;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final Map<TransactionType, WalletTransactionStrategy> transactionStrategies;

    @Autowired
    public WalletTransactionServiceImpl(
            WalletService walletService,
            TransactionService transactionService,
            List<WalletTransactionStrategy> transactionStrategies
    ) {
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.transactionStrategies = buildStrategyMap(transactionStrategies);
    }

    public WalletTransactionServiceImpl(
            WalletService walletService,
            TransactionService transactionService
    ) {
        this(
                walletService,
                transactionService,
                List.of(
                        new TopUpTransactionStrategy(walletService, transactionService),
                        new WithdrawTransactionStrategy(walletService, transactionService),
                        new RefundTransactionStrategy(walletService, transactionService),
                        new PaymentTransactionStrategy(walletService, transactionService)
                )
        );
    }

    @Override
    public Transaction requestTopUp(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);
        return executeStrategy(TransactionType.TOP_UP, userId, null, amount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getPendingTopUpRequests() {
        return transactionService.getTransactionsByTypeAndStatus(TransactionType.TOP_UP, TransactionStatus.PENDING);
    }

    @Override
    public void confirmTopUp(String transactionId) {
        Transaction trx = transactionService.getTransactionByIdForUpdate(transactionId);

        if (trx.getType() != TransactionType.TOP_UP) {
            throw new InvalidWalletTransactionException("Only top up transactions can be confirmed");
        }

        if (trx.getStatus() == TransactionStatus.SUCCESS) {
            return;
        }

        if (trx.getStatus() != TransactionStatus.PENDING) {
            throw new InvalidWalletTransactionException("Only pending top up transactions can be confirmed");
        }

        walletService.credit(trx.getUserId().toString(), trx.getAmount());

        transactionService.markSuccess(transactionId);
    }

    @Override
    public void rejectTopUp(String transactionId) {
        Transaction trx = transactionService.getTransactionByIdForUpdate(transactionId);

        if (trx.getType() != TransactionType.TOP_UP) {
            throw new InvalidWalletTransactionException(
                    "Only top up transactions can be rejected"
            );
        }

        if (trx.getStatus() == TransactionStatus.FAILED) {
            return;
        }

        if (trx.getStatus() != TransactionStatus.PENDING) {
            throw new InvalidWalletTransactionException(
                    "Only pending top up transactions can be rejected"
            );
        }

        transactionService.markFailed(transactionId);
    }



    @Override
    public void requestWithdraw(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);
        executeStrategy(TransactionType.WITHDRAW, userId, null, amount);
    }

    @Override
    public void refund(String userId, BigDecimal amount) {
        validatePositiveAmount(amount);
        executeStrategy(TransactionType.REFUND, userId, null, amount);
    }

    @Override
    public List<Transaction> getTransactionHistory(String userId) {
        return transactionService.getUserTransactions(userId);
    }

    @Override
    public Transaction requestPayment(String userId, String orderId, BigDecimal amount) {
        validatePositiveAmount(amount);
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        return executeStrategy(TransactionType.PAYMENT, userId, orderId, amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }

    private Transaction executeStrategy(TransactionType type, String userId, String orderId, BigDecimal amount) {
        WalletTransactionStrategy strategy = transactionStrategies.get(type);
        if (strategy == null) {
            throw new InvalidWalletTransactionException("Unsupported wallet transaction type: " + type);
        }
        return strategy.execute(userId, orderId, amount);
    }

    private Map<TransactionType, WalletTransactionStrategy> buildStrategyMap(List<WalletTransactionStrategy> strategies) {
        Map<TransactionType, WalletTransactionStrategy> strategyMap = new EnumMap<>(TransactionType.class);
        for (WalletTransactionStrategy strategy : strategies) {
            strategyMap.put(strategy.getType(), strategy);
        }
        return strategyMap;
    }
}
