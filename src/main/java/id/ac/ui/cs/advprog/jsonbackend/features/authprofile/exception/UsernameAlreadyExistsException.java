package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
