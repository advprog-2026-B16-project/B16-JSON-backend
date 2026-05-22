package id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception;

import org.springframework.http.HttpStatus;

public class WalletUnauthorizedException extends WalletException {

    public WalletUnauthorizedException(String message) {
        super(message, "WALLET_UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
