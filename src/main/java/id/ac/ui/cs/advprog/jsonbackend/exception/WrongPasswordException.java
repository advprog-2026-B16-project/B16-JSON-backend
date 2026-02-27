package id.ac.ui.cs.advprog.jsonbackend.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
