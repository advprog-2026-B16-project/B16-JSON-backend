package id.ac.ui.cs.advprog.jsonbackend.features.transaction.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.dto.TransactionResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletUnauthorizedException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final WalletTransactionService walletTransactionService;

    public TransactionController(WalletTransactionService walletTransactionService) {
        this.walletTransactionService = walletTransactionService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable String userId
    ) {
        if (authenticatedUser == null || !authenticatedUser.getId().toString().equals(userId)) {
            throw new WalletUnauthorizedException("You can only view your own transaction history");
        }

        List<TransactionResponse> response = walletTransactionService
                .getTransactionHistory(userId)
                .stream()
                .map(trx -> new TransactionResponse(
                        trx.getId(),
                        trx.getType(),
                        trx.getAmount(),
                        trx.getStatus(),
                        trx.getDescription()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
