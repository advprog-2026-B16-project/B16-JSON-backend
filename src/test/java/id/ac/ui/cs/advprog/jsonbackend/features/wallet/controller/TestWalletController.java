package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.WalletRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.TopUpRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
class TestWalletController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    private WalletTransactionService walletTransactionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRequestTopUp() throws Exception {

        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID trxId = UUID.randomUUID();

        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(new BigDecimal("100"));

        Transaction trx = new Transaction(
                walletId,
                userId,
                null,
                new BigDecimal("100"),
                "Top Up"
        );
        trx.setId(trxId);

        when(walletTransactionService.requestTopUp(userId.toString(), new BigDecimal("100")))
                .thenReturn(trx);

        authenticate(userId);
        try {
            mockMvc.perform(post("/api/wallet/topup/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(trxId.toString()));
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletTransactionService).requestTopUp(userId.toString(), new BigDecimal("100"));
    }

    @Test
    void testConfirmTopUp() throws Exception {
        UUID trxId = UUID.randomUUID();

        doNothing().when(walletTransactionService).confirmTopUp(trxId.toString());

        authenticate(UUID.randomUUID(), UserRole.ADMIN);
        try {
            mockMvc.perform(post("/api/wallet/topup/confirm/" + trxId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Top up confirmed"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletTransactionService).confirmTopUp(trxId.toString());
    }

    @Test
    void testConfirmTopUpRejectsNonAdmin() throws Exception {
        UUID trxId = UUID.randomUUID();

        authenticate(UUID.randomUUID(), UserRole.TITIPER);
        try {
            mockMvc.perform(post("/api/wallet/topup/confirm/" + trxId))
                    .andExpect(status().isUnauthorized());
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletTransactionService, never()).confirmTopUp(anyString());
    }

    @Test
    void testGetPendingTopUpRequestsForAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID trxId = UUID.randomUUID();
        Transaction trx = new Transaction(walletId, userId, TransactionType.TOP_UP, new BigDecimal("100"), "Top Up Request");
        trx.setId(trxId);
        trx.setStatus(TransactionStatus.PENDING);

        when(walletTransactionService.getPendingTopUpRequestResponses()).thenReturn(List.of(new TopUpRequestResponse(trx)));

        authenticate(UUID.randomUUID(), UserRole.ADMIN);
        try {
            mockMvc.perform(get("/api/wallet/topup/requests"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].transactionId").value(trxId.toString()))
                    .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                    .andExpect(jsonPath("$[0].walletId").value(walletId.toString()))
                    .andExpect(jsonPath("$[0].amount").value(100))
                    .andExpect(jsonPath("$[0].status").value("PENDING"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletTransactionService).getPendingTopUpRequestResponses();
    }

    @Test
    void testGetPendingTopUpRequestsRejectsNonAdmin() throws Exception {
        authenticate(UUID.randomUUID(), UserRole.TITIPER);
        try {
            mockMvc.perform(get("/api/wallet/topup/requests"))
                    .andExpect(status().isUnauthorized());
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletTransactionService, never()).getPendingTopUpRequestResponses();
    }

    @Test
    void testWithdraw() throws Exception {

        UUID userId = UUID.randomUUID();

        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(new BigDecimal("50"));

        doNothing().when(walletTransactionService)
                .requestWithdraw(userId.toString(), new BigDecimal("50"));

        authenticate(userId);
        try {
            mockMvc.perform(post("/api/wallet/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("utf-8")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Withdraw success"));
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletTransactionService)
                .requestWithdraw(userId.toString(), new BigDecimal("50"));
    }

    @Test
    void testGetBalance() throws Exception {

        UUID userId = UUID.randomUUID();

        when(walletService.getBalance(userId.toString()))
                .thenReturn(new BigDecimal("200"));

        authenticate(userId);
        try {
            mockMvc.perform(get("/api/wallet/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.balance").value(200));
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletService).getBalance(userId.toString());
    }

    @Test
    void testGetMyBalanceUsesAuthenticatedUser() throws Exception {
        UUID userId = UUID.randomUUID();

        when(walletService.getBalance(userId.toString()))
                .thenReturn(new BigDecimal("200"));

        authenticate(userId);
        try {
            mockMvc.perform(get("/api/wallet/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId.toString()))
                    .andExpect(jsonPath("$.balance").value(200));
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletService).getBalance(userId.toString());
    }

    @Test
    void testGetBalanceRejectsOtherUser() throws Exception {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        authenticate(authenticatedUserId);
        try {
            mockMvc.perform(get("/api/wallet/" + otherUserId))
                    .andExpect(status().isUnauthorized());
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(walletService, never()).getBalance(otherUserId.toString());
    }

    private void authenticate(UUID userId) {
        authenticate(userId, UserRole.TITIPER);
    }

    private void authenticate(UUID userId, UserRole role) {
        User user = User.builder()
                .id(userId)
                .username("titiper")
                .email("titiper@example.com")
                .password("password")
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }
}
