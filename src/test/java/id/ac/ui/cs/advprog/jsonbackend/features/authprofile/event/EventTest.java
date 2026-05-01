package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    @Test
    void testUserCreatedEvent() {
        UserCreatedEvent event = new UserCreatedEvent("user123");
        assertEquals("user123", event.getUserId());
    }
}
