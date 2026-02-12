package com.dinoventures.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Data
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version // Handles Optimistic Locking automatically
    private Long version;
}