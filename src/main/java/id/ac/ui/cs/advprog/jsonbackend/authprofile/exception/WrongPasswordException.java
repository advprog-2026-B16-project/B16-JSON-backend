package id.ac.ui.cs.advprog.jsonbackend.authprofile.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
