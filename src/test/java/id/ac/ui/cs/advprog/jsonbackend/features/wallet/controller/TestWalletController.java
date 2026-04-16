package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.WalletRequest;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testTopUp() throws Exception {

        WalletRequest request = new WalletRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("100"));

        doNothing().when(walletTransactionService).topUp("user1", new BigDecimal("100"));

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Top up success"));

        verify(walletTransactionService).topUp("user1", new BigDecimal("100"));
    }

    @Test
    void testWithdraw() throws Exception {

        WalletRequest request = new WalletRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("50"));

        doNothing().when(walletTransactionService).withdraw("user1", new BigDecimal("50"));

        mockMvc.perform(post("/api/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdraw success"));

        verify(walletTransactionService).withdraw("user1", new BigDecimal("50"));
    }

    @Test
    void testGetBalance() throws Exception {

        when(walletService.getBalance("user1"))
                .thenReturn(new BigDecimal("200"));

        mockMvc.perform(get("/api/wallet/user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("200"));

        verify(walletService).getBalance("user1");
    }
}
