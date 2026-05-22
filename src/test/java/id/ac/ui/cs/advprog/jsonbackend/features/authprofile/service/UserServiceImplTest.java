package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserProfileUpdateRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;
    private UUID userId;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);

        userId = UUID.randomUUID();
        testUser = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByUsername_Found() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByUsername("nonexistent");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testGetUserByEmail_Found() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("missing@example.com");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("missing@example.com");
    }

    @Test
    void testGetUserById_Found() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        UUID nonexistentId = UUID.randomUUID();
        when(userRepository.findById(nonexistentId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(nonexistentId);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(nonexistentId);
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.saveUser(testUser);

        assertEquals(testUser.getId(), result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testPromoteToJastiper() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.promoteToJastiper(testUser);

        assertEquals(UserRole.JASTIPER, testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testBanUser_Success() {
        User titiperUser = User.builder()
            .id(userId)
            .username("titiper")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(titiperUser));
        when(userRepository.save(any(User.class))).thenReturn(titiperUser);

        userService.banUser(userId);

        assertEquals(UserStatus.BANNED, titiperUser.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(titiperUser);
    }

    @Test
    void testBanUser_CannotBanAdmin() {
        User adminUser = User.builder()
            .id(userId)
            .username("admin")
            .role(UserRole.ADMIN)
            .status(UserStatus.ACTIVE)
            .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

        userService.banUser(userId);

        assertEquals(UserStatus.ACTIVE, adminUser.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(adminUser);
    }

    @Test
    void testBanUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userService.banUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDemoteUser_Success() {
        User jastiperUser = User.builder()
            .id(userId)
            .username("jastiper")
            .role(UserRole.JASTIPER)
            .status(UserStatus.ACTIVE)
            .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(jastiperUser));
        when(userRepository.save(any(User.class))).thenReturn(jastiperUser);

        userService.demoteUser(userId);

        assertEquals(UserRole.TITIPER, jastiperUser.getRole());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(jastiperUser);
    }

    @Test
    void testDemoteUser_CannotDemoteTitiper() {
        User titiperUser = User.builder()
            .id(userId)
            .username("titiper")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(titiperUser));

        userService.demoteUser(userId);

        assertEquals(UserRole.TITIPER, titiperUser.getRole());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDemoteUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userService.demoteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateProfile() {
        UserProfileUpdateRequest updateRequest = UserProfileUpdateRequest.builder()
            .fullName("Updated Name")
            .bio("Updated Bio")
            .location("Updated Location")
            .avatarUrl("http://example.com/avatar.jpg")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateProfile(userId, updateRequest);

        assertEquals("Updated Name", testUser.getFullName());
        assertEquals("Updated Bio", testUser.getBio());
        assertEquals("Updated Location", testUser.getLocation());
        assertEquals("http://example.com/avatar.jpg", testUser.getAvatarUrl());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        UserProfileUpdateRequest updateRequest = UserProfileUpdateRequest.builder()
            .fullName("Updated Name")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userService.updateProfile(userId, updateRequest);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }
}
