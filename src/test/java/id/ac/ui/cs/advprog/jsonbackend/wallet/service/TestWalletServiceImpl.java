package id.ac.ui.cs.advprog.jsonbackend.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.wallet.exception.WalletNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.WalletTransaction;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.wallet.repository.WalletRepository;
import id.ac.ui.cs.advprog.jsonbackend.wallet.repository.WalletTransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestWalletServiceImpl {

    private WalletRepository walletRepository;
    private WalletTransactionRepository transactionRepository;
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletRepository = Mockito.mock(WalletRepository.class);
        transactionRepository = Mockito.mock(WalletTransactionRepository.class);
        walletService = new WalletServiceImpl(walletRepository, transactionRepository);
    }

    @Test
    void testCreateWallet() {
        String userId = "user1";
        Wallet wallet = new Wallet(userId);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet result = walletService.createWallet(userId);

        assertEquals(userId, result.getUserId());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void testTopUp() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("100");

        Wallet wallet = new Wallet(userId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(WalletTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        walletService.topUp(userId, amount);

        assertEquals(new BigDecimal("100"), wallet.getBalance());
        verify(transactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void testWithdraw() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("50");

        Wallet wallet = new Wallet(userId);
        wallet.credit(new BigDecimal("100"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(WalletTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        walletService.withdraw(userId, amount);

        assertEquals(new BigDecimal("50"), wallet.getBalance());
        verify(transactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    void testGetBalance() {
        String userId = "user1";

        Wallet wallet = new Wallet(userId);
        wallet.credit(new BigDecimal("200"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(userId);

        assertEquals(new BigDecimal("200"), balance);
    }

    @Test
    void testGetTransactionHistory() {
        String userId = "user1";
        Wallet wallet = new Wallet(userId);

        WalletTransaction tx = new WalletTransaction(
                wallet.getId(),
                TransactionType.TOP_UP,
                new BigDecimal("100"),
                "Top Up"
        );

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(wallet.getId()))
                .thenReturn(List.of(tx));

        List<WalletTransaction> history = walletService.getTransactionHistory(userId);

        assertEquals(1, history.size());
    }

    @Test
    void testWalletNotFound() {
        String userId = "unknown";

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getBalance(userId);
        });
    }
}