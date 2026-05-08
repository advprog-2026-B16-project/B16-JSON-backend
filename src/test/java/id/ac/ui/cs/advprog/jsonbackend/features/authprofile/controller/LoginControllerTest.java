package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.LoginService;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private LoginService loginService;
    @MockBean private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;

    private User testUser;
    private UserLoginRequest validRequest;
    private String testJwt;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(UUID.randomUUID())
            .username("testuser")
            .email("test@example.com")
            .password("hashed")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .build();

        validRequest = new UserLoginRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setPassword("password123");

        testJwt = "eyJhbGciOiJIUzI1NiJ9.test.signature";
    }

    @Test
    void testGetLoginInfo() throws Exception {
        mockMvc.perform(get("/api/login"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("Login endpoint is active")));
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(testJwt);

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", equalTo("testuser")))
            .andExpect(jsonPath("$.email", equalTo("test@example.com")))
            .andExpect(jsonPath("$.token", equalTo(testJwt)))
            .andExpect(jsonPath("$.role", equalTo("TITIPER")));

        verify(loginService, times(1)).login(ArgumentMatchers.any(UserLoginRequest.class));
        verify(jwtService, times(1)).generateToken(testUser);
    }

    @Test
    void testLoginWithDifferentRoles() throws Exception {
        User adminUser = User.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .email(testUser.getEmail())
            .password(testUser.getPassword())
            .role(UserRole.ADMIN)
            .status(testUser.getStatus())
            .fullName(testUser.getFullName())
            .build();
        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class))).thenReturn(adminUser);
        when(jwtService.generateToken(adminUser)).thenReturn(testJwt);

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role", equalTo("ADMIN")));

        verify(loginService).login(ArgumentMatchers.any(UserLoginRequest.class));
    }

    @Test
    void testLoginMissingEmail() throws Exception {
        UserLoginRequest invalidRequest = new UserLoginRequest();
        invalidRequest.setPassword("password123");

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email", notNullValue()));

        verify(loginService, never()).login(ArgumentMatchers.any());
    }

    @Test
    void testLoginMissingPassword() throws Exception {
        UserLoginRequest invalidRequest = new UserLoginRequest();
        invalidRequest.setEmail("test@example.com");

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.password", notNullValue()));

        verify(loginService, never()).login(ArgumentMatchers.any());
    }

    @Test
    void testLoginBadCredentials() throws Exception {
        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class)))
            .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", containsString("Invalid")));

        verify(loginService, times(1)).login(ArgumentMatchers.any(UserLoginRequest.class));
    }

    @Test
    void testLoginServerError() throws Exception {
        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class)))
            .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error", containsString("Database")));

        verify(loginService, times(1)).login(ArgumentMatchers.any(UserLoginRequest.class));
    }

    @Test
    void testLoginValidationMultipleErrors() throws Exception {
        UserLoginRequest emptyRequest = new UserLoginRequest();
        emptyRequest.setEmail("");
        emptyRequest.setPassword("");

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emptyRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasKey("email")))
            .andExpect(jsonPath("$", hasKey("password")));

        verify(loginService, never()).login(ArgumentMatchers.any());
    }

    @Test
    void testLoginWithDifferentStatus() throws Exception {
        User bannedUser = User.builder()
            .id(testUser.getId())
            .username(testUser.getUsername())
            .email(testUser.getEmail())
            .password(testUser.getPassword())
            .role(testUser.getRole())
            .status(UserStatus.BANNED)
            .fullName(testUser.getFullName())
            .build();
        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class))).thenReturn(bannedUser);
        when(jwtService.generateToken(bannedUser)).thenReturn(testJwt);

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", equalTo("BANNED")));

        verify(loginService).login(ArgumentMatchers.any(UserLoginRequest.class));
    }

    @Test
    void testLoginTokenGeneration() throws Exception {
        String customToken = "custom.jwt.token";
        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(customToken);

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", equalTo(customToken)));

        verify(jwtService).generateToken(testUser);
    }

    // @Test - Disabled: CSRF test needs further investigation
    /*
    void testLoginWithoutCsrf() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());

        verify(loginService, never()).login(ArgumentMatchers.any());
    }
    */

    @Test
    void testLoginMultipleAttempts() throws Exception {
        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(testJwt);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
        }

        verify(loginService, times(3)).login(ArgumentMatchers.any(UserLoginRequest.class));
    }

    @Test
    void testLoginWithSpecialCharactersInPassword() throws Exception {
        UserLoginRequest specialRequest = new UserLoginRequest();
        specialRequest.setEmail("test@example.com");
        specialRequest.setPassword("p@ssw0rd!#$%^&*()");

        when(loginService.login(ArgumentMatchers.any(UserLoginRequest.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(testJwt);

        mockMvc.perform(post("/api/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(specialRequest)))
            .andExpect(status().isOk());

        verify(loginService).login(ArgumentMatchers.any(UserLoginRequest.class));
    }

}
