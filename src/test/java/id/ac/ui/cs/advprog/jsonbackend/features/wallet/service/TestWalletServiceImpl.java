package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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
}