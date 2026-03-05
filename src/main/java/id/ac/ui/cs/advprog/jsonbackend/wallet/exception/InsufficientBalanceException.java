package id.ac.ui.cs.advprog.jsonbackend.wallet.exception;

public class InsufficientBalanceException extends WalletException {

    public InsufficientBalanceException() {
        super("Insufficient balance", "INSUFFICIENT_BALANCE");
    }
}
