package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoCoverageTest {

    @Test
    void testUpgradeRequestResponseBranches() {
        // 1. null request
        assertNull(UpgradeRequestResponse.fromRequest(null));

        // 2. null requester user
        UpgradeRequest r1 = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID())
                .requesterUser(null)
                .build();
        UpgradeRequestResponse res1 = UpgradeRequestResponse.fromRequest(r1);
        assertEquals("unknown", res1.requesterUserId());
        assertEquals("unknown", res1.requesterUsername());

        // 3. null user ID (not possible in practice due to UUID.randomUUID but let's test if manually set)
        User u2 = new User();
        u2.setUsername("u2");
        UpgradeRequest r2 = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID())
                .requesterUser(u2)
                .build();
        UpgradeRequestResponse res2 = UpgradeRequestResponse.fromRequest(r2);
        assertEquals("unknown", res2.requesterUserId());
        assertEquals("u2", res2.requesterUsername()); assertEquals(null, res2.socialMediaUrl());
    }

    @Test
    void testUserProfileResponseSetters() {
        UserProfileResponse res = new UserProfileResponse();
        UUID id = UUID.randomUUID();
        res.setId(id);
        res.setUsername("u");
        res.setEmail("e");
        res.setRole("r");
        res.setStatus("s");
        res.setFullName("f");
        res.setBio("b");
        res.setLocation("l");
        res.setAvatarUrl("a");
        res.setSuccessfulTransactions(1L);

        assertEquals(id, res.getId());
        assertEquals("u", res.getUsername());
        
        // Call with nulls for branch coverage
        res.setId(null);
        assertNull(res.getId());
    }
}
