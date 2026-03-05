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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginServiceImpl(userRepository);
    }

    @Test
    void testLoginSuccess() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

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

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(WrongPasswordException.class, () -> loginService.login(request));
    }
}
