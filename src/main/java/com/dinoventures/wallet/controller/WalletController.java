package com.dinoventures.wallet.controller;

import com.dinoventures.wallet.dto.TransactionRequest;
import com.dinoventures.wallet.entity.JournalEntry;
import com.dinoventures.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // Requirement B.1: Execute transactions
    // Handles TOPUP, BONUS, and SPEND all in one robust endpoint
    @PostMapping("/transaction")
    public ResponseEntity<?> performTransaction(@RequestBody TransactionRequest request) {
        try {
            JournalEntry entry = walletService.performTransaction(request);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "transaction_id", entry.getId(),
                    "reference_id", entry.getReferenceId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // Catches "Insufficient funds", "User not found", etc.
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    // Requirement B.2: Check balance
    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long userId, @RequestParam String currency) {
        BigDecimal balance = walletService.getBalance(userId, currency);
        return ResponseEntity.ok(Map.of(
                "user_id", userId,
                "currency", currency,
                "balance", balance
        ));
    }
}