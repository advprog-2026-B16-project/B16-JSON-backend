package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
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
        UpgradeRequestResponse response = new UpgradeRequestResponse(id, now, userId.toString(), "user", "Full Name", "Cred", "PENDING");
        assertEquals(id, response.id());

        // Test Builder
        UpgradeRequestResponse.UpgradeRequestResponseBuilder builder = UpgradeRequestResponse.builder();
        builder.id(id);
        builder.createdAt(now);
        builder.requesterUserId(userId.toString());
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
                .upgrReqId(id)
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
    void testUpgradeRequestSubmissionRequestExhaustive() {
        // Test Builder explicitly (inner class coverage)
        UpgradeRequestSubmissionRequest.UpgradeRequestSubmissionRequestBuilder builder = UpgradeRequestSubmissionRequest.builder();
        builder.fullName("Name");
        builder.credential("Cred");
        assertNotNull(builder.toString());
        UpgradeRequestSubmissionRequest a = builder.build();

        UpgradeRequestSubmissionRequest b = new UpgradeRequestSubmissionRequest("Name", "Cred");
        UpgradeRequestSubmissionRequest c = new UpgradeRequestSubmissionRequest("Diff", "Cred");
        UpgradeRequestSubmissionRequest d = new UpgradeRequestSubmissionRequest("Name", "Diff");
        UpgradeRequestSubmissionRequest e = new UpgradeRequestSubmissionRequest(null, "Cred");
        UpgradeRequestSubmissionRequest f = new UpgradeRequestSubmissionRequest("Name", null);
        UpgradeRequestSubmissionRequest g = new UpgradeRequestSubmissionRequest(null, null);

        // equals
        assertTrue(a.equals(a)); // o == this
        assertFalse(a.equals(null)); // o == null
        assertFalse(a.equals("string")); // instanceof
        assertTrue(a.equals(b)); // same values
        assertFalse(a.equals(c)); // first field diff
        assertFalse(a.equals(d)); // second field diff
        assertFalse(a.equals(e)); // first field this null
        assertFalse(e.equals(a)); // first field other null
        assertFalse(a.equals(f)); // second field this null
        assertFalse(f.equals(a)); // second field other null
        assertTrue(e.equals(new UpgradeRequestSubmissionRequest(null, "Cred"))); // both nulls same
        assertTrue(g.equals(new UpgradeRequestSubmissionRequest(null, null))); // all nulls same

        // Exhaustive field comparisons for 100% Lombok branch coverage
        assertNotEquals(new UpgradeRequestSubmissionRequest(null, "B"), new UpgradeRequestSubmissionRequest("A", "B"));
        assertNotEquals(new UpgradeRequestSubmissionRequest("A", "B"), new UpgradeRequestSubmissionRequest(null, "B"));
        assertNotEquals(new UpgradeRequestSubmissionRequest("A", null), new UpgradeRequestSubmissionRequest("A", "B"));
        assertNotEquals(new UpgradeRequestSubmissionRequest("A", "B"), new UpgradeRequestSubmissionRequest("A", null));

        // Final permutation: Field 1 same null, Field 2 diff
        assertNotEquals(new UpgradeRequestSubmissionRequest(null, "B"), new UpgradeRequestSubmissionRequest(null, "C"));
        // Final permutation: Field 1 diff, Field 2 same null
        assertNotEquals(new UpgradeRequestSubmissionRequest("A", null), new UpgradeRequestSubmissionRequest("B", null));

        // canEqual check with anonymous subclass
        UpgradeRequestSubmissionRequest subclass = new UpgradeRequestSubmissionRequest("Name", "Cred") {
            @Override
            public boolean canEqual(Object o) { return false; }
        };
        assertFalse(a.equals(subclass));
        assertTrue(a.canEqual(b));
        assertFalse(a.canEqual("string"));

        // hashCode
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
        assertNotEquals(a.hashCode(), e.hashCode());
        assertNotEquals(a.hashCode(), f.hashCode());
        assertEquals(g.hashCode(), new UpgradeRequestSubmissionRequest(null, null).hashCode());

        // toString
        assertNotNull(a.toString());
    }

    @Test
    void testUserLoginResponse() {
        UUID id = UUID.randomUUID();

        // Test Canonical Constructor
        UserLoginResponse response = new UserLoginResponse(id, "user", "email", "ROLE", "STATUS", "token123");
        assertEquals(id, response.id());
        assertEquals("token123", response.token());
        
        // Test Builder
        UserLoginResponse.UserLoginResponseBuilder builder = UserLoginResponse.builder();
        builder.id(id);
        builder.username("user");
        builder.email("email");
        builder.role("ROLE");
        builder.status("STATUS");
        builder.token("token123");
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

        UserLoginResponse fromUser = UserLoginResponse.fromUser(user, "token123");
        assertEquals(id, fromUser.id());
        assertEquals("token123", fromUser.token());
    }

    @Test
    void testUserProfileUpdateRequest() {
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("Full Name")
                .bio("Bio")
                .location("Location")
                .avatarUrl("Avatar")
                .build();
        
        assertEquals("Full Name", request.getFullName());
        assertEquals("Bio", request.getBio());
        assertEquals("Location", request.getLocation());
        assertEquals("Avatar", request.getAvatarUrl());

        UserProfileUpdateRequest empty = new UserProfileUpdateRequest();
        empty.setFullName("Name");
        assertEquals("Name", empty.getFullName());
    }

    @Test
    void testUserProfileResponse() {
        UUID id = UUID.randomUUID();
        UserProfileResponse response = UserProfileResponse.builder()
                .id(id)
                .username("user")
                .email("email")
                .role("TITIPER")
                .status("ACTIVE")
                .fullName("Full Name")
                .bio("Bio")
                .location("Location")
                .avatarUrl("Avatar")
                .successfulTransactions(5L)
                .build();

        assertEquals(id, response.getId());
        assertEquals("user", response.getUsername());
        assertEquals("email", response.getEmail());
        assertEquals("TITIPER", response.getRole());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("Full Name", response.getFullName());
        assertEquals("Bio", response.getBio());
        assertEquals("Location", response.getLocation());
        assertEquals("Avatar", response.getAvatarUrl());
        assertEquals(5L, response.getSuccessfulTransactions());

        UserProfileResponse empty = new UserProfileResponse();
        empty.setId(id);
        assertEquals(id, empty.getId());
    }
}
