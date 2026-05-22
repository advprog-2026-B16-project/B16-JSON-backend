package id.ac.ui.cs.advprog.jsonbackend.features.wallet.event;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserCreatedEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserLoggedInEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WalletEventListener {

    private final WalletService walletService;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserRegistered(UserCreatedEvent event) {
        walletService.createWallet(event.getUserId());
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserLoggedIn(UserLoggedInEvent event) {
        walletService.createWallet(event.getUserId());
    }
}
