package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
}
