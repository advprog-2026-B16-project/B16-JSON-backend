package id.ac.ui.cs.advprog.jsonbackend.features.wallet.event;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserCreatedEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WalletEventListenerTest {

    @Test
    void handleUserRegisteredShouldCreateWallet() {
        WalletService walletService = mock(WalletService.class);
        WalletEventListener listener = new WalletEventListener(walletService);

        listener.handleUserRegistered(new UserCreatedEvent("user-123"));

        verify(walletService).createWallet("user-123");
    }
}
