package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.event.UserLoggedInEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.common.config.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private LoginServiceImpl loginService;
    private User testUser;
    private UserLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginServiceImpl(userRepository, passwordEncoder, loginAttemptService, eventPublisher);

        testUser = User.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .build();

        loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void testLogin_Success() {
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        User result = loginService.login(loginRequest);

        assertEquals(testUser.getId(), result.getId());
        assertEquals("testuser", result.getUsername());
        verify(loginAttemptService, times(1)).loginSucceeded("test@example.com");
        verify(loginAttemptService, never()).loginFailed(anyString());
        verify(eventPublisher).publishEvent(any(UserLoggedInEvent.class));
    }

    @Test
    void testLogin_AccountBlocked() {
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(true);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            loginService.login(loginRequest);
        });

        assertTrue(exception.getMessage().contains("Account is locked"));
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testLogin_UserNotFound() {
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            loginService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(loginAttemptService, times(1)).loginFailed("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testLogin_InvalidPassword() {
        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            loginService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(loginAttemptService, times(1)).loginFailed("test@example.com");
        verify(loginAttemptService, never()).loginSucceeded(anyString());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
