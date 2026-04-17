package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.WalletRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PostMapping("/topup")
    public ResponseEntity<String> topUp(@RequestBody WalletRequest request) {
        walletTransactionService.topUp(request.getUserId(), request.getAmount());
        return ResponseEntity.ok("Top up success");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@Valid @RequestBody WalletRequest request) {
        walletTransactionService.withdraw(request.getUserId(), request.getAmount());
        return ResponseEntity.ok("Withdraw success");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String userId) {
        BigDecimal balance = walletService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
}
