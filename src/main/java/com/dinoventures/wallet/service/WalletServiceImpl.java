package com.dinoventures.wallet.service;

import com.dinoventures.wallet.dto.TransactionRequest;
import com.dinoventures.wallet.entity.*;
import com.dinoventures.wallet.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final LedgerLineRepository ledgerLineRepository; // You might need to create this simple repo

    private static final Long SYSTEM_USER_ID = 1L; // From our seed.sql

    @Override
    @Transactional
    public JournalEntry performTransaction(TransactionRequest request) {
        // 1. Idempotency Check
        var existingEntry = journalEntryRepository.findByReferenceId(request.getReferenceId());
        if (existingEntry.isPresent()) {
            return existingEntry.get();
        }

        // 2. Determine IDs
        Long sourceUserId = "SPEND".equalsIgnoreCase(request.getTransactionType()) ? request.getUserId() : SYSTEM_USER_ID;
        Long destUserId   = "SPEND".equalsIgnoreCase(request.getTransactionType()) ? SYSTEM_USER_ID : request.getUserId();

        // 3. LOCKING STRATEGY (The Fix)
        // We must fetch the wallets WITH the lock immediately to ensure we have the latest version.
        // We sort by ID to prevent Deadlocks.
        Long firstId = Math.min(sourceUserId, destUserId);
        Long secondId = Math.max(sourceUserId, destUserId);

        // Note: You might need to adjust your repository to find by UserId OR just fetch by Wallet ID if you know it.
        // For simplicity/robustness, let's look up the Wallet IDs first (Read-only), then Lock them.

        var sourceWalletTemp = walletRepository.findByUserIdAndCurrency(sourceUserId, request.getCurrency())
                .orElseThrow(() -> new RuntimeException("Source wallet not found"));
        var destWalletTemp = walletRepository.findByUserIdAndCurrency(destUserId, request.getCurrency())
                .orElseThrow(() -> new RuntimeException("Destination wallet not found"));

        // Now LOCK them in deterministic order
        Wallet sourceWallet, destWallet;

        if (sourceWalletTemp.getId() < destWalletTemp.getId()) {
            sourceWallet = walletRepository.findByIdForUpdate(sourceWalletTemp.getId()).orElseThrow();
            destWallet = walletRepository.findByIdForUpdate(destWalletTemp.getId()).orElseThrow();
        } else {
            destWallet = walletRepository.findByIdForUpdate(destWalletTemp.getId()).orElseThrow();
            sourceWallet = walletRepository.findByIdForUpdate(sourceWalletTemp.getId()).orElseThrow();
        }

        // 4. Validate Balance (Now we are 100% sure nobody else is changing it)
        if (sourceWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // 5. Update Balances
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(request.getAmount()));
        destWallet.setBalance(destWallet.getBalance().add(request.getAmount()));

        // 6. Create Journal Entry
        JournalEntry entry = new JournalEntry();
        // FIX: DO NOT SET ID MANUALLY! Let Hibernate/Database generate it.
        // entry.setId(UUID.randomUUID());  <-- DELETE THIS LINE

        entry.setTransactionType(request.getTransactionType());
        entry.setReferenceId(request.getReferenceId());
        entry.setCreatedAt(LocalDateTime.now());

        journalEntryRepository.save(entry); // Hibernate now knows this is an INSERT

        // 7. Create Ledger Lines
        createLedgerLine(entry, sourceWallet, request.getAmount().negate(), "Debit " + request.getTransactionType());
        createLedgerLine(entry, destWallet, request.getAmount(), "Credit " + request.getTransactionType());

        // 8. Save Wallets
        walletRepository.save(sourceWallet);
        walletRepository.save(destWallet);

        return entry;
    }

    private void createLedgerLine(JournalEntry entry, Wallet wallet, BigDecimal amount, String description) {
        LedgerLine line = new LedgerLine();
        line.setJournalEntry(entry);
        line.setWallet(wallet);
        line.setAmount(amount);
        line.setDescription(description);
        // You need to create this repository interface
        // ledgerLineRepository.save(line);
    }

    @Override
    public BigDecimal getBalance(Long userId, String currency) {
        return walletRepository.findByUserIdAndCurrency(userId, currency)
                .map(Wallet::getBalance)
                .orElse(BigDecimal.ZERO);
    }
}