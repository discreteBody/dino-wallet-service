package com.dinoventures.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "ledger_lines")
@Data
public class LedgerLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "journal_entry_id", nullable = false) // Good practice to add this too
    private JournalEntry journalEntry;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false) // And this
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;
}