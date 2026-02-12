package com.dinoventures.wallet.repository;

import com.dinoventures.wallet.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    Optional<JournalEntry> findByReferenceId(String referenceId);
}