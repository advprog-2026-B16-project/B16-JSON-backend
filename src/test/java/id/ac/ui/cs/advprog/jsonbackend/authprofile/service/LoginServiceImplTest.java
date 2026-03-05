package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.WrongPasswordException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginServiceImpl(userRepository, passwordEncoder);
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

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);

        User result = loginService.login(request);
        assertEquals(user, result);
    }

    @Test
    void testLoginUserNotFound() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> loginService.login(request));
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

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded_password")).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> loginService.login(request));
    }
}
