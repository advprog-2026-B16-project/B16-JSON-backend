package id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception;

import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends WalletException {

    public WalletNotFoundException(String userId) {
        super("Wallet not found for user: " + userId, "WALLET_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
