package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileResponseTest {

    private UserProfileResponse response;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        response = UserProfileResponse.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .role("TITIPER")
            .status("ACTIVE")
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .successfulTransactions(42L)
            .build();
    }

    // ==================== Builder Tests ====================

    @Test
    void testBuilder_CreateCompleteResponse() {
        UserProfileResponse built = UserProfileResponse.builder()
            .id(testId)
            .username("builderuser")
            .email("builder@example.com")
            .role("JASTIPER")
            .status("ACTIVE")
            .fullName("Builder User")
            .bio("Bio")
            .location("Location")
            .avatarUrl("http://example.com/ava.jpg")
            .successfulTransactions(100L)
            .build();

        assertEquals(testId, built.getId());
        assertEquals("builderuser", built.getUsername());
        assertEquals("builder@example.com", built.getEmail());
        assertEquals("JASTIPER", built.getRole());
        assertEquals("ACTIVE", built.getStatus());
        assertEquals("Builder User", built.getFullName());
        assertEquals("Bio", built.getBio());
        assertEquals("Location", built.getLocation());
        assertEquals("http://example.com/ava.jpg", built.getAvatarUrl());
        assertEquals(100L, built.getSuccessfulTransactions());
    }

    @Test
    void testBuilder_CreateWithMinimalFields() {
        UserProfileResponse built = UserProfileResponse.builder()
            .username("minimal")
            .email("minimal@example.com")
            .build();

        assertNull(built.getId());
        assertEquals("minimal", built.getUsername());
        assertEquals("minimal@example.com", built.getEmail());
        assertNull(built.getRole());
        assertNull(built.getStatus());
    }

    @Test
    void testBuilder_AllNullFields() {
        UserProfileResponse built = UserProfileResponse.builder()
            .build();

        assertNull(built.getId());
        assertNull(built.getUsername());
        assertNull(built.getEmail());
        assertNull(built.getRole());
        assertNull(built.getStatus());
    }

    // ==================== No-Args Constructor ====================

    @Test
    void testNoArgsConstructor() {
        UserProfileResponse noArgs = new UserProfileResponse();
        assertNotNull(noArgs);
        assertNull(noArgs.getId());
        assertNull(noArgs.getUsername());
    }

    // ==================== All-Args Constructor ====================

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UserProfileResponse allArgs = new UserProfileResponse(
            id,
            "username",
            "email@example.com",
            "TITIPER",
            "ACTIVE",
            "Full Name",
            "Bio",
            "Location",
            "avatar_url",
            50L
        );

        assertEquals(id, allArgs.getId());
        assertEquals("username", allArgs.getUsername());
        assertEquals("email@example.com", allArgs.getEmail());
        assertEquals("TITIPER", allArgs.getRole());
        assertEquals("ACTIVE", allArgs.getStatus());
        assertEquals("Full Name", allArgs.getFullName());
        assertEquals("Bio", allArgs.getBio());
        assertEquals("Location", allArgs.getLocation());
        assertEquals("avatar_url", allArgs.getAvatarUrl());
        assertEquals(50L, allArgs.getSuccessfulTransactions());
    }

    // ==================== Getters and Setters ====================

    @Test
    void testGettersAndSetters() {
        UUID newId = UUID.randomUUID();
        response.setId(newId);
        assertEquals(newId, response.getId());

        response.setUsername("newusername");
        assertEquals("newusername", response.getUsername());

        response.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", response.getEmail());

        response.setRole("ADMIN");
        assertEquals("ADMIN", response.getRole());

        response.setStatus("PENDING_JASTIPER");
        assertEquals("PENDING_JASTIPER", response.getStatus());

        response.setFullName("New Full Name");
        assertEquals("New Full Name", response.getFullName());

        response.setBio("New bio");
        assertEquals("New bio", response.getBio());

        response.setLocation("New Location");
        assertEquals("New Location", response.getLocation());

        response.setAvatarUrl("http://example.com/newavatar.jpg");
        assertEquals("http://example.com/newavatar.jpg", response.getAvatarUrl());

        response.setSuccessfulTransactions(999L);
        assertEquals(999L, response.getSuccessfulTransactions());
    }

    @Test
    void testSetId_Null() {
        response.setId(null);
        assertNull(response.getId());
    }

    @Test
    void testSetUsername_Null() {
        response.setUsername(null);
        assertNull(response.getUsername());
    }

    @Test
    void testSetEmail_Null() {
        response.setEmail(null);
        assertNull(response.getEmail());
    }

    @Test
    void testSetSuccessfulTransactions_Null() {
        response.setSuccessfulTransactions(null);
        assertNull(response.getSuccessfulTransactions());
    }

    @Test
    void testSetSuccessfulTransactions_Zero() {
        response.setSuccessfulTransactions(0L);
        assertEquals(0L, response.getSuccessfulTransactions());
    }

    @Test
    void testSetSuccessfulTransactions_Negative() {
        response.setSuccessfulTransactions(-1L);
        assertEquals(-1L, response.getSuccessfulTransactions());
    }

    @Test
    void testSetSuccessfulTransactions_Large() {
        response.setSuccessfulTransactions(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, response.getSuccessfulTransactions());
    }

    // ==================== Lombok @Builder Methods ====================

    @Test
    void testBuilder_DifferentRoles() {
        String[] roles = {"TITIPER", "JASTIPER", "ADMIN"};

        for (String role : roles) {
            UserProfileResponse resp = UserProfileResponse.builder()
                .username("user")
                .email("email@example.com")
                .role(role)
                .build();

            assertEquals(role, resp.getRole());
        }
    }

    @Test
    void testBuilder_DifferentStatuses() {
        String[] statuses = {"ACTIVE", "PENDING_JASTIPER", "BANNED"};

        for (String status : statuses) {
            UserProfileResponse resp = UserProfileResponse.builder()
                .username("user")
                .email("email@example.com")
                .status(status)
                .build();

            assertEquals(status, resp.getStatus());
        }
    }

    // ==================== Field Combinations ====================

    @Test
    void testWithoutOptionalFields() {
        UserProfileResponse resp = UserProfileResponse.builder()
            .id(testId)
            .username("user")
            .email("email@example.com")
            .build();

        assertEquals(testId, resp.getId());
        assertEquals("user", resp.getUsername());
        assertEquals("email@example.com", resp.getEmail());
        assertNull(resp.getRole());
        assertNull(resp.getStatus());
        assertNull(resp.getFullName());
        assertNull(resp.getBio());
        assertNull(resp.getLocation());
        assertNull(resp.getAvatarUrl());
        assertNull(resp.getSuccessfulTransactions());
    }

    @Test
    void testWithOnlyProfileFields() {
        UserProfileResponse resp = UserProfileResponse.builder()
            .fullName("Name")
            .bio("Bio")
            .location("Location")
            .avatarUrl("url")
            .build();

        assertNull(resp.getId());
        assertNull(resp.getUsername());
        assertEquals("Name", resp.getFullName());
        assertEquals("Bio", resp.getBio());
        assertEquals("Location", resp.getLocation());
        assertEquals("url", resp.getAvatarUrl());
    }

    @Test
    void testMultipleFieldUpdates() {
        response.setUsername("user1");
        assertEquals("user1", response.getUsername());

        response.setUsername("user2");
        assertEquals("user2", response.getUsername());

        response.setFullName("name1");
        assertEquals("name1", response.getFullName());

        response.setFullName("name2");
        assertEquals("name2", response.getFullName());
    }

    @Test
    void testSpecialCharactersInFields() {
        response.setUsername("user@domain.com");
        response.setEmail("email+tag@example.com");
        response.setFullName("User (Nickname)");
        response.setBio("Bio with @mentions and #hashtags");

        assertEquals("user@domain.com", response.getUsername());
        assertEquals("email+tag@example.com", response.getEmail());
        assertEquals("User (Nickname)", response.getFullName());
        assertEquals("Bio with @mentions and #hashtags", response.getBio());
    }

    @Test
    void testEmptyStrings() {
        response.setUsername("");
        response.setEmail("");
        response.setRole("");
        response.setStatus("");
        response.setFullName("");
        response.setBio("");

        assertEquals("", response.getUsername());
        assertEquals("", response.getEmail());
        assertEquals("", response.getRole());
        assertEquals("", response.getStatus());
        assertEquals("", response.getFullName());
        assertEquals("", response.getBio());
    }

    @Test
    void testLongValues() {
        String longString = "a".repeat(1000);
        response.setUsername(longString);
        response.setFullName(longString);
        response.setBio(longString);

        assertEquals(longString, response.getUsername());
        assertEquals(longString, response.getFullName());
        assertEquals(longString, response.getBio());
    }
}
