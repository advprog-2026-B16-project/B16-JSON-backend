package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeRequestSubmissionRequestTest {

    private UpgradeRequestSubmissionRequest request;

    @BeforeEach
    void setUp() {
        request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User")
            .credential("http://example.com/credential")
            .socialMediaUrl("http://example.com/social")
            .build();
    }

    // ==================== Builder Tests ====================

    @Test
    void testBuilder_CreateCompleteRequest() {
        UpgradeRequestSubmissionRequest built = UpgradeRequestSubmissionRequest.builder()
            .fullName("John Doe")
            .credential("cert_url")
            .socialMediaUrl("twitter_url")
            .build();

        assertEquals("John Doe", built.getFullName());
        assertEquals("cert_url", built.getCredential());
        assertEquals("twitter_url", built.getSocialMediaUrl());
    }

    @Test
    void testBuilder_CreateWithMinimalFields() {
        UpgradeRequestSubmissionRequest built = UpgradeRequestSubmissionRequest.builder()
            .fullName("Jane Doe")
            .credential("cert")
            .build();

        assertEquals("Jane Doe", built.getFullName());
        assertEquals("cert", built.getCredential());
        assertNull(built.getSocialMediaUrl());
    }

    @Test
    void testBuilder_AllNullFields() {
        UpgradeRequestSubmissionRequest built = UpgradeRequestSubmissionRequest.builder()
            .build();

        assertNull(built.getFullName());
        assertNull(built.getCredential());
        assertNull(built.getSocialMediaUrl());
    }

    // ==================== No-Args Constructor ====================

    @Test
    void testNoArgsConstructor() {
        UpgradeRequestSubmissionRequest noArgs = new UpgradeRequestSubmissionRequest();
        assertNotNull(noArgs);
        assertNull(noArgs.getFullName());
        assertNull(noArgs.getCredential());
        assertNull(noArgs.getSocialMediaUrl());
    }

    // ==================== All-Args Constructor ====================

    @Test
    void testAllArgsConstructor() {
        UpgradeRequestSubmissionRequest allArgs = new UpgradeRequestSubmissionRequest(
            "Full Name",
            "credential_url",
            "social_url"
        );

        assertEquals("Full Name", allArgs.getFullName());
        assertEquals("credential_url", allArgs.getCredential());
        assertEquals("social_url", allArgs.getSocialMediaUrl());
    }

    // ==================== Getters and Setters ====================

    @Test
    void testGettersAndSetters() {
        request.setFullName("New Name");
        assertEquals("New Name", request.getFullName());

        request.setCredential("new_cred");
        assertEquals("new_cred", request.getCredential());

        request.setSocialMediaUrl("new_social");
        assertEquals("new_social", request.getSocialMediaUrl());
    }

    @Test
    void testSetFullName_Null() {
        request.setFullName(null);
        assertNull(request.getFullName());
    }

    @Test
    void testSetCredential_Null() {
        request.setCredential(null);
        assertNull(request.getCredential());
    }

    @Test
    void testSetSocialMediaUrl_Null() {
        request.setSocialMediaUrl(null);
        assertNull(request.getSocialMediaUrl());
    }

    @Test
    void testSetFullName_Empty() {
        request.setFullName("");
        assertEquals("", request.getFullName());
    }

    @Test
    void testSetCredential_Empty() {
        request.setCredential("");
        assertEquals("", request.getCredential());
    }

    @Test
    void testSetSocialMediaUrl_Empty() {
        request.setSocialMediaUrl("");
        assertEquals("", request.getSocialMediaUrl());
    }

    // ==================== Lombok @Data Methods ====================

    @Test
    void testToString() {
        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("UpgradeRequestSubmissionRequest") ||
                   toString.contains("Test User"));
    }

    @Test
    void testEquals_SameObject() {
        assertEquals(request, request);
    }

    @Test
    void testEquals_EqualObjects() {
        UpgradeRequestSubmissionRequest request2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User")
            .credential("http://example.com/credential")
            .socialMediaUrl("http://example.com/social")
            .build();

        assertEquals(request, request2);
    }

    @Test
    void testEquals_DifferentFullName() {
        UpgradeRequestSubmissionRequest request2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Different User")
            .credential("http://example.com/credential")
            .socialMediaUrl("http://example.com/social")
            .build();

        assertNotEquals(request, request2);
    }

    @Test
    void testEquals_DifferentCredential() {
        UpgradeRequestSubmissionRequest request2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User")
            .credential("http://different.com/credential")
            .socialMediaUrl("http://example.com/social")
            .build();

        assertNotEquals(request, request2);
    }

    @Test
    void testEquals_DifferentSocialMediaUrl() {
        UpgradeRequestSubmissionRequest request2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User")
            .credential("http://example.com/credential")
            .socialMediaUrl("http://different.com/social")
            .build();

        assertNotEquals(request, request2);
    }

    @Test
    void testEquals_NullObject() {
        assertNotEquals(request, null);
    }

    @Test
    void testEquals_DifferentType() {
        assertNotEquals(request, "not an upgrade request");
    }

    @Test
    void testHashCode_ConsistentForEqualObjects() {
        UpgradeRequestSubmissionRequest request2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User")
            .credential("http://example.com/credential")
            .socialMediaUrl("http://example.com/social")
            .build();

        assertEquals(request.hashCode(), request2.hashCode());
    }

    @Test
    void testHashCode_DifferentForDifferentObjects() {
        UpgradeRequestSubmissionRequest request2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Different User")
            .credential("http://different.com/credential")
            .socialMediaUrl("http://different.com/social")
            .build();

        assertNotEquals(request.hashCode(), request2.hashCode());
    }

    // ==================== Field Combinations ====================

    @Test
    void testWithoutSocialMediaUrl() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("cred")
            .build();

        assertNull(req.getSocialMediaUrl());
        assertEquals("Name", req.getFullName());
        assertEquals("cred", req.getCredential());
    }

    @Test
    void testMultipleFieldUpdates() {
        request.setFullName("Name1");
        assertEquals("Name1", request.getFullName());

        request.setFullName("Name2");
        assertEquals("Name2", request.getFullName());

        request.setCredential("cred1");
        assertEquals("cred1", request.getCredential());

        request.setCredential("cred2");
        assertEquals("cred2", request.getCredential());
    }

    @Test
    void testSpecialCharactersInFields() {
        request.setFullName("User @#$%");
        request.setCredential("url?param=value&other=test");
        request.setSocialMediaUrl("twitter.com/user@handle");

        assertEquals("User @#$%", request.getFullName());
        assertEquals("url?param=value&other=test", request.getCredential());
        assertEquals("twitter.com/user@handle", request.getSocialMediaUrl());
    }

    @Test
    void testWhitespaceInFields() {
        request.setFullName("  Name with spaces  ");
        request.setCredential("  url with spaces  ");
        request.setSocialMediaUrl("  social url  ");

        assertEquals("  Name with spaces  ", request.getFullName());
        assertEquals("  url with spaces  ", request.getCredential());
        assertEquals("  social url  ", request.getSocialMediaUrl());
    }

    @Test
    void testLongValues() {
        String longString = "a".repeat(1000);
        request.setFullName(longString);
        request.setCredential(longString);
        request.setSocialMediaUrl(longString);

        assertEquals(longString, request.getFullName());
        assertEquals(longString, request.getCredential());
        assertEquals(longString, request.getSocialMediaUrl());
    }

    // ==================== Additional Edge Case Tests ====================

    @Test
    void testConstructorWithNullFullName() {
        UpgradeRequestSubmissionRequest req = new UpgradeRequestSubmissionRequest(null, "cred", "social");
        assertNull(req.getFullName());
        assertEquals("cred", req.getCredential());
        assertEquals("social", req.getSocialMediaUrl());
    }

    @Test
    void testConstructorWithNullCredential() {
        UpgradeRequestSubmissionRequest req = new UpgradeRequestSubmissionRequest("name", null, "social");
        assertEquals("name", req.getFullName());
        assertNull(req.getCredential());
        assertEquals("social", req.getSocialMediaUrl());
    }

    @Test
    void testConstructorWithNullSocialMediaUrl() {
        UpgradeRequestSubmissionRequest req = new UpgradeRequestSubmissionRequest("name", "cred", null);
        assertEquals("name", req.getFullName());
        assertEquals("cred", req.getCredential());
        assertNull(req.getSocialMediaUrl());
    }

    @Test
    void testConstructorWithAllNulls() {
        UpgradeRequestSubmissionRequest req = new UpgradeRequestSubmissionRequest(null, null, null);
        assertNull(req.getFullName());
        assertNull(req.getCredential());
        assertNull(req.getSocialMediaUrl());
    }

    @Test
    void testEqualsWithAllNullFields() {
        UpgradeRequestSubmissionRequest req1 = new UpgradeRequestSubmissionRequest();
        UpgradeRequestSubmissionRequest req2 = new UpgradeRequestSubmissionRequest();

        assertEquals(req1, req2);
    }

    @Test
    void testEqualsOneNullFullName() {
        UpgradeRequestSubmissionRequest req1 = UpgradeRequestSubmissionRequest.builder()
            .fullName(null)
            .credential("cred")
            .socialMediaUrl("social")
            .build();

        UpgradeRequestSubmissionRequest req2 = UpgradeRequestSubmissionRequest.builder()
            .fullName(null)
            .credential("cred")
            .socialMediaUrl("social")
            .build();

        assertEquals(req1, req2);
    }

    @Test
    void testEqualsOneNullCredential() {
        UpgradeRequestSubmissionRequest req1 = UpgradeRequestSubmissionRequest.builder()
            .fullName("name")
            .credential(null)
            .socialMediaUrl("social")
            .build();

        UpgradeRequestSubmissionRequest req2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("name")
            .credential(null)
            .socialMediaUrl("social")
            .build();

        assertEquals(req1, req2);
    }

    @Test
    void testEqualsOneNullSocialMediaUrl() {
        UpgradeRequestSubmissionRequest req1 = UpgradeRequestSubmissionRequest.builder()
            .fullName("name")
            .credential("cred")
            .socialMediaUrl(null)
            .build();

        UpgradeRequestSubmissionRequest req2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("name")
            .credential("cred")
            .socialMediaUrl(null)
            .build();

        assertEquals(req1, req2);
    }

    @Test
    void testNotEqualsWithOneNullField() {
        UpgradeRequestSubmissionRequest req1 = UpgradeRequestSubmissionRequest.builder()
            .fullName("name")
            .credential("cred")
            .socialMediaUrl("social")
            .build();

        UpgradeRequestSubmissionRequest req2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("name")
            .credential("cred")
            .socialMediaUrl(null)
            .build();

        assertNotEquals(req1, req2);
    }

    @Test
    void testHashCodeWithNullFields() {
        UpgradeRequestSubmissionRequest req1 = new UpgradeRequestSubmissionRequest(null, null, null);
        UpgradeRequestSubmissionRequest req2 = new UpgradeRequestSubmissionRequest(null, null, null);

        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void testToStringWithNullValues() {
        UpgradeRequestSubmissionRequest req = new UpgradeRequestSubmissionRequest(null, null, null);
        String toString = req.toString();
        assertNotNull(toString);
    }

    @Test
    void testBuilderWithOnlyFullName() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name Only")
            .build();

        assertEquals("Name Only", req.getFullName());
        assertNull(req.getCredential());
        assertNull(req.getSocialMediaUrl());
    }

    @Test
    void testBuilderWithOnlyCredential() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .credential("Credential Only")
            .build();

        assertNull(req.getFullName());
        assertEquals("Credential Only", req.getCredential());
        assertNull(req.getSocialMediaUrl());
    }

    @Test
    void testBuilderWithOnlySocialMediaUrl() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .socialMediaUrl("Social Only")
            .build();

        assertNull(req.getFullName());
        assertNull(req.getCredential());
        assertEquals("Social Only", req.getSocialMediaUrl());
    }

    @Test
    void testBuilderWithFullNameAndCredential() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("Credential")
            .build();

        assertEquals("Name", req.getFullName());
        assertEquals("Credential", req.getCredential());
        assertNull(req.getSocialMediaUrl());
    }

    @Test
    void testBuilderWithFullNameAndSocialMediaUrl() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .socialMediaUrl("Social")
            .build();

        assertEquals("Name", req.getFullName());
        assertNull(req.getCredential());
        assertEquals("Social", req.getSocialMediaUrl());
    }

    @Test
    void testBuilderWithCredentialAndSocialMediaUrl() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .credential("Credential")
            .socialMediaUrl("Social")
            .build();

        assertNull(req.getFullName());
        assertEquals("Credential", req.getCredential());
        assertEquals("Social", req.getSocialMediaUrl());
    }

    @Test
    void testSingleCharacterValues() {
        request.setFullName("A");
        request.setCredential("B");
        request.setSocialMediaUrl("C");

        assertEquals("A", request.getFullName());
        assertEquals("B", request.getCredential());
        assertEquals("C", request.getSocialMediaUrl());
    }

    @Test
    void testNumericStringValues() {
        request.setFullName("12345");
        request.setCredential("67890");
        request.setSocialMediaUrl("11111");

        assertEquals("12345", request.getFullName());
        assertEquals("67890", request.getCredential());
        assertEquals("11111", request.getSocialMediaUrl());
    }

    @Test
    void testMixedWhitespaceAndContent() {
        request.setFullName("  Name  ");
        request.setCredential("cred ");
        request.setSocialMediaUrl(" social");

        assertEquals("  Name  ", request.getFullName());
        assertEquals("cred ", request.getCredential());
        assertEquals(" social", request.getSocialMediaUrl());
    }

    @Test
    void testEqualsReflexivity() {
        UpgradeRequestSubmissionRequest req = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("Cred")
            .socialMediaUrl("Social")
            .build();

        assertEquals(req, req);
    }

    @Test
    void testEqualsSymmetry() {
        UpgradeRequestSubmissionRequest req1 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("Cred")
            .socialMediaUrl("Social")
            .build();

        UpgradeRequestSubmissionRequest req2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("Cred")
            .socialMediaUrl("Social")
            .build();

        assertEquals(req1, req2);
        assertEquals(req2, req1);
    }

    @Test
    void testEqualsTransitivity() {
        UpgradeRequestSubmissionRequest req1 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("Cred")
            .build();

        UpgradeRequestSubmissionRequest req2 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("Cred")
            .build();

        UpgradeRequestSubmissionRequest req3 = UpgradeRequestSubmissionRequest.builder()
            .fullName("Name")
            .credential("Cred")
            .build();

        assertEquals(req1, req2);
        assertEquals(req2, req3);
        assertEquals(req1, req3);
    }

    @Test
    void testSettersReturnVoid() {
        request.setFullName("Test");
        assertNotNull(request.getFullName());

        request.setCredential("Test");
        assertNotNull(request.getCredential());

        request.setSocialMediaUrl("Test");
        assertNotNull(request.getSocialMediaUrl());
    }
}
