package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRequestTopUp() throws Exception {

        WalletRequest request = new WalletRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("100"));

        Transaction trx = new Transaction(
                "wallet1",
                "user1",
                null,
                new BigDecimal("100"),
                "Top Up"
        );
        trx.setId("trx-1");

        when(walletTransactionService.requestTopUp("user1", new BigDecimal("100")))
                .thenReturn(trx);

        mockMvc.perform(post("/api/wallet/topup/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("trx-1"));

        verify(walletTransactionService).requestTopUp("user1", new BigDecimal("100"));
    }

    @Test
    void testConfirmTopUp() throws Exception {

        doNothing().when(walletTransactionService).confirmTopUp("trx-1");

        mockMvc.perform(post("/api/wallet/topup/confirm/trx-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Top up confirmed"));

        verify(walletTransactionService).confirmTopUp("trx-1");
    }

    @Test
    void testWithdraw() throws Exception {

        WalletRequest request = new WalletRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("50"));

        doNothing().when(walletTransactionService)
                .requestWithdraw("user1", new BigDecimal("50"));

        mockMvc.perform(post("/api/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdraw success"));

        verify(walletTransactionService)
                .requestWithdraw("user1", new BigDecimal("50"));
    }

    @Test
    void testGetBalance() throws Exception {

        when(walletService.getBalance("user1"))
                .thenReturn(new BigDecimal("200"));

        mockMvc.perform(get("/api/wallet/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.balance").value(200));

        verify(walletService).getBalance("user1");
    }
}