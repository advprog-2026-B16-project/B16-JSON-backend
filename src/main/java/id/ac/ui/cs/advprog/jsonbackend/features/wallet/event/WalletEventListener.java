package id.ac.ui.cs.advprog.jsonbackend.features.wallet.event;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserCreatedEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class WalletEventListener {
    private final WalletService walletService;
    // @org.springframework.context.event.EventListener
    public void handleUserRegistered(UserCreatedEvent event) {
        // Disabled to prevent type conflict crashes during Auth/Profile testing
    }
}