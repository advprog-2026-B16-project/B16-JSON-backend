package id.ac.ui.cs.advprog.jsonbackend.authprofile.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "user", "email", "pass", UserRole.TITIPER, UserStatus.ACTIVE, "profile");
        assertEquals(id, user.getId());
        assertEquals("user", user.getUsername());
        assertEquals("email", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals(UserRole.TITIPER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals("profile", user.getJastiperProfile());
    }

    @Test
    void testUserBuilder() {
        UUID id = UUID.randomUUID();
        // Call EVERY builder method explicitly
        User.UserBuilder builder = User.builder();
        builder.id(id);
        builder.username("user");
        builder.email("email");
        builder.password("pass");
        builder.role(UserRole.TITIPER);
        builder.status(UserStatus.ACTIVE);
        builder.jastiperProfile("profile");
        assertNotNull(builder.toString());
        User user = builder.build();
        
        assertEquals(id, user.getId());
        assertEquals("user", user.getUsername());
        assertTrue(user.toString().contains("user"));
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setUsername("user");
        user.setEmail("email");
        user.setPassword("pass");
        user.setRole(UserRole.TITIPER);
        user.setStatus(UserStatus.ACTIVE);
        user.setJastiperProfile("profile");

        assertEquals(id, user.getId());
        assertEquals("user", user.getUsername());
        assertEquals("email", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals(UserRole.TITIPER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals("profile", user.getJastiperProfile());
    }

    @Test
    void testUserRoleEnum() {
        for (UserRole role : UserRole.values()) {
            assertEquals(role, UserRole.valueOf(role.name()));
        }
    }

    @Test
    void testUserStatusEnum() {
        for (UserStatus status : UserStatus.values()) {
            assertEquals(status, UserStatus.valueOf(status.name()));
        }
    }
}
