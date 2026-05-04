package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.WalletRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionService walletTransactionService;

    public WalletController(
            WalletService walletService,
            WalletTransactionService walletTransactionService
    ) {
        this.walletService = walletService;
        this.walletTransactionService = walletTransactionService;
    }

    @PostMapping("/topup/request")
    public ResponseEntity<Transaction> requestTopUp(@RequestBody WalletRequest request) {
        Transaction trx = walletTransactionService.requestTopUp(request.getUserId(), request.getAmount());

        return ResponseEntity.ok(trx);
    }

    @PostMapping("/topup/confirm/{transactionId}")
    public ResponseEntity<String> confirmTopUp(@PathVariable String transactionId) {
        walletTransactionService.confirmTopUp(transactionId);
        return ResponseEntity.ok("Top up confirmed");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@Valid @RequestBody WalletRequest request) {
        walletTransactionService.requestWithdraw(request.getUserId(), request.getAmount());
        return ResponseEntity.ok("Withdraw success");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable String userId) {
        BigDecimal balance = walletService.getBalance(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "balance", balance
            )
        );
    }
}
