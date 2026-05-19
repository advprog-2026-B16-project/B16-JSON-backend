package id.ac.ui.cs.advprog.jsonbackend.features.transaction.service;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.repository.TransactionRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    private TransactionRepository transactionRepository;
    private WalletService walletService;
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        walletService = mock(WalletService.class);
        transactionService = new TransactionServiceImpl(transactionRepository, walletService);
    }

    @Test
    void createTransactionSavesPendingTransaction() {
        Wallet wallet = buildWallet();

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(wallet, TransactionType.TOP_UP, new BigDecimal("50000"), "Top Up");

        assertEquals(wallet.getId(), result.getWalletId());
        assertEquals(wallet.getUserId(), result.getUserId());
        assertEquals(TransactionType.TOP_UP, result.getType());
        assertEquals(TransactionStatus.PENDING, result.getStatus());
        verify(transactionRepository).save(result);
    }

    @Test
    void getUserTransactionsUsesWalletId() {
        Wallet wallet = buildWallet();
        Transaction transaction = new Transaction(wallet.getId(), wallet.getUserId(), TransactionType.TOP_UP, BigDecimal.TEN, "Top Up");

        when(walletService.findWallet(wallet.getUserId().toString())).thenReturn(wallet);
        when(transactionRepository.findByWalletId(wallet.getId())).thenReturn(List.of(transaction));

        List<Transaction> result = transactionService.getUserTransactions(wallet.getUserId().toString());

        assertEquals(List.of(transaction), result);
    }

    @Test
    void getTransactionsByTypeAndStatusUsesRepositoryFilter() {
        Transaction transaction = buildTransaction();

        when(transactionRepository.findByTypeAndStatusOrderByCreatedAtDesc(TransactionType.TOP_UP, TransactionStatus.PENDING))
                .thenReturn(List.of(transaction));

        List<Transaction> result = transactionService.getTransactionsByTypeAndStatus(TransactionType.TOP_UP, TransactionStatus.PENDING);

        assertEquals(List.of(transaction), result);
    }

    @Test
    void getTransactionByIdReturnsTransaction() {
        Transaction transaction = buildTransaction();

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        assertSame(transaction, transactionService.getTransactionById(transaction.getId().toString()));
    }

    @Test
    void getTransactionByIdThrowsWhenMissing() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionById(transactionId.toString()));
    }

    @Test
    void getTransactionByIdForUpdateReturnsTransaction() {
        Transaction transaction = buildTransaction();

        when(transactionRepository.findByIdForUpdate(transaction.getId())).thenReturn(Optional.of(transaction));

        assertSame(transaction, transactionService.getTransactionByIdForUpdate(transaction.getId().toString()));
    }

    @Test
    void getTransactionByIdForUpdateThrowsWhenMissing() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.findByIdForUpdate(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransactionByIdForUpdate(transactionId.toString()));
    }

    @Test
    void markSuccessUpdatesStatus() {
        Transaction transaction = buildTransaction();

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transactionService.markSuccess(transaction.getId().toString());

        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    void markFailedUpdatesStatus() {
        Transaction transaction = buildTransaction();

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transactionService.markFailed(transaction.getId().toString());

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
    }

    @Test
    void markSuccessThrowsWhenMissing() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.markSuccess(transactionId.toString()));
    }

    @Test
    void markFailedThrowsWhenMissing() {
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.markFailed(transactionId.toString()));
    }

    private Wallet buildWallet() {
        Wallet wallet = new Wallet(UUID.randomUUID());
        wallet.setId(UUID.randomUUID());
        return wallet;
    }

    private Transaction buildTransaction() {
        Transaction transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), TransactionType.TOP_UP, BigDecimal.TEN, "Top Up");
        transaction.setId(UUID.randomUUID());
        return transaction;
    }
}
