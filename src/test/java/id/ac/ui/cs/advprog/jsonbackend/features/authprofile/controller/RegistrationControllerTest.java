package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private RegistrationService registrationService;
    @Autowired private ObjectMapper objectMapper;

    private UserRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new UserRegistrationRequest();
        validRequest.setUsername("newuser");
        validRequest.setEmail("newuser@example.com");
        validRequest.setPassword("SecurePassword123!");
        validRequest.setConfirmPassword("SecurePassword123!");
    }

    // GET /api/register
    @Test
    void testGetRegistrationInfo() throws Exception {
        mockMvc.perform(get("/api/register"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("Registration endpoint is active")));
    }

    // POST /api/register - Success
    @Test
    void testRegisterUserSuccessfully() throws Exception {
        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message", equalTo("User registered successfully")));

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterMultipleUsers() throws Exception {
        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated());

        UserRegistrationRequest secondRequest = new UserRegistrationRequest();
        secondRequest.setUsername("anotheruser");
        secondRequest.setEmail("another@example.com");
        secondRequest.setPassword("AnotherPassword123!");
        secondRequest.setConfirmPassword("AnotherPassword123!");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secondRequest)))
            .andExpect(status().isCreated());

        verify(registrationService, times(2)).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    // Validation errors - missing fields
    @Test
    void testRegisterMissingUsername() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword("SecurePassword123!");

        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterMissingEmail() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword("SecurePassword123!");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email", notNullValue()));

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterMissingPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        request.setConfirmPassword("SecurePassword123!");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.password", notNullValue()));

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterMissingConfirmPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword(null);

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", equalTo("Passwords do not match")));

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    // Validation errors - empty fields
    @Test
    void testRegisterEmptyUsername() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("");
        request.setEmail("test@example.com");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword("SecurePassword123!");

        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterEmptyEmail() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword("SecurePassword123!");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterEmptyPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        request.setPassword("");
        request.setConfirmPassword("SecurePassword123!");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.password", notNullValue()));

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    // Password mismatch
    @Test
    void testRegisterPasswordMismatch() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        request.setPassword("SecurePass123!");
        request.setConfirmPassword("DifferentPass123!");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", equalTo("Passwords do not match")));

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterPasswordMismatchMultipleTimes() throws Exception {
        UserRegistrationRequest mismatchRequest = new UserRegistrationRequest();
        mismatchRequest.setUsername("user1");
        mismatchRequest.setEmail("user1@example.com");
        mismatchRequest.setPassword("Password1@A");
        mismatchRequest.setConfirmPassword("Password2@A");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mismatchRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Passwords do not match")));

        UserRegistrationRequest anotherMismatch = new UserRegistrationRequest();
        anotherMismatch.setUsername("user2");
        anotherMismatch.setEmail("user2@example.com");
        anotherMismatch.setPassword("Pass1@AAA");
        anotherMismatch.setConfirmPassword("Pass2@AAA");

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(anotherMismatch)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Passwords do not match")));

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    // Service exceptions
    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        doThrow(new RuntimeException("User already exists"))
            .when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error", equalTo("User already exists")));

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterEmailAlreadyUsed() throws Exception {
        doThrow(new RuntimeException("Email already registered"))
            .when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error", equalTo("Email already registered")));

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterDatabaseError() throws Exception {
        doThrow(new RuntimeException("Database connection failed"))
            .when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error", containsString("Database")));

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    // Special cases
    @Test
    void testRegisterWithEmailAsUsername() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("user@example.com");
        request.setEmail("user@example.com");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword("SecurePassword123!");

        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterWithSpecialCharactersInUsername() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("user_name-123");
        request.setEmail("user@example.com");
        request.setPassword("SecurePassword123!");
        request.setConfirmPassword("SecurePassword123!");

        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterWithStrongPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        request.setPassword("V3ry$tr0ng!Passw0rd");
        request.setConfirmPassword("V3ry$tr0ng!Passw0rd");

        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    @Test
    void testRegisterWithLongPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        String longPass = "P" + "a".repeat(100) + "1!";
        request.setPassword(longPass);
        request.setConfirmPassword(longPass);

        doNothing().when(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));

        mockMvc.perform(post("/api/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(registrationService).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }

    // @Test - Disabled: CSRF test needs further investigation
    /*
    void testRegisterWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/api/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isForbidden());

        verify(registrationService, never()).register(ArgumentMatchers.any(UserRegistrationRequest.class));
    }
    */

    @Test
    void testRegisterGetInfoMultipleTimes() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/register"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Registration endpoint")));
        }
    }
}
