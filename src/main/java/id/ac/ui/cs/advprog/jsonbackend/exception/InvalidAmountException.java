package id.ac.ui.cs.advprog.jsonbackend.exception;

public class InvalidAmountException extends WalletException {

    public InvalidAmountException() {
        super("Amount must be greater than zero", "INVALID_AMOUNT");
    }
}
