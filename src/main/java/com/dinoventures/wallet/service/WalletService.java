package com.dinoventures.wallet.service;

import com.dinoventures.wallet.dto.TransactionRequest;
import com.dinoventures.wallet.entity.JournalEntry;

import java.math.BigDecimal;

public interface WalletService {
    JournalEntry performTransaction(TransactionRequest request);
    BigDecimal getBalance(Long userId, String currency);
}