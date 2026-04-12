package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.WrongPasswordException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.LoginService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.RegistrationService;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private RegistrationService registrationService;

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
                .username("test")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
        
        when(loginService.login(any())).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("token123");
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = loginController.loginUser(request, result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLoginUserBindingErrors() {
        UserLoginRequest request = new UserLoginRequest();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("request", "email", "Email required")));

        ResponseEntity<?> response = loginController.loginUser(request, result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLoginUserBadCredentials() {
        UserLoginRequest request = new UserLoginRequest();
        when(loginService.login(any())).thenThrow(new BadCredentialsException("Invalid credentials"));
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = loginController.loginUser(request, result);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testLoginUserGeneralException() {
        UserLoginRequest request = new UserLoginRequest();
        when(loginService.login(any())).thenThrow(new RuntimeException("Error"));
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = loginController.loginUser(request, result);
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
        request.setPassword("pass123456");
        request.setConfirmPassword("pass123456");
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
        request.setPassword("pass123");
        request.setConfirmPassword("pass456");
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = registrationController.registerUser(request, result);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRegisterUserConflict() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPassword("pass123");
        request.setConfirmPassword("pass123");
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Conflict")).when(registrationService).register(any());

        ResponseEntity<?> response = registrationController.registerUser(request, result);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
