package id.ac.ui.cs.advprog.jsonbackend.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.wallet.dto.WalletRequest;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.WalletTransaction;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.wallet.service.WalletService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testTopUp() throws Exception {

        WalletRequest request = new WalletRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("100"));

        doNothing().when(walletService).topUp("user1", new BigDecimal("100"));

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Top up success"));

        verify(walletService).topUp("user1", new BigDecimal("100"));
    }

    @Test
    void testWithdraw() throws Exception {

        WalletRequest request = new WalletRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("50"));

        doNothing().when(walletService).withdraw("user1", new BigDecimal("50"));

        mockMvc.perform(post("/api/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdraw success"));

        verify(walletService).withdraw("user1", new BigDecimal("50"));
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

    @Test
    void testGetTransactionHistory() throws Exception {

        WalletTransaction tx = new WalletTransaction(
                "wallet1",
                TransactionType.TOP_UP,
                new BigDecimal("100"),
                "Top Up"
        );

        when(walletService.getTransactionHistory("user1"))
                .thenReturn(List.of(tx));

        mockMvc.perform(get("/api/wallet/user1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(walletService).getTransactionHistory("user1");
    }
}
