package com.dinoventures.wallet.repository;
import com.dinoventures.wallet.entity.LedgerLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerLineRepository extends JpaRepository<LedgerLine, Long> {}