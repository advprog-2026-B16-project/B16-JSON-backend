package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileUpdateRequestTest {

    private UserProfileUpdateRequest request;

    @BeforeEach
    void setUp() {
        request = UserProfileUpdateRequest.builder()
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .build();
    }

    // ==================== Builder Tests ====================

    @Test
    void testBuilder_CreateCompleteRequest() {
        UserProfileUpdateRequest built = UserProfileUpdateRequest.builder()
            .fullName("John Doe")
            .bio("Software Engineer")
            .location("San Francisco")
            .avatarUrl("http://example.com/john.jpg")
            .build();

        assertEquals("John Doe", built.getFullName());
        assertEquals("Software Engineer", built.getBio());
        assertEquals("San Francisco", built.getLocation());
        assertEquals("http://example.com/john.jpg", built.getAvatarUrl());
    }

    @Test
    void testBuilder_OnlyFullNameRequired() {
        UserProfileUpdateRequest built = UserProfileUpdateRequest.builder()
            .fullName("Jane Doe")
            .build();

        assertEquals("Jane Doe", built.getFullName());
        assertNull(built.getBio());
        assertNull(built.getLocation());
        assertNull(built.getAvatarUrl());
    }

    @Test
    void testBuilder_AllNullFields() {
        UserProfileUpdateRequest built = UserProfileUpdateRequest.builder()
            .build();

        assertNull(built.getFullName());
        assertNull(built.getBio());
        assertNull(built.getLocation());
        assertNull(built.getAvatarUrl());
    }

    @Test
    void testBuilder_WithPartialFields() {
        UserProfileUpdateRequest built = UserProfileUpdateRequest.builder()
            .fullName("User")
            .bio("Bio")
            .build();

        assertEquals("User", built.getFullName());
        assertEquals("Bio", built.getBio());
        assertNull(built.getLocation());
        assertNull(built.getAvatarUrl());
    }

    // ==================== No-Args Constructor ====================

    @Test
    void testNoArgsConstructor() {
        UserProfileUpdateRequest noArgs = new UserProfileUpdateRequest();
        assertNotNull(noArgs);
        assertNull(noArgs.getFullName());
        assertNull(noArgs.getBio());
    }

    // ==================== All-Args Constructor ====================

    @Test
    void testAllArgsConstructor() {
        UserProfileUpdateRequest allArgs = new UserProfileUpdateRequest(
            "Full Name",
            "Bio",
            "Location",
            "avatar_url"
        );

        assertEquals("Full Name", allArgs.getFullName());
        assertEquals("Bio", allArgs.getBio());
        assertEquals("Location", allArgs.getLocation());
        assertEquals("avatar_url", allArgs.getAvatarUrl());
    }

    // ==================== Getters and Setters ====================

    @Test
    void testGettersAndSetters() {
        request.setFullName("New Full Name");
        assertEquals("New Full Name", request.getFullName());

        request.setBio("New bio");
        assertEquals("New bio", request.getBio());

        request.setLocation("New Location");
        assertEquals("New Location", request.getLocation());

        request.setAvatarUrl("http://example.com/newavatar.jpg");
        assertEquals("http://example.com/newavatar.jpg", request.getAvatarUrl());
    }

    @Test
    void testSetFullName_Null() {
        request.setFullName(null);
        assertNull(request.getFullName());
    }

    @Test
    void testSetBio_Null() {
        request.setBio(null);
        assertNull(request.getBio());
    }

    @Test
    void testSetLocation_Null() {
        request.setLocation(null);
        assertNull(request.getLocation());
    }

    @Test
    void testSetAvatarUrl_Null() {
        request.setAvatarUrl(null);
        assertNull(request.getAvatarUrl());
    }

    @Test
    void testSetFullName_Empty() {
        request.setFullName("");
        assertEquals("", request.getFullName());
    }

    @Test
    void testSetBio_Empty() {
        request.setBio("");
        assertEquals("", request.getBio());
    }

    @Test
    void testSetLocation_Empty() {
        request.setLocation("");
        assertEquals("", request.getLocation());
    }

    @Test
    void testSetAvatarUrl_Empty() {
        request.setAvatarUrl("");
        assertEquals("", request.getAvatarUrl());
    }

    @Test
    void testGetFullName_BeforeSet() {
        UserProfileUpdateRequest newReq = new UserProfileUpdateRequest();
        assertNull(newReq.getFullName());
    }

    @Test
    void testGetBio_BeforeSet() {
        UserProfileUpdateRequest newReq = new UserProfileUpdateRequest();
        assertNull(newReq.getBio());
    }

    // ==================== Lombok @Builder Methods ====================

    @Test
    void testBuilder_MultipleInstancesAreIndependent() {
        UserProfileUpdateRequest req1 = UserProfileUpdateRequest.builder()
            .fullName("User1")
            .bio("Bio1")
            .build();

        UserProfileUpdateRequest req2 = UserProfileUpdateRequest.builder()
            .fullName("User2")
            .bio("Bio2")
            .build();

        assertEquals("User1", req1.getFullName());
        assertEquals("User2", req2.getFullName());
        assertEquals("Bio1", req1.getBio());
        assertEquals("Bio2", req2.getBio());
    }

    // ==================== Field Combinations ====================

    @Test
    void testWithoutOptionalFields() {
        UserProfileUpdateRequest req = UserProfileUpdateRequest.builder()
            .fullName("Name")
            .build();

        assertEquals("Name", req.getFullName());
        assertNull(req.getBio());
        assertNull(req.getLocation());
        assertNull(req.getAvatarUrl());
    }

    @Test
    void testWithAllOptionalFields() {
        UserProfileUpdateRequest req = UserProfileUpdateRequest.builder()
            .fullName("Name")
            .bio("Bio")
            .location("Location")
            .avatarUrl("url")
            .build();

        assertEquals("Name", req.getFullName());
        assertEquals("Bio", req.getBio());
        assertEquals("Location", req.getLocation());
        assertEquals("url", req.getAvatarUrl());
    }

    @Test
    void testMultipleFieldUpdates() {
        request.setFullName("name1");
        assertEquals("name1", request.getFullName());

        request.setFullName("name2");
        assertEquals("name2", request.getFullName());

        request.setBio("bio1");
        assertEquals("bio1", request.getBio());

        request.setBio("bio2");
        assertEquals("bio2", request.getBio());
    }

    @Test
    void testSpecialCharactersInFields() {
        request.setFullName("User (Nickname)");
        request.setBio("Bio with @mentions and #hashtags");
        request.setLocation("San Francisco, CA");
        request.setAvatarUrl("http://example.com/avatar?size=large&format=jpg");

        assertEquals("User (Nickname)", request.getFullName());
        assertEquals("Bio with @mentions and #hashtags", request.getBio());
        assertEquals("San Francisco, CA", request.getLocation());
        assertEquals("http://example.com/avatar?size=large&format=jpg", request.getAvatarUrl());
    }

    @Test
    void testWhitespaceInFields() {
        request.setFullName("  Name with spaces  ");
        request.setBio("  Bio with spaces  ");
        request.setLocation("  Location with spaces  ");

        assertEquals("  Name with spaces  ", request.getFullName());
        assertEquals("  Bio with spaces  ", request.getBio());
        assertEquals("  Location with spaces  ", request.getLocation());
    }

    @Test
    void testLongValues() {
        String longString = "a".repeat(1000);
        request.setFullName(longString);
        request.setBio(longString);
        request.setLocation(longString);
        request.setAvatarUrl(longString);

        assertEquals(longString, request.getFullName());
        assertEquals(longString, request.getBio());
        assertEquals(longString, request.getLocation());
        assertEquals(longString, request.getAvatarUrl());
    }

    @Test
    void testUnicodeCharactersInFields() {
        request.setFullName("用户名");
        request.setBio("生物信息");
        request.setLocation("北京");

        assertEquals("用户名", request.getFullName());
        assertEquals("生物信息", request.getBio());
        assertEquals("北京", request.getLocation());
    }

    @Test
    void testURLFormatsInAvatarUrl() {
        String[] urls = {
            "http://example.com/avatar.jpg",
            "https://secure.example.com/avatar.png",
            "data:image/png;base64,ABC123==",
            "/relative/path/avatar.jpg"
        };

        for (String url : urls) {
            request.setAvatarUrl(url);
            assertEquals(url, request.getAvatarUrl());
        }
    }

    @Test
    void testBuilderReuse() {
        var builder = UserProfileUpdateRequest.builder()
            .fullName("Base Name")
            .bio("Base Bio");

        UserProfileUpdateRequest req1 = builder
            .location("Location1")
            .build();

        UserProfileUpdateRequest req2 = builder
            .location("Location2")
            .build();

        assertEquals("Base Name", req1.getFullName());
        assertEquals("Location1", req1.getLocation());

        assertEquals("Base Name", req2.getFullName());
        assertEquals("Location2", req2.getLocation());
    }
}
