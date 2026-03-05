package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

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
        
        request.setPassword(null);
        assertFalse(request.passwordConfirmationMathces());
        
        request.setPassword("pass");
        request.setConfirmPassword(null);
        assertFalse(request.passwordConfirmationMathces());
        
        request.setConfirmPassword("diff");
        assertFalse(request.passwordConfirmationMathces());
    }

    @Test
    void testUserLoginRequest() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testUpgradeRequestStatusChangeRequest() {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("testuser");
        request.setNewStatus("APPROVED");

        assertEquals("testuser", request.getUsername());
        assertEquals("APPROVED", request.getNewStatus());
    }

    @Test
    void testUpgradeRequestResponse() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        UUID userId = UUID.randomUUID();
        
        // Test Canonical Constructor
        UpgradeRequestResponse response = new UpgradeRequestResponse(id, now, userId, "user", "Full Name", "Cred", "PENDING");
        assertEquals(id, response.id());
        
        // Test Builder
        UpgradeRequestResponse.UpgradeRequestResponseBuilder builder = UpgradeRequestResponse.builder();
        builder.id(id);
        builder.createdAt(now);
        builder.requesterUserId(userId);
        builder.requesterUsername("user");
        builder.fullName("Full Name");
        builder.credential("Cred");
        builder.status("PENDING");
        assertNotNull(builder.toString());
        UpgradeRequestResponse responseFromBuilder = builder.build();
        
        assertEquals(response, responseFromBuilder);
        assertEquals(response.hashCode(), responseFromBuilder.hashCode());
        assertNotNull(response.toString());

        User user = User.builder().id(userId).username("user").build();
        UpgradeRequest ur = UpgradeRequest.builder()
                .id(id)
                .createdAt(now)
                .requesterUser(user)
                .fullName("Full Name")
                .credential("Cred")
                .status("PENDING")
                .build();

        UpgradeRequestResponse fromRequest = UpgradeRequestResponse.fromRequest(ur);
        assertEquals(response, fromRequest);
    }

    @Test
    void testUserLoginResponse() {
        UUID id = UUID.randomUUID();
        
        // Test Canonical Constructor
        UserLoginResponse response = new UserLoginResponse(id, "user", "email", "ROLE", "STATUS");
        assertEquals(id, response.id());
        
        // Test Builder
        UserLoginResponse.UserLoginResponseBuilder builder = UserLoginResponse.builder();
        builder.id(id);
        builder.username("user");
        builder.email("email");
        builder.role("ROLE");
        builder.status("STATUS");
        assertNotNull(builder.toString());
        UserLoginResponse responseFromBuilder = builder.build();
        
        assertEquals(response, responseFromBuilder);
        assertEquals(response.hashCode(), responseFromBuilder.hashCode());
        assertNotNull(response.toString());

        User user = User.builder()
                .id(id)
                .username("user")
                .email("email")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        
        UserLoginResponse fromUser = UserLoginResponse.fromUser(user);
        assertEquals(id, fromUser.id());
    }
}
