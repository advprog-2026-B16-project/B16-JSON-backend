package id.ac.ui.cs.advprog.jsonbackend.exception;

public class InsufficientBalanceException extends WalletException {

    public InsufficientBalanceException() {
        super("Insufficient balance", "INSUFFICIENT_BALANCE");
    }
}
