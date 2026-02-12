-- Clean up existing tables if we restart the app (optional, good for dev)
DROP TABLE IF EXISTS ledger_lines CASCADE;
DROP TABLE IF EXISTS journal_entries CASCADE;
DROP TABLE IF EXISTS wallets CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. USERS: The people or systems holding money
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. WALLETS: The buckets of money (e.g., User A's Gold Coins)
CREATE TABLE wallets (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL REFERENCES users(id),
                         currency VARCHAR(20) NOT NULL, -- e.g., 'GOLD_COINS'
                         balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
                         version BIGINT DEFAULT 0, -- Optimistic locking support (Brownie point for concurrency)
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT uq_user_currency UNIQUE (user_id, currency)
);

-- 3. JOURNAL_ENTRIES: The "Master Record" of a transaction
CREATE TABLE journal_entries (
                                 id UUID PRIMARY KEY, -- We will generate this in Java
                                 transaction_type VARCHAR(50) NOT NULL, -- 'TOPUP', 'BONUS', 'SPEND'
                                 reference_id VARCHAR(100) UNIQUE, -- Idempotency key (Critical Constraint D.2)
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. LEDGER_LINES: The double-entry bookkeeping (Debits & Credits)
CREATE TABLE ledger_lines (
                              id BIGSERIAL PRIMARY KEY,
                              journal_entry_id UUID NOT NULL REFERENCES journal_entries(id),
                              wallet_id BIGINT NOT NULL REFERENCES wallets(id),
                              amount DECIMAL(19, 4) NOT NULL, -- Positive = Credit, Negative = Debit
                              description VARCHAR(255)
);

-- Indexes for performance
CREATE INDEX idx_wallets_user ON wallets(user_id);