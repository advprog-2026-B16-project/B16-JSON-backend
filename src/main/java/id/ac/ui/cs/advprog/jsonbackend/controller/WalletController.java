package id.ac.ui.cs.advprog.jsonbackend.controller;

import id.ac.ui.cs.advprog.jsonbackend.dto.WalletRequest;
import id.ac.ui.cs.advprog.jsonbackend.service.WalletService;
import id.ac.ui.cs.advprog.jsonbackend.model.WalletTransaction;
import id.ac.ui.cs.advprog.jsonbackend.model.Wallet;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/topup")
    public ResponseEntity<String> topUp(@RequestBody WalletRequest request) {
        walletService.topUp(request.getUserId(), request.getAmount());
        return ResponseEntity.ok("Top up success");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@Valid @RequestBody WalletRequest request) {
        walletService.withdraw(request.getUserId(), request.getAmount());
        return ResponseEntity.ok("Withdraw success");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String userId) {
        BigDecimal balance = walletService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<WalletTransaction>> getHistory(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                walletService.getTransactionHistory(userId)
        );
    }
}
