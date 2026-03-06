package id.ac.ui.cs.advprog.jsonbackend.wallet;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.event.UserCreatedEvent;
import id.ac.ui.cs.advprog.jsonbackend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletEventListener {

    private final WalletService walletService;

    @EventListener
    public void handleUserRegistered(UserCreatedEvent event) {
        walletService.createWallet(event.getUserId());
    }
}