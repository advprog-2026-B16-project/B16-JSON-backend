package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserProfileUpdateRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import id.ac.ui.cs.advprog.jsonbackend.features.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;
    @MockBean private OrderService orderService;
    @Autowired private ObjectMapper objectMapper;

    private User testAdminUser;
    private User testRegularUser;
    private User testJastiperUser;
    private UUID testUserId;
    private UUID jastiperId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        jastiperId = UUID.randomUUID();

        testAdminUser = User.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .email("admin@example.com")
            .password("hashed")
            .role(UserRole.ADMIN)
            .status(UserStatus.ACTIVE)
            .fullName("Admin User")
            .build();

        testRegularUser = User.builder()
            .id(testUserId)
            .username("regularuser")
            .email("regular@example.com")
            .password("hashed")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Regular User")
            .build();

        testJastiperUser = User.builder()
            .id(jastiperId)
            .username("jastiperuser")
            .email("jastiper@example.com")
            .password("hashed")
            .role(UserRole.JASTIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Jastiper User")
            .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetUsersAsAdmin() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testAdminUser, testRegularUser));

        mockMvc.perform(get("/api/user/getUsers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].username", equalTo("admin")))
            .andExpect(jsonPath("$[1].username", equalTo("regularuser")));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(username = "user", roles = "TITIPER")
    void testGetUsersAsNonAdmin() throws Exception {
        mockMvc.perform(get("/api/user/getUsers"))
            .andExpect(status().isInternalServerError());

        verify(userService, never()).getAllUsers();
    }

    @Test
    void testGetUsersUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/user/getUsers"))
            .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testBanUserAsAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).banUser(ArgumentMatchers.any(UUID.class));

        mockMvc.perform(patch("/api/user/{id}/ban", userId).with(csrf()))
            .andExpect(status().isOk());

        verify(userService).banUser(ArgumentMatchers.any(UUID.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "TITIPER")
    void testBanUserAsNonAdmin() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(patch("/api/user/{id}/ban", userId).with(csrf()))
            .andExpect(status().isInternalServerError());

        verify(userService, never()).banUser(ArgumentMatchers.any(UUID.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDemoteUserAsAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).demoteUser(ArgumentMatchers.any(UUID.class));

        mockMvc.perform(patch("/api/user/{id}/demote", userId).with(csrf()))
            .andExpect(status().isOk());

        verify(userService).demoteUser(ArgumentMatchers.any(UUID.class));
    }

    @Test
    @WithMockUser(username = "regularuser", roles = "TITIPER")
    void testUpdateProfileSuccess() throws Exception {
        UserProfileUpdateRequest updateRequest = UserProfileUpdateRequest.builder()
            .fullName("Updated Name")
            .bio("Updated bio")
            .location("New Location")
            .avatarUrl("http://example.com/new.jpg")
            .build();

        when(userService.getUserByUsername("regularuser")).thenReturn(java.util.Optional.of(testRegularUser));
        doNothing().when(userService).updateProfile(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(UserProfileUpdateRequest.class));

        mockMvc.perform(put("/api/user/profile")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk());

        verify(userService).updateProfile(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(UserProfileUpdateRequest.class));
    }

    @Test
    @WithMockUser(username = "regularuser", roles = "TITIPER")
    void testGetProfileSuccess() throws Exception {
        when(userService.getUserByUsername("regularuser")).thenReturn(java.util.Optional.of(testRegularUser));
        when(orderService.getOrderByJastiperId(testUserId.toString())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", equalTo("regularuser")))
            .andExpect(jsonPath("$.email", equalTo("regular@example.com")))
            .andExpect(jsonPath("$.role", equalTo("TITIPER")));

        verify(userService).getUserByUsername("regularuser");
    }

    @Test
    @WithMockUser(username = "nonexistent", roles = "TITIPER")
    void testGetProfileNotFound() throws Exception {
        when(userService.getUserByUsername("nonexistent")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isNotFound());

        verify(userService).getUserByUsername("nonexistent");
    }

    @Test
    void testGetPublicProfileSuccess() throws Exception {
        when(userService.getUserByUsername("regularuser")).thenReturn(java.util.Optional.of(testRegularUser));
        when(orderService.getOrderByJastiperId(testUserId.toString())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/user/profile/regularuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", equalTo("regularuser")))
            .andExpect(jsonPath("$.email", equalTo("regular@example.com")));

        verify(userService).getUserByUsername("regularuser");
    }

    @Test
    void testGetPublicProfileNotFound() throws Exception {
        when(userService.getUserByUsername("nonexistent")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/user/profile/nonexistent"))
            .andExpect(status().isNotFound());

        verify(userService).getUserByUsername("nonexistent");
    }



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetUsersEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/user/getUsers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).getAllUsers();
    }

    @Test
    void testUpdateProfileUnauthenticated() throws Exception {
        UserProfileUpdateRequest updateRequest = UserProfileUpdateRequest.builder()
            .fullName("Updated")
            .build();

        mockMvc.perform(put("/api/user/profile")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isInternalServerError());

        verify(userService, never()).updateProfile(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(UserProfileUpdateRequest.class)) ;
    }

    @Test
    void testGetProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isInternalServerError());

        verify(userService, never()).getUserByUsername(ArgumentMatchers.any(String.class));
    }
}
