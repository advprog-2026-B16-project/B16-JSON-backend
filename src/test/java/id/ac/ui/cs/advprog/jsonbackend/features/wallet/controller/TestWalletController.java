package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.WalletRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

        mockMvc.perform(post("/api/wallet/topup/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(trxId.toString()));

        verify(walletTransactionService).requestTopUp(userId.toString(), new BigDecimal("100"));
    }

    @Test
    void testConfirmTopUp() throws Exception {
        UUID trxId = UUID.randomUUID();

        doNothing().when(walletTransactionService).confirmTopUp(trxId.toString());

        mockMvc.perform(post("/api/wallet/topup/confirm/" + trxId))
                .andExpect(status().isOk())
                .andExpect(content().string("Top up confirmed"));

        verify(walletTransactionService).confirmTopUp(trxId.toString());
    }

    @Test
    void testWithdraw() throws Exception {

        UUID userId = UUID.randomUUID();

        WalletRequest request = new WalletRequest();
        request.setUserId(userId);
        request.setAmount(new BigDecimal("50"));

        doNothing().when(walletTransactionService)
                .requestWithdraw(userId.toString(), new BigDecimal("50"));

        mockMvc.perform(post("/api/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdraw success"));

        verify(walletTransactionService)
                .requestWithdraw(userId.toString(), new BigDecimal("50"));
    }

    @Test
    void testGetBalance() throws Exception {

        UUID userId = UUID.randomUUID();

        when(walletService.getBalance(userId.toString()))
                .thenReturn(new BigDecimal("200"));

        mockMvc.perform(get("/api/wallet/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.balance").value(200));

        verify(walletService).getBalance(userId.toString());
    }
}