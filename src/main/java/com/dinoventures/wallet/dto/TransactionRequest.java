package com.dinoventures.wallet.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private Long userId;
    private BigDecimal amount; // Always positive
    private String currency;   // "GOLD_COINS"
    private String transactionType; // "TOPUP", "BONUS", "SPEND"
    private String referenceId; // Unique ID (e.g., "PAYMENT_123")
}