package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.UUID;
import static org.mockito.Mockito.*;
class TestWalletTransactionServiceImpl {
    @Mock private TransactionRepository transactionRepository;
    @Mock private WalletService walletService;
    @InjectMocks private TransactionServiceImpl transactionService;
    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }
    @Test void testGetUserTransactions() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(userId);
        wallet.setId(UUID.randomUUID()); 
        when(walletService.findWallet(userId.toString())).thenReturn(wallet);
        transactionService.getUserTransactions(userId.toString());
        verify(transactionRepository).findByWalletId(any());
    }
}