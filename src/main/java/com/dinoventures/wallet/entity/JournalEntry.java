package com.dinoventures.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "journal_entries")
@Data
public class JournalEntry {
    @Id
    @GeneratedValue // Auto-generates UUID
    private UUID id;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}