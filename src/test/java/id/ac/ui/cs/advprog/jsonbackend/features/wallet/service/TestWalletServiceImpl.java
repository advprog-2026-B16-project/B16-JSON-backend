package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class TestWalletServiceImpl {
    @Mock private WalletRepository walletRepository;
    @InjectMocks private WalletServiceImpl walletService;
    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }
    @Test void testCreateWallet() {
        String userId = UUID.randomUUID().toString();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        walletService.createWallet(userId);
        verify(walletRepository).save(any(Wallet.class));
    }
}