package id.ac.ui.cs.advprog.jsonbackend.module.authprofile.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String message) {
        super(message);
    }
}
