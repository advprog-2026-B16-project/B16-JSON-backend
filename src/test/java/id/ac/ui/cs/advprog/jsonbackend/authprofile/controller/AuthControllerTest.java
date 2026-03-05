package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.WrongPasswordException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.LoginService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private LoginController loginController;

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLoginInfo() {
        ResponseEntity<?> response = loginController.getLoginInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Map) response.getBody()).containsKey("message"));
    }

    @Test
    void testLoginUserSuccess() {
        UserLoginRequest request = new UserLoginRequest();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(loginService.login(request)).thenReturn(new User());

        ResponseEntity<?> response = loginController.loginUser(request, bindingResult);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testLoginUserValidationError() {
        UserLoginRequest request = new UserLoginRequest();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("request", "email", "Email required")));

        ResponseEntity<?> response = loginController.loginUser(request, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLoginUserWrongPassword() {
        UserLoginRequest request = new UserLoginRequest();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(loginService.login(request)).thenThrow(new WrongPasswordException("Wrong password"));

        ResponseEntity<?> response = loginController.loginUser(request, bindingResult);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testLoginUserNotFound() {
        UserLoginRequest request = new UserLoginRequest();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(loginService.login(request)).thenThrow(new UserNotFoundException("Not found"));

        ResponseEntity<?> response = loginController.loginUser(request, bindingResult);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    
    @Test
    void testLoginUserGeneralException() {
        UserLoginRequest request = new UserLoginRequest();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(loginService.login(request)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = loginController.loginUser(request, bindingResult);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetRegistrationInfo() {
        ResponseEntity<?> response = registrationController.getRegistrationInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRegisterUserSuccess() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);
        when(request.passwordConfirmationMathces()).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = registrationController.registerUser(request, bindingResult);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(registrationService).register(request);
    }

    @Test
    void testRegisterUserPasswordMismatch() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);
        when(request.passwordConfirmationMathces()).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = registrationController.registerUser(request, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRegisterUserValidationError() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("request", "username", "Required")));

        ResponseEntity<?> response = registrationController.registerUser(request, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRegisterUserException() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);
        when(request.passwordConfirmationMathces()).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Error")).when(registrationService).register(request);

        ResponseEntity<?> response = registrationController.registerUser(request, bindingResult);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
