package id.ac.ui.cs.advprog.jsonbackend.features.admin.controller;

import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestStatusChangeService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UpgradeRequestRetrievalService upgradeRequestRetrievalService;

    @MockBean
    private UpgradeRequestStatusChangeService upgradeRequestStatusChangeService;

    @MockBean
    private WalletTransactionService walletTransactionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsersReturnsAllUsers() throws Exception {
        User user = buildUser(UserRole.TITIPER);
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(user.getId().toString()))
                .andExpect(jsonPath("$[0].role").value("TITIPER"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanMakeUserInactiveBanDemoteAndDelete() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(patch("/api/admin/users/{userId}/inactive", userId))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/admin/users/{userId}/ban", userId))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/admin/users/{userId}/demote", userId))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/admin/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(2)).banUser(userId);
        verify(userService).demoteUser(userId);
        verify(userService).deleteUser(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingTopUpsReturnsTopUpRequests() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction(walletId, userId, TransactionType.TOP_UP, new BigDecimal("50000"), "Top Up Request");
        transaction.setId(transactionId);
        transaction.setStatus(TransactionStatus.PENDING);

        when(walletTransactionService.getPendingTopUpRequests()).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/admin/topups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void confirmTopUpUsesWalletTransactionService() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/admin/topups/{transactionId}/confirm", transactionId))
                .andExpect(status().isOk());

        verify(walletTransactionService).confirmTopUp(transactionId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUpgradeRequestsReturnsRequests() throws Exception {
        User user = buildUser(UserRole.TITIPER);
        UpgradeRequest request = UpgradeRequest.builder()
                .requesterUser(user)
                .fullName("Applicant")
                .credential("Credential")
                .socialMediaUrl("https://example.com/applicant")
                .status("PENDING")
                .build();
        request.setUpgrReqId(UUID.randomUUID());

        when(upgradeRequestRetrievalService.getAllRequests()).thenReturn(List.of(request));

        mockMvc.perform(get("/api/admin/upgrade-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(request.getUpgrReqId().toString()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAcceptAndRejectUpgradeRequests() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(patch("/api/admin/upgrade-requests/{requestId}/accept", requestId))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/admin/upgrade-requests/{requestId}/reject", requestId))
                .andExpect(status().isOk());

        verify(upgradeRequestStatusChangeService).updateRequestStatus(requestId, "ACCEPTED");
        verify(upgradeRequestStatusChangeService).updateRequestStatus(requestId, "REJECTED");
    }

    private User buildUser(UserRole role) {
        return User.builder()
                .id(UUID.randomUUID())
                .username("user")
                .email("user@example.com")
                .password("password")
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
