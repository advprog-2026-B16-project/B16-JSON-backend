package id.ac.ui.cs.advprog.jsonbackend.wallet.exception;

public abstract class WalletException extends RuntimeException {

    private final String errorCode;

    protected WalletException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
