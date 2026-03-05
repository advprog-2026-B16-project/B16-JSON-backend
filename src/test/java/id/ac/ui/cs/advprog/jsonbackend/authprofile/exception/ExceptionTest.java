package id.ac.ui.cs.advprog.jsonbackend.authprofile.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testUserNotFoundException() {
        UserNotFoundException exception = new UserNotFoundException("User not found");
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testWrongPasswordException() {
        WrongPasswordException exception = new WrongPasswordException("Wrong password");
        assertEquals("Wrong password", exception.getMessage());
    }
}
