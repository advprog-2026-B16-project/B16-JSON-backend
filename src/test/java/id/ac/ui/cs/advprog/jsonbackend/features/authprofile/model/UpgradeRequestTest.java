package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeRequestTest {

    private UpgradeRequest upgradeRequest;
    private UUID requestId;
    private UUID userId;
    private User requester;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        userId = UUID.randomUUID();
        requester = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@example.com")
            .password("password")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        upgradeRequest = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(OffsetDateTime.now())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();
    }

    // ==================== Lombok @Data Methods ====================

    @Test
    void testToString() {
        String toString = upgradeRequest.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("UpgradeRequest") || toString.contains("PENDING"));
    }

    @Test
    void testEquals_SameObject() {
        assertEquals(upgradeRequest, upgradeRequest);
    }

    @Test
    void testEquals_EqualObjects() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_DifferentId() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(UUID.randomUUID())
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_DifferentCreatedAt() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(OffsetDateTime.now().minusDays(1))
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_DifferentRequester() {
        User differentUser = User.builder()
            .id(UUID.randomUUID())
            .username("differentuser")
            .email("different@example.com")
            .password("password")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(differentUser)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_DifferentFullName() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Different Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_DifferentCredential() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("different_credential")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_DifferentSocialMediaUrl() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("different_social_url")
            .status("PENDING")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_DifferentStatus() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("ACCEPTED")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testEquals_NullObject() {
        assertNotEquals(upgradeRequest, null);
    }

    @Test
    void testEquals_DifferentType() {
        assertNotEquals(upgradeRequest, "not an upgrade request");
    }

    @Test
    void testHashCode_ConsistentForEqualObjects() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertEquals(upgradeRequest.hashCode(), request2.hashCode());
    }

    @Test
    void testHashCode_DifferentForDifferentObjects() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(UUID.randomUUID())
            .createdAt(OffsetDateTime.now().minusDays(1))
            .requesterUser(User.builder().id(UUID.randomUUID()).build())
            .fullName("Different Name")
            .credential("different_credential")
            .socialMediaUrl("different_social")
            .status("ACCEPTED")
            .build();

        assertNotEquals(upgradeRequest.hashCode(), request2.hashCode());
    }

    // ==================== Lombok @Builder Methods ====================

    @Test
    void testBuilder_CreateCompleteUpgradeRequest() {
        OffsetDateTime now = OffsetDateTime.now();
        UpgradeRequest builtRequest = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(now)
            .requesterUser(requester)
            .fullName("Full Name")
            .credential("credential")
            .socialMediaUrl("social")
            .status("ACCEPTED")
            .build();

        assertEquals(requestId, builtRequest.getUpgrReqId());
        assertEquals(now, builtRequest.getCreatedAt());
        assertEquals(requester, builtRequest.getRequesterUser());
        assertEquals("Full Name", builtRequest.getFullName());
        assertEquals("credential", builtRequest.getCredential());
        assertEquals("social", builtRequest.getSocialMediaUrl());
        assertEquals("ACCEPTED", builtRequest.getStatus());
    }

    @Test
    void testBuilder_CreateWithMinimalFields() {
        UpgradeRequest builtRequest = UpgradeRequest.builder()
            .requesterUser(requester)
            .fullName("Minimal Name")
            .credential("cred")
            .build();

        assertNull(builtRequest.getUpgrReqId());
        assertNotNull(builtRequest.getCreatedAt());
        assertEquals(requester, builtRequest.getRequesterUser());
        assertEquals("Minimal Name", builtRequest.getFullName());
        assertEquals("PENDING", builtRequest.getStatus());
    }

    @Test
    void testBuilder_DefaultStatus() {
        UpgradeRequest builtRequest = UpgradeRequest.builder()
            .requesterUser(requester)
            .fullName("Test")
            .credential("cred")
            .build();

        assertEquals("PENDING", builtRequest.getStatus());
    }

    // ==================== Getters and Setters ====================

    @Test
    void testGettersAndSetters() {
        UUID newId = UUID.randomUUID();
        upgradeRequest.setUpgrReqId(newId);
        assertEquals(newId, upgradeRequest.getUpgrReqId());

        OffsetDateTime newTime = OffsetDateTime.now();
        upgradeRequest.setCreatedAt(newTime);
        assertEquals(newTime, upgradeRequest.getCreatedAt());

        User newRequester = User.builder()
            .id(UUID.randomUUID())
            .username("newuser")
            .email("new@example.com")
            .password("pass")
            .role(UserRole.JASTIPER)
            .status(UserStatus.ACTIVE)
            .build();
        upgradeRequest.setRequesterUser(newRequester);
        assertEquals(newRequester, upgradeRequest.getRequesterUser());

        upgradeRequest.setFullName("New Full Name");
        assertEquals("New Full Name", upgradeRequest.getFullName());

        upgradeRequest.setCredential("new_credential");
        assertEquals("new_credential", upgradeRequest.getCredential());

        upgradeRequest.setSocialMediaUrl("new_social_url");
        assertEquals("new_social_url", upgradeRequest.getSocialMediaUrl());

        upgradeRequest.setStatus("ACCEPTED");
        assertEquals("ACCEPTED", upgradeRequest.getStatus());
    }

    // ==================== No-Args Constructor ====================

    @Test
    void testNoArgsConstructor() {
        UpgradeRequest noArgsRequest = new UpgradeRequest();
        assertNotNull(noArgsRequest);
        assertNull(noArgsRequest.getUpgrReqId());
        assertNull(noArgsRequest.getRequesterUser());
    }

    // ==================== All-Args Constructor ====================

    @Test
    void testAllArgsConstructor() {
        OffsetDateTime now = OffsetDateTime.now();
        UpgradeRequest allArgsRequest = new UpgradeRequest(
            requestId,
            now,
            requester,
            "Full Name",
            "credential",
            "social_url",
            "PENDING"
        );

        assertEquals(requestId, allArgsRequest.getUpgrReqId());
        assertEquals(now, allArgsRequest.getCreatedAt());
        assertEquals(requester, allArgsRequest.getRequesterUser());
        assertEquals("Full Name", allArgsRequest.getFullName());
        assertEquals("credential", allArgsRequest.getCredential());
        assertEquals("social_url", allArgsRequest.getSocialMediaUrl());
        assertEquals("PENDING", allArgsRequest.getStatus());
    }

    // ==================== Default CreatedAt ====================

    @Test
    void testBuilderDefaultCreatedAt() {
        UpgradeRequest requestWithoutTime = UpgradeRequest.builder()
            .requesterUser(requester)
            .fullName("Test")
            .credential("cred")
            .build();

        assertNotNull(requestWithoutTime.getCreatedAt());
        assertTrue(requestWithoutTime.getCreatedAt().isBefore(OffsetDateTime.now().plusSeconds(1)));
    }

    // ==================== Field Combinations ====================

    @Test
    void testWithoutSocialMediaUrl() {
        UpgradeRequest request = UpgradeRequest.builder()
            .requesterUser(requester)
            .fullName("Test")
            .credential("cred")
            .build();

        assertNull(request.getSocialMediaUrl());
    }

    @Test
    void testDifferentStatuses() {
        String[] statuses = {"PENDING", "ACCEPTED", "REJECTED", "IN_REVIEW"};

        for (String status : statuses) {
            UpgradeRequest request = UpgradeRequest.builder()
                .requesterUser(requester)
                .fullName("Test")
                .credential("cred")
                .status(status)
                .build();

            assertEquals(status, request.getStatus());
        }
    }

    // ==================== Additional Comprehensive Equality Tests ====================

    @Test
    void testEquals_NullCreatedAt() {
        UpgradeRequest request1 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(null)
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(null)
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertEquals(request1, request2);
    }

    @Test
    void testEquals_NullSocialMediaUrl_Both() {
        UpgradeRequest request1 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl(null)
            .status("PENDING")
            .build();

        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl(null)
            .status("PENDING")
            .build();

        assertEquals(request1, request2);
    }

    @Test
    void testHashCode_WithNullSocialMediaUrl() {
        UpgradeRequest request1 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl(null)
            .status("PENDING")
            .build();

        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl(null)
            .status("PENDING")
            .build();

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEquals_AllFieldsPresent() {
        UpgradeRequest request1 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEquals_ReflexiveProperty() {
        assertEquals(upgradeRequest, upgradeRequest);
    }

    @Test
    void testEquals_SymmetricProperty() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertEquals(upgradeRequest, request2);
        assertEquals(request2, upgradeRequest);
    }

    @Test
    void testEquals_TransitiveProperty() {
        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        UpgradeRequest request3 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(requester)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertEquals(upgradeRequest, request2);
        assertEquals(request2, request3);
        assertEquals(upgradeRequest, request3);
    }

    @Test
    void testToString_NotNull() {
        assertNotNull(upgradeRequest.toString());
        assertTrue(upgradeRequest.toString().length() > 0);
    }

    @Test
    void testEqualsWithDifferentRequesterUserSameId() {
        User user2 = User.builder()
            .id(userId)
            .username("different")
            .email("different@example.com")
            .password("password")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        UpgradeRequest request2 = UpgradeRequest.builder()
            .upgrReqId(requestId)
            .createdAt(upgradeRequest.getCreatedAt())
            .requesterUser(user2)
            .fullName("Test User Full Name")
            .credential("credential_url")
            .socialMediaUrl("social_url")
            .status("PENDING")
            .build();

        assertNotEquals(upgradeRequest, request2);
    }

    @Test
    void testHashCodeConsistency() {
        int hash1 = upgradeRequest.hashCode();
        int hash2 = upgradeRequest.hashCode();
        assertEquals(hash1, hash2);
    }
}
