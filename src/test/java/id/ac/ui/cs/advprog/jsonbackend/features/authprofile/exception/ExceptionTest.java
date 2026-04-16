package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testBadCredentialsException() {
        BadCredentialsException exception = new BadCredentialsException("Invalid credentials");
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testUsernameAlreadyExistsException() {
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException("Username exists");
        assertEquals("Username exists", exception.getMessage());
    }

    @Test
    void testEmailAlreadyExistsException() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("Email exists");
        assertEquals("Email exists", exception.getMessage());
    }

    @Test
    void testUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException("Not found");
        assertEquals("Not found", exception.getMessage());
    }

    @Test
    void testWrongPasswordException() {
        WrongPasswordException exception = new WrongPasswordException("Wrong password");
        assertEquals("Wrong password", exception.getMessage());
    }
}
