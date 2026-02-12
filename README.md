# Dino Ventures Internal Wallet Service

## 🚀 Overview
A high-performance, double-entry ledger wallet service designed for scalability and auditability.

## 🛠 Tech Stack
* **Language:** Java 21 (Spring Boot 3)
* **Database:** PostgreSQL
* **Architecture:** Double-Entry Ledger (Immutable Journal)

## ⚡ Key Features (Engineering Excellence)
1.  **Ledger Architecture:** Uses a `journal_entries` and `ledger_lines` table structure. Balances are derived from the ledger, ensuring auditability.
2.  **Concurrency Control:** Implements **Pessimistic Locking** (`SELECT ... FOR UPDATE`) to prevent Race Conditions.
3.  **Deadlock Avoidance:** Locks are always acquired in deterministic order (ascending Wallet ID) to prevent database deadlocks.
4.  **Idempotency:** Unique `reference_id` checks prevent duplicate transactions.

## 🐳 How to Run
1.  **Prerequisites:** Docker and Docker Compose.
2.  **Run:**
    ```bash
    docker-compose up --build
    ```
3.  **Test:**
    POST /api/wallet/transaction
    GET /api/wallet/{id}/balance

