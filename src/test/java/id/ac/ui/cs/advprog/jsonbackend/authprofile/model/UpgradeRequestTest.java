package id.ac.ui.cs.advprog.jsonbackend.authprofile.model;

import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UpgradeRequestTest {

    @Test
    void testUpgradeRequestGettersSetters() {
        UpgradeRequest ur = new UpgradeRequest();
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        User user = new User();
        user.setUsername("requester");

        ur.setId(id);
        ur.setCreatedAt(now);
        ur.setRequesterUser(user);
        ur.setFullName("Full Name");
        ur.setCredential("Credential");
        ur.setStatus("PENDING");

        assertEquals(id, ur.getId());
        assertEquals(now, ur.getCreatedAt());
        assertEquals(user, ur.getRequesterUser());
        assertEquals("Full Name", ur.getFullName());
        assertEquals("Credential", ur.getCredential());
        assertEquals("PENDING", ur.getStatus());
    }

    @Test
    void testUserRoleEnum() {
        assertNotNull(UserRole.valueOf("TITIPER"));
        assertNotNull(UserRole.valueOf("JASTIPER"));
        assertNotNull(UserRole.valueOf("ADMIN"));
        assertEquals(3, UserRole.values().length);
    }

    @Test
    void testUserStatusEnum() {
        assertNotNull(UserStatus.valueOf("ACTIVE"));
        assertNotNull(UserStatus.valueOf("PENDING_JASTIPER"));
        assertNotNull(UserStatus.valueOf("BANNED"));
        assertEquals(3, UserStatus.values().length);
    }
}
