package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestWalletServiceImpl {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWallet_whenNotExists_shouldCreateNewWallet() {
        UUID USER_ID = UUID.randomUUID();

        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(Optional.empty());

        walletService.createWallet(USER_ID.toString());

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());

        Wallet savedWallet = walletCaptor.getValue();

        assertEquals(USER_ID, savedWallet.getUserId());
        assertEquals(0, savedWallet.getBalance().compareTo(java.math.BigDecimal.ZERO));
    }

    @Test
    void testCreateWallet_whenAlreadyExists_shouldNotCreateNewWallet() {
        UUID USER_ID = UUID.randomUUID();

        when(walletRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(new Wallet(USER_ID)));

        walletService.createWallet(USER_ID.toString());

        verify(walletRepository, never()).save(any());
    }

    @Test
    void testCreditAddsBalance() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.credit(userId.toString(), new BigDecimal("150"));

        assertEquals(new BigDecimal("150"), wallet.getBalance());
    }

    @Test
    void testDebitSubtractsBalance() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        wallet.setBalance(new BigDecimal("200"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.debit(userId.toString(), new BigDecimal("75"));

        assertEquals(new BigDecimal("125"), wallet.getBalance());
    }

    @Test
    void testGetBalanceReturnsWalletBalance() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        wallet.setBalance(new BigDecimal("250"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertEquals(new BigDecimal("250"), walletService.getBalance(userId.toString()));
    }

    @Test
    void testFindWalletReturnsWallet() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertSame(wallet, walletService.findWallet(userId.toString()));
    }

    @Test
    void testFindWalletThrowsWhenMissing() {
        UUID userId = UUID.randomUUID();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.findWallet(userId.toString()));
    }

    @Test
    void testFindWalletForUpdateReturnsWallet() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);

        when(walletRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(wallet));

        assertSame(wallet, walletService.findWalletForUpdate(userId.toString()));
    }

    @Test
    void testFindWalletForUpdateThrowsWhenMissing() {
        UUID userId = UUID.randomUUID();

        when(walletRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.findWalletForUpdate(userId.toString()));
    }
}
