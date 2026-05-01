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
        String userId = UUID.randomUUID().toString();
        Wallet wallet = new Wallet(UUID.fromString(userId));
        when(walletService.findWallet(userId)).thenReturn(wallet);
        transactionService.getUserTransactions(userId);
        verify(transactionRepository).findByWalletId(any());
    }
}