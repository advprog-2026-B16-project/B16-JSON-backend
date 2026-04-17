package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestWalletServiceImpl {

    private WalletRepository walletRepository;
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletRepository = Mockito.mock(WalletRepository.class);
        walletService = new WalletServiceImpl(walletRepository);
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
    void testCredit() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("100");

        Wallet wallet = new Wallet(userId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.credit(userId, amount);

        assertEquals(new BigDecimal("100"), wallet.getBalance());
    }

    @Test
    void testDebit() {
        String userId = "user1";
        BigDecimal amount = new BigDecimal("50");

        Wallet wallet = new Wallet(userId);
        wallet.credit(new BigDecimal("100"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.debit(userId, amount);

        assertEquals(new BigDecimal("50"), wallet.getBalance());
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
    void testWalletNotFound() {
        String userId = "unknown";

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getBalance(userId);
        });
    }
}
