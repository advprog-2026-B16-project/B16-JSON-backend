package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
