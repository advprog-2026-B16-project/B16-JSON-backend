package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.common.config.LoginAttemptService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .build();

        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);

        User result = loginService.login(request);
        assertEquals(user, result);
        verify(loginAttemptService).loginSucceeded("test@example.com");
    }

    @Test
    void testLoginUserNotFound() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password123");

        when(loginAttemptService.isBlocked("notfound@example.com")).thenReturn(false);
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> loginService.login(request));
        verify(loginAttemptService).loginFailed("notfound@example.com");
    }

    @Test
    void testLoginWrongPassword() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpass");

        User user = User.builder()
                .email("test@example.com")
                .password("encoded_password")
                .build();

        when(loginAttemptService.isBlocked("test@example.com")).thenReturn(false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded_password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> loginService.login(request));
        verify(loginAttemptService).loginFailed("test@example.com");
    }

    @Test
    void testLoginBlocked() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("blocked@example.com");
        
        when(loginAttemptService.isBlocked("blocked@example.com")).thenReturn(true);
        
        assertThrows(BadCredentialsException.class, () -> loginService.login(request));
    }
}
