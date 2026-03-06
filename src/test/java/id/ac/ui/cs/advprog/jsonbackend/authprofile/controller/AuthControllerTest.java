package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.config.JwtService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.LoginAttemptService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.LoginService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginController loginController;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLoginInfo() {
        ResponseEntity<?> response = loginController.getLoginInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLoginUserSuccess() {
        UserLoginRequest request = new UserLoginRequest();
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        
        when(loginService.login(any())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mockToken");
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = loginController.loginUser(request, result, httpServletRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLoginUserBlocked() {
        UserLoginRequest request = new UserLoginRequest();
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(loginAttemptService.isBlocked(anyString())).thenReturn(true);
        
        BindingResult result = mock(BindingResult.class);

        ResponseEntity<?> response = loginController.loginUser(request, result, httpServletRequest);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
    }

    @Test
    void testLoginUserBindingErrors() {
        UserLoginRequest request = new UserLoginRequest();
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("request", "email", "Email required")));

        ResponseEntity<?> response = loginController.loginUser(request, result, httpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLoginUserBadCredentials() {
        UserLoginRequest request = new UserLoginRequest();
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        
        when(loginService.login(any())).thenThrow(new BadCredentialsException("Invalid email or password"));
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = loginController.loginUser(request, result, httpServletRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(loginAttemptService).loginFailed(anyString());
    }

    @Test
    void testLoginUserGeneralException() {
        UserLoginRequest request = new UserLoginRequest();
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        
        when(loginService.login(any())).thenThrow(new RuntimeException("Error"));
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = loginController.loginUser(request, result, httpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetRegistrationInfo() {
        ResponseEntity<?> response = registrationController.getRegistrationInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRegisterUserSuccess() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = registrationController.registerUser(request, result);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testRegisterUserBindingErrors() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("request", "username", "Username required")));

        ResponseEntity<?> response = registrationController.registerUser(request, result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRegisterUserPasswordMismatch() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPassword("Password123!");
        request.setConfirmPassword("Different123!");
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = registrationController.registerUser(request, result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRegisterUserConflict() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Conflict")).when(registrationService).register(any());

        ResponseEntity<?> response = registrationController.registerUser(request, result);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
