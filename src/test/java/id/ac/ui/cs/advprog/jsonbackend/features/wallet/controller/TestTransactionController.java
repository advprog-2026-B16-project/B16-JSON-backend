package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TestTransactionController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletTransactionService walletTransactionService;

    @MockBean
    private JwtService jwtService;

    @Test
    void testGetTransactionHistory() throws Exception {

        Transaction tx = new Transaction(
                "wallet1",
                "user1",
                TransactionType.TOP_UP,
                new BigDecimal("100"),
                "Top Up"
        );

        tx.setId("trx-1");
        tx.setStatus(TransactionStatus.SUCCESS);

        when(walletTransactionService.getTransactionHistory("user1"))
                .thenReturn(List.of(tx));

        mockMvc.perform(get("/api/transaction/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("trx-1"))
                .andExpect(jsonPath("$[0].type").value("TOP_UP"))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));

        verify(walletTransactionService).getTransactionHistory("user1");
    }
}