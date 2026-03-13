package id.ac.ui.cs.advprog.jsonbackend.module.wallet.event;

import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.event.UserCreatedEvent;
import id.ac.ui.cs.advprog.jsonbackend.module.wallet.service.WalletService;
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