package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeRequestTest {

    @Test
    void testUpgradeRequestAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        User user = new User();
        UpgradeRequest request = new UpgradeRequest(id, now, user, "Name", "Cred", "social", "PENDING");
        
        assertEquals(id, request.getUpgrReqId());
        assertEquals(now, request.getCreatedAt());
        assertEquals(user, request.getRequesterUser());
        assertEquals("Name", request.getFullName());
        assertEquals("Cred", request.getCredential());
        assertEquals("PENDING", request.getStatus());
    }

    @Test
    void testUpgradeRequestBuilder() {
        UUID id = UUID.randomUUID();
        User user = new User();
        OffsetDateTime now = OffsetDateTime.now();
        
        UpgradeRequest.UpgradeRequestBuilder builder = UpgradeRequest.builder();
        builder.upgrReqId(id);
        builder.createdAt(now);
        builder.requesterUser(user);
        builder.fullName("Name");
        builder.credential("Cred");
        builder.status("PENDING");
        assertNotNull(builder.toString());
        UpgradeRequest request = builder.build();
        
        assertEquals(id, request.getUpgrReqId());
        assertEquals(user, request.getRequesterUser());
        assertEquals(now, request.getCreatedAt());
    }

    @Test
    void testDefaultValues() {
        UpgradeRequest request = UpgradeRequest.builder().build();
        assertNotNull(request.getCreatedAt());
        assertEquals("PENDING", request.getStatus());
    }

    @Test
    void testGettersAndSetters() {
        UpgradeRequest request = new UpgradeRequest();
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        User user = new User();
        
        request.setUpgrReqId(id);
        request.setCreatedAt(now);
        request.setRequesterUser(user);
        request.setFullName("Name");
        request.setCredential("Cred");
        request.setStatus("ACCEPTED");

        assertEquals(id, request.getUpgrReqId());
        assertEquals(now, request.getCreatedAt());
        assertEquals(user, request.getRequesterUser());
        assertEquals("Name", request.getFullName());
        assertEquals("Cred", request.getCredential());
        assertEquals("ACCEPTED", request.getStatus());
    }
}
