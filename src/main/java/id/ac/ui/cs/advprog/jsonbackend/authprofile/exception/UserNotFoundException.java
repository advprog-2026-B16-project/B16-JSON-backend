package id.ac.ui.cs.advprog.jsonbackend.authprofile.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
