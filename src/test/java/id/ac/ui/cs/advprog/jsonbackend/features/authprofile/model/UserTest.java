package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        user = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();
    }

    // ==================== UserDetails Interface Methods ====================

    @Test
    void testGetAuthorities_Titiper() {
        user.setRole(UserRole.TITIPER);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TITIPER")));
    }

    @Test
    void testGetAuthorities_Jastiper() {
        user.setRole(UserRole.JASTIPER);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_JASTIPER")));
    }

    @Test
    void testGetAuthorities_Admin() {
        user.setRole(UserRole.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked_WhenActive() {
        user.setStatus(UserStatus.ACTIVE);
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsAccountNonLocked_WhenPendingJastiper() {
        user.setStatus(UserStatus.PENDING_JASTIPER);
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsAccountNonLocked_WhenBanned() {
        user.setStatus(UserStatus.BANNED);
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", user.getUsername());
    }

    // ==================== Lombok @Data Methods ====================

    @Test
    void testToString() {
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("User") || toString.contains("testuser"));
    }

    @Test
    void testEquals_SameObject() {
        assertEquals(user, user);
    }

    @Test
    void testEquals_EqualObjects() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertEquals(user, user2);
    }

    @Test
    void testEquals_DifferentId() {
        User user2 = User.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentUsername() {
        User user2 = User.builder()
            .id(testId)
            .username("differentuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentEmail() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("different@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentPassword() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("differentPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentRole() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.JASTIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentStatus() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.BANNED)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentFullName() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Different Name")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentBio() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Different bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentLocation() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Different Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentAvatarUrl() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://different.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_DifferentJastiperProfile() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("different_profile_url")
            .build();

        assertNotEquals(user, user2);
    }

    @Test
    void testEquals_NullObject() {
        assertNotEquals(user, null);
    }

    @Test
    void testEquals_DifferentType() {
        assertNotEquals(user, "not a user");
    }

    @Test
    void testHashCode_ConsistentForEqualObjects() {
        User user2 = User.builder()
            .id(testId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .bio("Test bio")
            .location("Test Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .jastiperProfile("profile_url")
            .build();

        assertEquals(user.hashCode(), user2.hashCode());
    }

    @Test
    void testHashCode_DifferentForDifferentObjects() {
        User user2 = User.builder()
            .id(UUID.randomUUID())
            .username("differentuser")
            .email("different@example.com")
            .password("hashedPassword")
            .role(UserRole.JASTIPER)
            .status(UserStatus.BANNED)
            .fullName("Different Name")
            .bio("Different bio")
            .location("Different Location")
            .avatarUrl("http://different.com/avatar.jpg")
            .jastiperProfile("different_profile")
            .build();

        assertNotEquals(user.hashCode(), user2.hashCode());
    }

    // ==================== Lombok @Builder Methods ====================

    @Test
    void testBuilder_CreateCompleteUser() {
        User builtUser = User.builder()
            .id(testId)
            .username("builderuser")
            .email("builder@example.com")
            .password("password")
            .role(UserRole.ADMIN)
            .status(UserStatus.ACTIVE)
            .fullName("Builder User")
            .build();

        assertEquals(testId, builtUser.getId());
        assertEquals("builderuser", builtUser.getUsername());
        assertEquals("builder@example.com", builtUser.getEmail());
        assertEquals(UserRole.ADMIN, builtUser.getRole());
        assertEquals(UserStatus.ACTIVE, builtUser.getStatus());
    }

    @Test
    void testBuilder_CreateUserWithMinimalFields() {
        User builtUser = User.builder()
            .username("minimal")
            .email("minimal@example.com")
            .password("pass")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        assertEquals("minimal", builtUser.getUsername());
        assertEquals("minimal@example.com", builtUser.getEmail());
        assertNull(builtUser.getId());
    }

    // ==================== Getters and Setters ====================

    @Test
    void testGettersAndSetters() {
        UUID newId = UUID.randomUUID();
        user.setId(newId);
        assertEquals(newId, user.getId());

        user.setUsername("newusername");
        assertEquals("newusername", user.getUsername());

        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());

        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());

        user.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, user.getRole());

        user.setStatus(UserStatus.BANNED);
        assertEquals(UserStatus.BANNED, user.getStatus());

        user.setFullName("New Full Name");
        assertEquals("New Full Name", user.getFullName());

        user.setBio("New bio");
        assertEquals("New bio", user.getBio());

        user.setLocation("New Location");
        assertEquals("New Location", user.getLocation());

        user.setAvatarUrl("http://example.com/newavatar.jpg");
        assertEquals("http://example.com/newavatar.jpg", user.getAvatarUrl());

        user.setJastiperProfile("new_profile_url");
        assertEquals("new_profile_url", user.getJastiperProfile());
    }

    // ==================== No-Args Constructor ====================

    @Test
    void testNoArgsConstructor() {
        User noArgsUser = new User();
        assertNotNull(noArgsUser);
        assertNull(noArgsUser.getId());
        assertNull(noArgsUser.getUsername());
    }

    // ==================== All-Args Constructor ====================

    @Test
    void testAllArgsConstructor() {
        User allArgsUser = new User(
            testId,
            "username",
            "email@example.com",
            "password",
            UserRole.TITIPER,
            UserStatus.ACTIVE,
            "Full Name",
            "Bio",
            "Location",
            "avatar_url",
            "profile_url"
        );

        assertEquals(testId, allArgsUser.getId());
        assertEquals("username", allArgsUser.getUsername());
        assertEquals("email@example.com", allArgsUser.getEmail());
        assertEquals("password", allArgsUser.getPassword());
        assertEquals(UserRole.TITIPER, allArgsUser.getRole());
        assertEquals(UserStatus.ACTIVE, allArgsUser.getStatus());
        assertEquals("Full Name", allArgsUser.getFullName());
        assertEquals("Bio", allArgsUser.getBio());
        assertEquals("Location", allArgsUser.getLocation());
        assertEquals("avatar_url", allArgsUser.getAvatarUrl());
        assertEquals("profile_url", allArgsUser.getJastiperProfile());
    }
}
