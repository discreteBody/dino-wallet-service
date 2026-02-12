-- 1. Create the SYSTEM user (The 'Bank' or 'Treasury')
-- This meets Requirement A.2: "Create at least one System wallet"
INSERT INTO users (username, email) VALUES ('system_treasury', 'treasury@dinoventures.com')
    ON CONFLICT DO NOTHING;

-- 2. Create Regular Users
-- This meets Requirement A.3: "Create at least two users"
INSERT INTO users (username, email) VALUES ('john_doe', 'john@example.com')
    ON CONFLICT DO NOTHING;
INSERT INTO users (username, email) VALUES ('jane_smith', 'jane@example.com')
    ON CONFLICT DO NOTHING;

-- 3. Initialize Wallets
-- Giving the System deep pockets so it can pay out bonuses
INSERT INTO wallets (user_id, currency, balance)
SELECT id, 'GOLD_COINS', 1000000.00 FROM users WHERE username = 'system_treasury'
    ON CONFLICT DO NOTHING;

INSERT INTO wallets (user_id, currency, balance)
SELECT id, 'DIAMONDS', 1000000.00 FROM users WHERE username = 'system_treasury'
    ON CONFLICT DO NOTHING;

-- Initialize User Wallets with 0 balance
INSERT INTO wallets (user_id, currency, balance)
SELECT id, 'GOLD_COINS', 0.00 FROM users WHERE username IN ('john_doe', 'jane_smith')
    ON CONFLICT DO NOTHING;