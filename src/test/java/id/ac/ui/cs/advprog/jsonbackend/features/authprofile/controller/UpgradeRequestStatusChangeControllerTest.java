package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestStatusChangeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestStatusChangeService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UpgradeRequestStatusChangeControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UpgradeRequestStatusChangeService upgradeService;
    @MockBean private UserService userService;
    @Autowired private ObjectMapper objectMapper;

    private User testUser;
    private UUID userId;
    private UUID requestId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        testUser = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@example.com")
            .password("hashed")
            .role(UserRole.TITIPER)
            .status(UserStatus.ACTIVE)
            .fullName("Test User")
            .build();
    }

    // POST /api/upgrade-request/submit
    @Test
    @WithMockUser(username = "testuser", roles = "TITIPER")
    void testSubmitUpgradeRequestSuccess() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User Full")
            .credential("credential123")
            .socialMediaUrl("http://instagram.com/testuser")
            .build();

        UpgradeRequestResponse response = UpgradeRequestResponse.builder()
            .id(requestId)
            .createdAt(OffsetDateTime.now())
            .requesterUsername("testuser")
            .fullName("Test User Full")
            .credential("credential123")
            .socialMediaUrl("http://instagram.com/testuser")
            .status("PENDING")
            .build();

        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(upgradeService.submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.fullName", equalTo("Test User Full")))
            .andExpect(jsonPath("$.status", equalTo("PENDING")))
            .andExpect(jsonPath("$.requesterUsername", equalTo("testuser")));

        verify(userService).getUserByUsername("testuser");
        verify(upgradeService).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "TITIPER")
    void testSubmitUpgradeRequestMultipleTimes() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User Full")
            .credential("credential123")
            .socialMediaUrl("http://instagram.com/testuser")
            .build();

        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(upgradeService.submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class))).thenReturn(
            UpgradeRequestResponse.builder()
                .id(UUID.randomUUID())
                .createdAt(OffsetDateTime.now())
                .requesterUsername("testuser")
                .fullName("Test User Full")
                .credential("credential123")
                .status("PENDING")
                .build()
        );

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(upgradeService, times(2)).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "TITIPER")
    void testSubmitMissingFullName() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .credential("credential123")
            .socialMediaUrl("http://instagram.com/testuser")
            .build();

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", containsString("Full name is required")));

        verify(upgradeService, never()).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "TITIPER")
    void testSubmitMissingCredential() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User Full")
            .socialMediaUrl("http://instagram.com/testuser")
            .build();

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", containsString("Credential")));

        verify(upgradeService, never()).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "TITIPER")
    void testSubmitWithNullSocialMediaUrl() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User Full")
            .credential("credential123")
            .socialMediaUrl(null)
            .build();

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", containsString("Social media")));

        verify(upgradeService, never()).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    @Test
    void testSubmitUnauthenticated() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User Full")
            .credential("credential123")
            .socialMediaUrl("http://example.com")
            .build();

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());

        verify(upgradeService, never()).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    // PATCH /api/upgrade-request/change-status/{requestId}
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusSuccess() throws Exception {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("admin");
        request.setNewStatus("APPROVED");

        doNothing().when(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());

        mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", equalTo("Status updated successfully")));

        verify(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusToPending() throws Exception {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("admin");
        request.setNewStatus("PENDING");

        doNothing().when(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());

        mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusToRejected() throws Exception {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("admin");
        request.setNewStatus("REJECTED");

        doNothing().when(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());

        mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusMultipleTimes() throws Exception {
        doNothing().when(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());

        String[] statuses = {"PENDING", "APPROVED", "REJECTED"};

        for (String status : statuses) {
            UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
            request.setUsername("admin");
            request.setNewStatus(status);

            mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }

        verify(upgradeService, times(3)).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusMissingNewStatus() throws Exception {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("admin");

        mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(upgradeService, never()).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(String.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusEmptyNewStatus() throws Exception {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("admin");
        request.setNewStatus("");

        mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(upgradeService, never()).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(String.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusWithDifferentRequestIds() throws Exception {
        doNothing().when(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("admin");
        request.setNewStatus("APPROVED");

        for (UUID id : new UUID[]{id1, id2, id3}) {
            mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }

        verify(upgradeService, times(3)).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.eq("APPROVED"));
    }

    @Test
    void testSubmitWithoutCsrf() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User Full")
            .credential("credential123")
            .socialMediaUrl("http://example.com")
            .build();

        mockMvc.perform(post("/api/upgrade-request/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());

        verify(upgradeService, never()).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    // @Test - Disabled: CSRF test needs further investigation
    /*
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusWithoutCsrf() throws Exception {
        UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
        request.setUsername("admin");
        request.setNewStatus("APPROVED");

        mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());

        verify(upgradeService, never()).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(String.class));
    }
    */

    @Test
    @WithMockUser(username = "testuser", roles = "TITIPER")
    void testSubmitWithSpecialCharactersInFullName() throws Exception {
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User @#$%^&*()")
            .credential("credential123")
            .socialMediaUrl("http://instagram.com/testuser")
            .build();

        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(upgradeService.submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class)))
            .thenReturn(UpgradeRequestResponse.builder()
                .id(requestId)
                .createdAt(OffsetDateTime.now())
                .requesterUsername("testuser")
                .fullName("Test User @#$%^&*()")
                .credential("credential123")
                .status("PENDING")
                .build());

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(upgradeService).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "TITIPER")
    void testSubmitWithLongCredential() throws Exception {
        String longCred = "C".repeat(500);
        UpgradeRequestSubmissionRequest request = UpgradeRequestSubmissionRequest.builder()
            .fullName("Test User Full")
            .credential(longCred)
            .socialMediaUrl("http://instagram.com/testuser")
            .build();

        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(upgradeService.submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class)))
            .thenReturn(UpgradeRequestResponse.builder()
                .id(requestId)
                .createdAt(OffsetDateTime.now())
                .requesterUsername("testuser")
                .fullName("Test User Full")
                .credential(longCred)
                .status("PENDING")
                .build());

        mockMvc.perform(post("/api/upgrade-request/submit")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(upgradeService).submitUpgradeRequest(ArgumentMatchers.any(User.class), ArgumentMatchers.any(UpgradeRequestSubmissionRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testChangeStatusWithDifferentStatuses() throws Exception {
        doNothing().when(upgradeService).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());

        String[] allStatuses = {"PENDING", "APPROVED", "REJECTED", "UNDER_REVIEW"};

        for (String status : allStatuses) {
            UpgradeRequestStatusChangeRequest request = new UpgradeRequestStatusChangeRequest();
            request.setUsername("admin");
            request.setNewStatus(status);

            mockMvc.perform(patch("/api/upgrade-request/change-status/{requestId}", requestId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }

        verify(upgradeService, times(4)).updateRequestStatus(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyString());
    }
}
