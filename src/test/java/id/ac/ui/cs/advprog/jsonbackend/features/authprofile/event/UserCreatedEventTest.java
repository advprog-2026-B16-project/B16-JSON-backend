package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserCreatedEventTest {

    @Test
    void testUserCreatedEvent() {
        UserCreatedEvent event = new UserCreatedEvent("user-123");
        assertEquals("user-123", event.getUserId());
    }
}
