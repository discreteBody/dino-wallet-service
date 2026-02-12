package com.dinoventures.wallet.repository;

import com.dinoventures.wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Finds a wallet for a specific user and currency
    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);

    // PESSIMISTIC_WRITE locks the row. No other transaction can read/write this
    // until we finish. This prevents Race Conditions.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") Long id);
}