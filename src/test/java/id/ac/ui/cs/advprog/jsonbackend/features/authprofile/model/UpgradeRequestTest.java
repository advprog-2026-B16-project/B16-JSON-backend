package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model;

import org.junit.jupiter.api.Test; import java.util.UUID;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UpgradeRequestTest {

    @Test
    void testUpgradeRequestBruteForce() {
        UpgradeRequest r1 = new UpgradeRequest();
        UpgradeRequest r2 = new UpgradeRequest();
        
        r1.setUpgrReqId(UUID.randomUUID());
        r1.setCreatedAt(OffsetDateTime.now());
        r1.setRequesterUser(new User());
        r1.setFullName("name");
        r1.setCredential("cred");
        r1.setSocialMediaUrl("url");
        r1.setStatus("PENDING");

        assertEquals(r1, r1);
        assertNotEquals(r1, null);
        assertNotEquals(r1, new Object());
        
        r2.setUpgrReqId(r1.getUpgrReqId());
        r2.setCreatedAt(r1.getCreatedAt());
        r2.setRequesterUser(r1.getRequesterUser());
        r2.setFullName("name");
        r2.setCredential("cred");
        r2.setSocialMediaUrl("url");
        r2.setStatus("PENDING");
        
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertTrue(r1.canEqual(r2));
        
        r2.setUpgrReqId(UUID.randomUUID());
        assertNotEquals(r1, r2);
        
        UpgradeRequest b = UpgradeRequest.builder()
            .upgrReqId(UUID.randomUUID())
            .fullName("name")
            .build();
        assertNotNull(b.toString());
        
        UpgradeRequest all = new UpgradeRequest(UUID.randomUUID(), OffsetDateTime.now(), new User(), "name", "cred", "url", "PENDING");
        assertNotNull(all.getUpgrReqId());
    }
}