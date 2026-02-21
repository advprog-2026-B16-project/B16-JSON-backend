package id.ac.ui.cs.advprog.jsonbackend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testDefaultUsernameIsNull() {
        assertNull(user.getUsername());
    }

    @Test
    void testDefaultEmailIsNull() {
        assertNull(user.getEmail());
    }

    @Test
    void testDefaultRoleIsNull() {
        assertNull(user.getRole());
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testSetAndGetEmail() {
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testSetAndGetPassword() {
        user.setPassword("password123");
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetAndGetRole() {
        user.setRole(UserRole.TITIPER);
        assertEquals(UserRole.TITIPER, user.getRole());
    }

    @Test
    void testSetAndGetStatus() {
        user.setStatus(UserStatus.ACTIVE);
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void testSetAndGetJastiperProfile() {
        user.setJastiper_profile("Test Bio");
        assertEquals("Test Bio", user.getJastiper_profile());
    }

    @Test
    void testConstructorWithParameters() {
        User userWithParams = new User("jane", "jane@example.com", "pass123", UserRole.JASTIPER, UserStatus.PENDING_JASTIPER);

        assertEquals("jane", userWithParams.getUsername());
        assertEquals("jane@example.com", userWithParams.getEmail());
        assertEquals("pass123", userWithParams.getPassword());
        assertEquals(UserRole.JASTIPER, userWithParams.getRole());
        assertEquals(UserStatus.PENDING_JASTIPER, userWithParams.getStatus());
    }

    @Test
    void testToString() {
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        assertEquals("User [username=john, email=john@example.com, role=ADMIN, status=ACTIVE]", user.toString());
    }

}
