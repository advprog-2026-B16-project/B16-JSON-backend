package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeRequestStatusChangeRequestTest {

    private UpgradeRequestStatusChangeRequest request;

    @BeforeEach
    void setUp() {
        request = new UpgradeRequestStatusChangeRequest();
    }

    @Test
    void testSettersAndGetters() {
        request.setUsername("testuser");
        assertEquals("testuser", request.getUsername());

        request.setNewStatus("ACCEPTED");
        assertEquals("ACCEPTED", request.getNewStatus());
    }

    @Test
    void testSetUsername_Null() {
        request.setUsername(null);
        assertNull(request.getUsername());
    }

    @Test
    void testSetNewStatus_Null() {
        request.setNewStatus(null);
        assertNull(request.getNewStatus());
    }

    @Test
    void testSetUsername_Empty() {
        request.setUsername("");
        assertEquals("", request.getUsername());
    }

    @Test
    void testSetNewStatus_Empty() {
        request.setNewStatus("");
        assertEquals("", request.getNewStatus());
    }

    @Test
    void testMultipleStatusValues() {
        String[] statuses = {"PENDING", "ACCEPTED", "REJECTED", "IN_REVIEW"};

        for (String status : statuses) {
            request.setNewStatus(status);
            assertEquals(status, request.getNewStatus());
        }
    }

    @Test
    void testGetUsername_BeforeSet() {
        assertNull(request.getUsername());
    }

    @Test
    void testGetNewStatus_BeforeSet() {
        assertNull(request.getNewStatus());
    }

    @Test
    void testSetUsername_MultipleUpdates() {
        request.setUsername("user1");
        assertEquals("user1", request.getUsername());

        request.setUsername("user2");
        assertEquals("user2", request.getUsername());
    }

    @Test
    void testSetNewStatus_MultipleUpdates() {
        request.setNewStatus("PENDING");
        assertEquals("PENDING", request.getNewStatus());

        request.setNewStatus("ACCEPTED");
        assertEquals("ACCEPTED", request.getNewStatus());
    }

    @Test
    void testChainingSetters() {
        request.setUsername("admin");
        request.setNewStatus("REJECTED");

        assertEquals("admin", request.getUsername());
        assertEquals("REJECTED", request.getNewStatus());
    }

    @Test
    void testSpecialCharactersInUsername() {
        request.setUsername("user@example.com");
        assertEquals("user@example.com", request.getUsername());
    }

    @Test
    void testWhitespaceInUsername() {
        request.setUsername("user name");
        assertEquals("user name", request.getUsername());
    }

    @Test
    void testLongUsername() {
        String longUsername = "a".repeat(1000);
        request.setUsername(longUsername);
        assertEquals(longUsername, request.getUsername());
    }
}
