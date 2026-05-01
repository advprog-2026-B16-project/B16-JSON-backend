package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserProfileUpdateRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = User.builder()
                .username("john")
                .email("john@example.com")
                .password("pass1")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        user2 = User.builder()
                .username("jane")
                .email("jane@example.com")
                .password("pass2")
                .role(UserRole.JASTIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
    }

    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user1));
        Optional<User> result = userService.getUserByUsername("john");
        assertEquals(user1, result.get());
    }

    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user1));
        Optional<User> result = userService.getUserByEmail("john@example.com");
        assertEquals(user1, result.get());
    }

    @Test
    void testGetUserById() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        Optional<User> result = userService.getUserById(id);
        assertEquals(user1, result.get());
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(user1)).thenReturn(user1);
        User result = userService.saveUser(user1);
        assertEquals(user1, result);
    }

    @Test
    void testPromoteToJastiper() {
        userService.promoteToJastiper(user1);
        assertEquals(UserRole.JASTIPER, user1.getRole());
        verify(userRepository).save(user1);
    }

    @Test
    void testBanUser() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user2));
        userService.banUser(id);
        assertEquals(UserStatus.BANNED, user2.getStatus());
        verify(userRepository).save(user2);
    }

    @Test
    void testDemoteUser() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user2));
        userService.demoteUser(id);
        assertEquals(UserRole.TITIPER, user2.getRole());
        verify(userRepository).save(user2);
    }

    @Test
    void testBanAdminShouldNotChangeStatus() {
        user1.setRole(UserRole.ADMIN);
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        userService.banUser(id);
        assertEquals(UserStatus.ACTIVE, user1.getStatus());
        verify(userRepository, never()).save(user1);
    }

    @Test
    void testDemoteNonJastiperShouldNotChangeRole() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        userService.demoteUser(id);
        assertEquals(UserRole.TITIPER, user1.getRole());
        verify(userRepository, never()).save(user1);
    }

    @Test
    void testBanUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        userService.banUser(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDemoteUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        userService.demoteUser(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateProfile() {
        UUID id = UUID.randomUUID();
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("New Name")
                .bio("New Bio")
                .location("New Location")
                .avatarUrl("New Avatar")
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));
        
        userService.updateProfile(id, request);
        
        assertEquals("New Name", user1.getFullName());
        assertEquals("New Bio", user1.getBio());
        assertEquals("New Location", user1.getLocation());
        assertEquals("New Avatar", user1.getAvatarUrl());
        verify(userRepository).save(user1);
    }

    @Test
    void testUpdateProfileUserNotFound() {
        UUID id = UUID.randomUUID();
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("New Name")
                .build();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        
        userService.updateProfile(id, request);
        
        verify(userRepository, never()).save(any());
    }
}
