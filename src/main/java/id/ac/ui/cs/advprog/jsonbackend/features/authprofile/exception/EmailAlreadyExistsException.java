package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
