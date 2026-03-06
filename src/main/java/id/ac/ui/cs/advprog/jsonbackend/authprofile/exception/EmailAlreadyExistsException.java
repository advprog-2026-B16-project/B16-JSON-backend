package id.ac.ui.cs.advprog.jsonbackend.authprofile.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
