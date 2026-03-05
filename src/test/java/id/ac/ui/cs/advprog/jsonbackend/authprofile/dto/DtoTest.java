package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testUserRegistrationRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("password123", request.getConfirmPassword());
        assertTrue(request.passwordConfirmationMathces());

        request.setConfirmPassword("different");
        assertFalse(request.passwordConfirmationMathces());

        request.setPassword(null);
        assertFalse(request.passwordConfirmationMathces());
    }

    @Test
    void testUserLoginRequest() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("login@example.com");
        request.setPassword("secret123");

        assertEquals("login@example.com", request.getEmail());
        assertEquals("secret123", request.getPassword());
        
        request.setEmail("new@example.com");
        request.setPassword("newpass");
        assertEquals("new@example.com", request.getEmail());
        assertEquals("newpass", request.getPassword());
    }

    @Test
    void testUpgradeRequestStatusChangeRequest() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("user1");
        request.setNewStatus("APPROVED");

        assertEquals("user1", request.getUsername());
        assertEquals("APPROVED", request.getNewStatus());
    }

    @Test
    void testUpgradeRequestResponse() {
        UpgradeRequest ur = new UpgradeRequest();
        UpgradeRequestResponse response = new UpgradeRequestResponse(ur);
        assertEquals(ur, response.upgradeRequest());
    }

    @Test
    void testUserLoginResponse() {
        User user = new User("john", "john@example.com", "mypassword", UserRole.TITIPER, UserStatus.ACTIVE);
        UserLoginResponse response = new UserLoginResponse(user);
        
        assertEquals(user, response.user());
        assertEquals("", user.getPassword()); // Test that password is cleared
    }
}
