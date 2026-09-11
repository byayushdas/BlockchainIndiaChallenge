# Digital Payment & Wallet Platform

A production-inspired backend system for digital wallets and payments, built with Java and Spring Boot. The platform supports secure wallet management, peer-to-peer transfers, merchant payments, refunds, double-entry accounting, idempotent payment processing, concurrency control, asynchronous event processing, caching, rate limiting, auditing, testing, and containerized deployment.

> **Core engineering goal:** ensure that money movement is correct, atomic, auditable, retry-safe, and resilient under concurrent requests and partial failures.

---

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Problem Statement](#2-problem-statement)
- [3. Objectives](#3-objectives)
- [4. Key Features](#4-key-features)
- [5. Technology Stack](#5-technology-stack)
- [6. High-Level Architecture](#6-high-level-architecture)
- [7. Architecture Principles](#7-architecture-principles)
- [8. Core Modules](#8-core-modules)
- [9. User & Authentication Flow](#9-user--authentication-flow)
- [10. Wallet Management](#10-wallet-management)
- [11. Add Money / Top-Up Flow](#11-add-money--top-up-flow)
- [12. Peer-to-Peer Transfer Flow](#12-peer-to-peer-transfer-flow)
- [13. ACID Transactions & Concurrency Control](#13-acid-transactions--concurrency-control)
- [14. Double-Entry Ledger](#14-double-entry-ledger)
- [15. Idempotency](#15-idempotency)
- [16. Merchant Payments](#16-merchant-payments)
- [17. Payment State Machine](#17-payment-state-machine)
- [18. Refund System](#18-refund-system)
- [19. Kafka Event Processing](#19-kafka-event-processing)
- [20. Redis Usage](#20-redis-usage)
- [21. PostgreSQL Database Design](#21-postgresql-database-design)
- [22. Database Tables](#22-database-tables)
- [23. Database Integrity & Indexing](#23-database-integrity--indexing)
- [24. REST API Design](#24-rest-api-design)
- [25. API Error Handling](#25-api-error-handling)
- [26. Security](#26-security)
- [27. Rate Limiting](#27-rate-limiting)
- [28. Audit Logging](#28-audit-logging)
- [29. Project Structure](#29-project-structure)
- [30. Testing Strategy](#30-testing-strategy)
- [31. Testcontainers](#31-testcontainers)
- [32. Docker & Local Development](#32-docker--local-development)
- [33. Database Migrations](#33-database-migrations)
- [34. OpenAPI / Swagger](#34-openapi--swagger)
- [35. Observability](#35-observability)
- [36. Performance Testing](#36-performance-testing)
- [37. Development Roadmap](#37-development-roadmap)
- [38. Example Payment Flow](#38-example-payment-flow)
- [39. Failure Scenarios](#39-failure-scenarios)
- [40. Design Decisions](#40-design-decisions)
- [41. Future Enhancements](#41-future-enhancements)
- [42. Resume Highlights](#42-resume-highlights)
- [43. Interview Topics Demonstrated](#43-interview-topics-demonstrated)
- [44. Running the Project](#44-running-the-project)
- [45. Project Status](#45-project-status)
- [46. Disclaimer](#46-disclaimer)

---

## 1. Project Overview

**Digital Payment & Wallet Platform** is a backend-first payment system inspired by the architectural patterns used in modern wallet and payment platforms.

The platform allows:

- Customer registration and authentication
- Wallet creation and management
- Adding money to wallets through a simulated payment gateway
- Peer-to-peer transfers
- Merchant payments
- Transaction history and lookup
- Refund processing
- Double-entry ledger accounting
- Idempotent payment processing
- Concurrent transfer protection
- Asynchronous event processing with Kafka
- Caching and rate limiting with Redis
- Audit logging
- Automated testing
- Docker-based deployment
- API documentation and observability

The system intentionally prioritizes **financial correctness and consistency** over superficial feature count.

---

## 2. Problem Statement

Payment systems operate in an environment where the same request may be retried, concurrent requests may arrive at the same time, downstream services may fail, and users still expect their balance and transaction history to remain correct.

A naive implementation can produce serious problems:

```text
Double spending
Duplicate payment
Partial debit/credit
Lost transaction history
Incorrect balances
Race conditions
Inconsistent states
```

This project addresses those problems using:

```text
ACID transactions
+
Row-level locking
+
Database constraints
+
Double-entry ledger
+
Idempotency
+
Event-driven processing
+
Audit logs
```

---

## 3. Objectives

### Functional Objectives

- Create and manage users
- Secure user authentication
- Create customer and merchant wallets
- Maintain wallet balances
- Support top-ups
- Support peer-to-peer transfers
- Support merchant payments
- Support refunds
- Provide transaction history
- Publish payment events
- Generate payment notifications

### Engineering Objectives

- Maintain transactional consistency
- Prevent concurrent balance corruption
- Prevent duplicate processing
- Preserve a complete financial audit trail
- Keep PostgreSQL as the authoritative financial data store
- Decouple asynchronous work with Kafka
- Improve response performance with Redis
- Provide testable, observable, containerized services

---

## 4. Key Features

### Core

- User registration/login
- JWT authentication
- Role-based access control
- Wallet creation
- Wallet balance
- Wallet freeze/close states
- Top-up
- Peer-to-peer transfer
- Merchant payment
- Transaction history
- Refunds

### Reliability

- ACID-compliant transfers
- Pessimistic row-level locking
- Double-entry ledger
- Idempotency keys
- Unique constraints
- Database-level validation
- Audit logging
- Consistent error handling

### Distributed / Infrastructure

- Kafka event publishing
- Kafka consumers
- Redis caching
- Redis-based rate limiting
- Docker / Docker Compose
- Flyway migrations
- OpenAPI / Swagger
- Actuator / metrics

### Quality

- Unit tests
- Integration tests
- Concurrency tests
- Testcontainers
- Load testing
- CI pipeline

---

## 5. Technology Stack

| Category | Preferred Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.x |
| Web | Spring Web MVC |
| Security | Spring Security + JWT |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 18.x |
| Database Driver | PostgreSQL JDBC Driver |
| Migrations | Flyway |
| Cache / Rate Limiting | Redis 7.4+ |
| Messaging | Apache Kafka 4.3.x |
| Testing | JUnit 6, Mockito, Spring Boot Test |
| Integration Testing | Testcontainers |
| API Documentation | OpenAPI / Swagger |
| Build | Maven |
| Containers | Docker + Docker Compose |
| Observability | Spring Boot Actuator + Micrometer |
| Metrics | Prometheus |
| Dashboards | Grafana |
| Load Testing | k6 |
| CI/CD | GitHub Actions |
| Version Control | Git + GitHub |

### Important Implementation Notes

- Use `BigDecimal` for monetary amounts; never use `double` for money.
- Use PostgreSQL as the authoritative source for wallet/accounting state.
- Redis is for non-authoritative data such as cache and rate limiting.
- Kafka is for asynchronous events and downstream processing.
- Use Flyway for schema evolution.
- Keep payment logic transactional and deterministic.

---

## 6. High-Level Architecture

```text
                         ┌─────────────────────┐
                         │    Web / Mobile UI  │
                         └──────────┬──────────┘
                                    │
                              HTTPS / REST
                                    │
                         ┌──────────▼──────────┐
                         │    Spring Boot API  │
                         │                     │
                         │ Auth                │
                         │ User                │
                         │ Wallet              │
                         │ Transfer            │
                         │ Payment             │
                         │ Refund              │
                         │ Ledger              │
                         │ Merchant            │
                         │ Admin               │
                         └───────┬──────┬──────┘
                                 │      │
                    ┌────────────┘      └────────────┐
                    ▼                                ▼
             ┌─────────────┐                  ┌────────────┐
             │ PostgreSQL  │                  │   Redis    │
             │             │                  │            │
             │ Source of   │                  │ Cache      │
             │ truth       │                  │ Rate Limit │
             └──────┬──────┘                  └────────────┘
                    │
                    │ Domain Events
                    ▼
               ┌──────────┐
               │  Kafka   │
               └────┬─────┘
                    │
          ┌─────────┼───────────┐
          ▼         ▼           ▼
     Notification  Audit    Analytics
      Consumer     Consumer   Consumer
```

---

## 7. Architecture Principles

### 1. PostgreSQL is the source of truth

Financial state must remain in a strongly consistent relational database.

### 2. Financial operations are transactional

Debit, credit, transaction creation, and ledger entries belong to one logical database transaction.

### 3. Lock before deciding

For a transfer, the sender wallet must be protected against concurrent modifications before checking the spendable balance.

### 4. Every financial movement is auditable

Successful transfers and refunds produce explicit ledger records instead of silently mutating history.

### 5. Retries must be safe

Client or network retries must not create duplicate money movement.

### 6. Asynchronous work is decoupled

Notifications, analytics, and other downstream work can consume Kafka events rather than blocking the main payment path.

### 7. Measure performance instead of claiming scale

Load-test the application and report actual throughput/latency results.

---

## 8. Core Modules

### Auth Module

Responsible for:

- Registration
- Login
- Password hashing
- JWT issuance
- Token refresh
- Role handling
- Account status

### User Module

Responsible for:

- User profile
- Contact information
- Status
- Role
- Ownership checks

### Wallet Module

Responsible for:

- Wallet creation
- Balance retrieval
- Wallet status
- Top-up
- Wallet-level validation

### Transfer Module

Responsible for:

- Peer-to-peer transfer
- Balance verification
- Wallet locking
- Transaction creation
- Ledger creation

### Payment Module

Responsible for:

- Merchant payments
- Payment state transitions
- Idempotency
- Payment lookup

### Refund Module

Responsible for:

- Refund validation
- Refund transaction creation
- Compensating ledger entries
- Refund state

### Ledger Module

Responsible for:

- Debit entries
- Credit entries
- Balance-after snapshots
- Financial auditability

### Merchant Module

Responsible for:

- Merchant registration
- Merchant wallet
- Merchant payment endpoint
- Merchant status

### Notification Module

Responsible for:

- Kafka event consumption
- Payment notifications
- Email/SMS/push abstraction

### Audit Module

Responsible for:

- Security events
- Administrative changes
- Wallet actions
- Payment-related audit data

### Admin Module

Responsible for:

- User management
- Wallet freeze/unfreeze
- Transaction monitoring
- Platform statistics

---

## 9. User & Authentication Flow

```text
Register
   ↓
Validate email/phone
   ↓
Hash password
   ↓
Create user
   ↓
Create wallet
   ↓
Return registration response
```

Login:

```text
Login
  ↓
Verify credentials
  ↓
Create access token
  ↓
Create/rotate refresh token
  ↓
Return tokens
```

Suggested roles:

```text
CUSTOMER
MERCHANT
ADMIN
```

---

## 10. Wallet Management

### Wallet Entity

```text
Wallet
--------------------------------
id
user_id
currency
balance
version
status
created_at
updated_at
```

Possible wallet statuses:

```text
ACTIVE
FROZEN
CLOSED
```

### Ownership Rule

A user can access only wallets they own unless they have an explicitly authorized administrative role.

---

## 11. Add Money / Top-Up Flow

A real payment provider should be represented by a sandbox/mock provider in a student project.

```text
User
  │
  │ ₹2,000
  ▼
Wallet API
  │
  ▼
Payment Service
  │
  ▼
Mock Gateway
  │
  ├──────── SUCCESS
  │
  ▼
DB Transaction
  │
  ├── Update wallet state
  ├── Create transaction
  └── Create ledger entry
  │
  ▼
Kafka Event
```

### Example Request

```http
POST /api/v1/wallets/topup
Authorization: Bearer <JWT>
Idempotency-Key: topup-9d82c1
Content-Type: application/json
```

```json
{
  "amount": 2000.00,
  "currency": "INR",
  "paymentMethod": "CARD"
}
```

### Example Response

```json
{
  "transactionId": "TXN-938471",
  "status": "SUCCESS",
  "amount": 2000.00,
  "currency": "INR"
}
```

---

## 12. Peer-to-Peer Transfer Flow

Example:

```text
Ayush Wallet
₹5,000
   │
   │ ₹750
   ▼
Rahul Wallet
₹1,500
```

After the transfer:

```text
Ayush = ₹4,250
Rahul = ₹2,250
```

### Transaction Flow

```text
1. Authenticate request
2. Validate transfer amount
3. Validate receiver
4. Validate source wallet
5. Start DB transaction
6. Acquire wallet lock(s)
7. Check sufficient balance
8. Debit sender
9. Credit receiver
10. Create transaction record
11. Create double-entry ledger records
12. Commit
13. Publish payment/transfer event
14. Process notifications asynchronously
```

If any critical database operation fails:

```text
ROLLBACK
```

No partial transfer should remain.

---

## 13. ACID Transactions & Concurrency Control

This is the most important backend engineering component of the project.

### Atomicity

The debit and credit happen together.

```text
Debit ₹500
+
Credit ₹500
+
Transaction record
+
Ledger entries
```

All succeed or all roll back.

### Consistency

Database constraints and application invariants should remain valid before and after the transaction.

### Isolation

Concurrent transfers cannot safely modify the same wallet based on stale reads.

### Durability

Committed state remains persisted in PostgreSQL.

### Row-Level Locking

Use a pessimistic write lock for the wallet rows involved in a transfer.

Conceptually:

```text
Request A
   ↓
LOCK Sender Wallet
   ↓
Read balance = ₹1,000
   ↓
Debit ₹800
   ↓
COMMIT
   ↓
UNLOCK

Request B
   ↓
Waits for lock
   ↓
Reads balance = ₹200
   ↓
₹800 not available
   ↓
FAIL
```

This prevents the classic concurrent double-spend race.

### Spring Example

```java
@Transactional
public TransferResult transferMoney(TransferRequest request) {
    // Load sender and receiver with an appropriate write lock
    // Validate balance and wallet state
    // Debit sender
    // Credit receiver
    // Persist transaction
    // Persist ledger entries
    // Commit atomically
}
```

For actual implementation, use a repository query with an appropriate JPA lock mode such as `PESSIMISTIC_WRITE` for the critical wallet reads.

---

## 14. Double-Entry Ledger

A transfer of ₹500 should create two balanced accounting entries.

```text
Ayush Wallet
DEBIT  ₹500

Rahul Wallet
CREDIT ₹500
```

### Ledger Table

```text
ledger_entries
--------------------------------
id
transaction_id
wallet_id
entry_type
amount
balance_after
created_at
```

### Why Double-Entry?

A simple balance column tells you the current amount but does not provide a complete accounting history.

The ledger lets you ask:

```text
Where did this amount come from?
Why did this balance change?
Which transaction caused it?
What were the two sides of the transfer?
```

### Ledger Invariant

For every money-moving transaction:

```text
Total Debits == Total Credits
```

The ledger should be append-oriented. Corrections should preferably be represented by new compensating entries rather than deleting historical financial records.

---

## 15. Idempotency

Idempotency ensures that retrying the same logical payment does not create duplicate money movement.

### Example

Client sends:

```http
POST /api/v1/payments
Idempotency-Key: 92d8f1a
```

The request succeeds on the server, but the network drops before the client receives the response.

The client retries:

```text
Same Idempotency-Key
         ↓
Check existing record
         ↓
Already processed
         ↓
Return original result
```

### Suggested Table

```text
idempotency_records
--------------------------------
id
idempotency_key
user_id
request_hash
response_status
response_body
created_at
expires_at
```

### Important Rules

- Idempotency keys must have an appropriate uniqueness constraint.
- Store enough information to ensure the retry represents the same request.
- Do not treat two different payloads using the same key as the same operation.
- Do not perform the financial side effect before the idempotency decision is made.

---

## 16. Merchant Payments

Merchants have their own wallet.

```text
Customer Wallet
      │
      │ ₹999
      ▼
Payment Service
      │
      ▼
Merchant Wallet
```

### Example Request

```http
POST /api/v1/payments
Authorization: Bearer <JWT>
Idempotency-Key: payment-abc123
Content-Type: application/json
```

```json
{
  "merchantId": "M1001",
  "amount": 999.00,
  "currency": "INR",
  "description": "Demo purchase"
}
```

### Processing

```text
Validate merchant
      ↓
Validate customer wallet
      ↓
Validate amount
      ↓
Check idempotency
      ↓
Acquire locks
      ↓
Debit customer
      ↓
Credit merchant
      ↓
Create transaction
      ↓
Create ledger entries
      ↓
COMMIT
      ↓
Publish event
```

---

## 17. Payment State Machine

Use explicit states instead of only `SUCCESS`/`FAILED`.

### Payment

```text
PENDING
   ↓
PROCESSING
   ├──────────────→ FAILED
   ↓
SUCCESS
   ↓
REFUND_REQUESTED
   ↓
REFUNDED
```

Possible status values:

```text
PENDING
PROCESSING
SUCCESS
FAILED
REFUND_REQUESTED
REFUNDED
```

State transitions should be validated so that invalid transitions cannot occur.

---

## 18. Refund System

A refund should not delete or overwrite the original payment.

Example:

```text
Original Payment
₹1,000
   ↓
Refund
₹1,000
```

The history becomes:

```text
PAYMENT ₹1,000
REFUND  ₹1,000
```

### Refund Flow

```text
Payment = SUCCESS
       ↓
Validate refund request
       ↓
Create refund transaction
       ↓
Create compensating ledger entries
       ↓
Update refund/payment state
       ↓
Commit
       ↓
Publish refund event
```

### API

```http
POST /api/v1/payments/{paymentId}/refund
GET  /api/v1/refunds/{refundId}
```

---

## 19. Kafka Event Processing

Kafka handles asynchronous domain events.

### Suggested Topics

```text
payment.created
payment.success
payment.failed
payment.refunded
transfer.completed
wallet.updated
user.registered
```

### Architecture

```text
Payment Service
      │
      ▼
    Kafka
      │
 ┌────┼─────────────┐
 ▼    ▼             ▼
Email  SMS      Analytics
Consumer Consumer Consumer
```

### Example Event

```json
{
  "eventId": "evt-123",
  "eventType": "PAYMENT_SUCCESS",
  "transactionId": "TXN-938471",
  "userId": "USR-1001",
  "amount": 999.00,
  "currency": "INR",
  "occurredAt": "2026-09-11T10:00:00Z"
}
```

### Kafka Design Principles

- Events should have unique IDs.
- Consumers should be designed for safe retries.
- Consumer processing should be idempotent where required.
- Payment completion should not depend on an email provider being available.
- Kafka should not replace PostgreSQL as the financial source of truth.

---

## 20. Redis Usage

Redis is intentionally used for **non-authoritative, low-latency workloads**.

### Good Use Cases

#### Rate limiting

```text
USER → Redis Counter → Allow/Reject
```

#### Temporary state

- OTP metadata
- short-lived state
- request throttling information

#### Cache

- Merchant metadata
- Frequently read reference data
- Non-authoritative read models

### Critical Rule

Do not use a potentially stale Redis balance as the final authorization source for a money transfer.

Financial authorization should be based on authoritative transactional state in PostgreSQL.

---

## 21. PostgreSQL Database Design

Core entities:

```text
users
wallets
merchants
transactions
ledger_entries
refunds
idempotency_records
payment_methods
notifications
audit_logs
```

### Relationships

```text
USER
 │
 └──── WALLET
          │
          ├──── TRANSACTIONS
          │
          └──── LEDGER_ENTRIES

USER
 │
 └──── MERCHANT
          │
          └──── WALLET

TRANSACTION
    │
    └──── REFUND

USER
 │
 └──── AUDIT_LOG
```

---

## 22. Database Tables

### `users`

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### `wallets`

```sql
CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    currency CHAR(3) NOT NULL,
    balance NUMERIC(19,2) NOT NULL,
    version BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_wallet_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT positive_balance
        CHECK (balance >= 0)
);
```

### `transactions`

```sql
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    sender_wallet_id UUID,
    receiver_wallet_id UUID,
    amount NUMERIC(19,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,

    CONSTRAINT positive_amount
        CHECK (amount > 0)
);
```

### `ledger_entries`

```sql
CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    wallet_id UUID NOT NULL,
    entry_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    balance_after NUMERIC(19,2),
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT positive_ledger_amount
        CHECK (amount > 0)
);
```

### `idempotency_records`

```sql
CREATE TABLE idempotency_records (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    request_hash CHAR(64) NOT NULL,
    response_status INTEGER,
    response_body JSONB,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,

    UNIQUE (user_id, idempotency_key)
);
```

### `audit_logs`

```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    actor_id UUID,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50),
    resource_id UUID,
    ip_address INET,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL
);
```

The exact schema can be extended as implementation requirements become clearer.

---

## 23. Database Integrity & Indexing

### Important Constraints

Use:

- Primary keys
- Foreign keys
- Unique constraints
- Check constraints
- Not-null constraints

### Recommended Indexes

```sql
CREATE INDEX idx_transactions_sender
ON transactions(sender_wallet_id);

CREATE INDEX idx_transactions_receiver
ON transactions(receiver_wallet_id);

CREATE INDEX idx_transactions_created_at
ON transactions(created_at);

CREATE INDEX idx_ledger_wallet
ON ledger_entries(wallet_id);

CREATE INDEX idx_ledger_transaction
ON ledger_entries(transaction_id);
```

### Query Optimization

Use:

```sql
EXPLAIN ANALYZE
SELECT ...
```

for important queries.

Avoid adding indexes blindly. Every index has storage and write-maintenance cost.

---

## 24. REST API Design

All APIs should be versioned:

```text
/api/v1/auth
/api/v1/users
/api/v1/wallets
/api/v1/transfers
/api/v1/payments
/api/v1/refunds
/api/v1/transactions
/api/v1/merchants
/api/v1/admin
```

### Authentication

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
```

### Users

```http
GET  /api/v1/users/me
PUT  /api/v1/users/me
```

### Wallets

```http
POST /api/v1/wallets
GET  /api/v1/wallets/me
GET  /api/v1/wallets/balance
POST /api/v1/wallets/topup
```

### Transfers

```http
POST /api/v1/transfers
GET  /api/v1/transfers/{id}
```

### Payments

```http
POST /api/v1/payments
GET  /api/v1/payments/{id}
```

### Refunds

```http
POST /api/v1/payments/{id}/refund
GET  /api/v1/refunds/{id}
```

### Transactions

```http
GET /api/v1/transactions
GET /api/v1/transactions/{id}
```

### Merchants

```http
POST /api/v1/merchants
GET  /api/v1/merchants/{id}
PUT  /api/v1/merchants/{id}
```

### Admin

```http
GET  /api/v1/admin/users
GET  /api/v1/admin/transactions
POST /api/v1/admin/wallets/{id}/freeze
POST /api/v1/admin/wallets/{id}/unfreeze
GET  /api/v1/admin/statistics
```

---

## 25. API Error Handling

Create consistent error responses.

### Example

```json
{
  "timestamp": "2026-09-11T10:00:00Z",
  "status": 422,
  "code": "INSUFFICIENT_BALANCE",
  "message": "Insufficient wallet balance",
  "traceId": "7ac31abc"
}
```

### Recommended Error Codes

```text
UNAUTHORIZED
FORBIDDEN
USER_NOT_FOUND
WALLET_NOT_FOUND
MERCHANT_NOT_FOUND
INVALID_AMOUNT
INSUFFICIENT_BALANCE
WALLET_FROZEN
PAYMENT_NOT_FOUND
DUPLICATE_REQUEST
INVALID_STATE_TRANSITION
RATE_LIMIT_EXCEEDED
INTERNAL_ERROR
```

Use a global exception handler so all endpoints follow the same response structure.

---

## 26. Security

### Authentication

- Spring Security
- JWT access tokens
- Refresh tokens
- Secure password hashing

### Authorization

Use RBAC:

```text
CUSTOMER
MERCHANT
ADMIN
```

### Resource Ownership

Never trust a client-supplied wallet ID by itself.

Verify:

```text
Authenticated user
        ↓
Owns resource?
        ↓
YES → continue
NO  → reject
```

### Other Security Measures

- Request validation
- Rate limiting
- Secure headers
- Audit logging
- Minimal error details
- No passwords in logs
- No secret values in source code
- Environment-based configuration
- HTTPS in deployed environments

---

## 27. Rate Limiting

Protect sensitive endpoints.

Example policy:

```text
POST /auth/login
→ 5 attempts / minute / account or client key

POST /payments
→ project-defined request threshold

POST /transfers
→ project-defined request threshold
```

Redis can maintain short-lived request counters.

Do not hard-code a rate limit in documentation as a production guarantee unless it has actually been implemented and tested.

---

## 28. Audit Logging

Audit events should include:

```text
actor_id
action
resource_type
resource_id
timestamp
source/IP metadata where appropriate
metadata
```

Examples:

```text
LOGIN_SUCCESS
LOGIN_FAILURE
WALLET_CREATED
WALLET_FROZEN
TRANSFER_CREATED
PAYMENT_SUCCESS
PAYMENT_FAILED
REFUND_CREATED
ADMIN_ACTION
```

Audit data should be append-oriented and protected from ordinary user modification.

---

## 29. Project Structure

Recommended feature-oriented package structure:

```text
src/main/java/com/example/payment/

├── auth/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
│
├── user/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
│
├── wallet/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
│
├── transfer/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
│
├── payment/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
│
├── refund/
├── ledger/
├── merchant/
├── notification/
├── audit/
├── admin/
│
└── common/
    ├── config/
    ├── exception/
    ├── security/
    ├── response/
    └── util/
```

---

## 30. Testing Strategy

Testing should cover both ordinary functionality and failure/concurrency behavior.

### Unit Tests

Test:

- TransferService
- WalletService
- PaymentService
- RefundService
- IdempotencyService
- Security rules
- Validation

### Integration Tests

Test:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Real PostgreSQL
```

### Critical Test Cases

```text
✓ Successful transfer
✓ Insufficient balance
✓ Non-existent receiver
✓ Frozen wallet
✓ Closed wallet
✓ Invalid amount
✓ Duplicate payment request
✓ Same idempotency key with different payload
✓ Successful refund
✓ Failed payment
✓ Unauthorized resource access
✓ Concurrent transfer attempts
✓ Database rollback after an injected failure
```

---

## 31. Testcontainers

Use Testcontainers for realistic integration tests.

```text
JUnit
  ↓
Testcontainers
  ↓
PostgreSQL container
  ↓
Spring Boot test
```

Optional containers:

```text
PostgreSQL
Redis
Kafka
```

This avoids relying only on a developer's local database and gives CI a reproducible environment.

---

## 32. Docker & Local Development

Use Docker Compose for local infrastructure.

Example services:

```text
app
postgres
redis
kafka
```

Conceptual setup:

```text
docker-compose
     │
 ┌───┼───────────┐
 ▼   ▼           ▼
DB  Redis       Kafka
 │
 └── Spring Boot
```

### Typical Commands

```bash
mvn clean test
mvn spring-boot:run
docker compose up -d
docker compose down
```

Adjust commands if the final project uses a separate profile or compose file.

---

## 33. Database Migrations

Use Flyway instead of manually editing production schemas.

Example:

```text
src/main/resources/db/migration/

V1__create_users.sql
V2__create_wallets.sql
V3__create_transactions.sql
V4__create_ledger_entries.sql
V5__create_merchants.sql
V6__create_refunds.sql
V7__create_idempotency_records.sql
V8__create_audit_logs.sql
```

Every schema change should be represented by a migration.

---

## 34. OpenAPI / Swagger

The API documentation should include:

- Endpoint
- HTTP method
- Authentication requirement
- Request body
- Response body
- Validation rules
- HTTP status codes
- Error examples

Example:

```text
POST /api/v1/transfers

200 SUCCESS
400 BAD_REQUEST
401 UNAUTHORIZED
404 NOT_FOUND
409 CONFLICT
422 UNPROCESSABLE_ENTITY
```

---

## 35. Observability

Recommended:

```text
Spring Boot Actuator
Micrometer
Prometheus
Grafana
```

Track:

```text
Request count
Response latency
Error rate
Database pool utilization
JVM memory
JVM threads
Kafka consumer lag
Cache hit/miss rate
```

Use structured logs and include a trace/request identifier so a payment can be followed across API logs and asynchronous processing.

---

## 36. Performance Testing

Use k6 or JMeter.

Test scenarios:

```text
Login load
Wallet balance reads
Transaction history reads
Concurrent transfers
Merchant payment load
```

Measure:

```text
Requests/sec
p50 latency
p95 latency
p99 latency
Error rate
DB utilization
Kafka lag
```

### Resume Rule

Only report measured numbers.

Example format:

```text
"Achieved <actual-throughput> requests/sec at <actual-p95> p95 latency
under a <actual-concurrency>-user load test."
```

Never invent benchmark numbers.

---

## 37. Development Roadmap

### Phase 1 — Foundation

```text
User
Authentication
JWT
Wallet
PostgreSQL
REST API
```

Deliverables:

- Registration
- Login
- Wallet creation
- Balance retrieval

### Phase 2 — Financial Core

```text
Transfers
Transactions
BigDecimal
ACID
Row-level locking
Double-entry ledger
```

This is the most important phase.

### Phase 3 — Reliability

```text
Idempotency
Refunds
Audit logging
Rate limiting
Error handling
```

### Phase 4 — Distributed Features

```text
Redis
Kafka
Notifications
Event consumers
```

### Phase 5 — Productionization

```text
Docker
Testcontainers
OpenAPI
Actuator
Prometheus
Grafana
GitHub Actions
Load testing
```

---

## 38. Example Payment Flow

Example:

```text
Customer: Ayush
Merchant: DemoStore
Amount: ₹750
```

### Step-by-Step

```text
1. Ayush authenticates using JWT.

2. Client sends payment request:
   POST /api/v1/payments

3. Server validates:
   - JWT
   - merchant
   - amount
   - wallet status
   - idempotency key

4. Server begins PostgreSQL transaction.

5. Customer wallet is locked.

6. Merchant wallet is locked in a deterministic order.

7. Customer balance is checked.

8. Customer wallet is debited ₹750.

9. Merchant wallet is credited ₹750.

10. Payment transaction is created.

11. Debit ledger entry is created.

12. Credit ledger entry is created.

13. Database transaction commits.

14. Payment success event is published.

15. Kafka consumers process notification/analytics work.

16. API returns success.
```

---

## 39. Failure Scenarios

### Scenario A — Insufficient Funds

```text
Balance = ₹500
Payment = ₹750

→ Reject
→ No debit
→ No credit
→ No successful ledger transaction
```

### Scenario B — Duplicate Request

```text
Request 1
Idempotency-Key = X
→ SUCCESS

Request 2
Idempotency-Key = X
→ Return original result
→ No second debit
```

### Scenario C — Concurrent Transfers

```text
Balance = ₹1,000

Request A → ₹800
Request B → ₹800

→ One transfer succeeds
→ Other observes insufficient balance after locking
```

### Scenario D — Database Failure

```text
Debit succeeds
Credit fails

→ DB transaction rolls back
→ Debit is undone
→ No partial financial state remains
```

### Scenario E — Notification Failure

```text
Payment committed
Notification service unavailable

→ Financial transaction remains successful
→ Kafka event remains available for processing/retry
```

This is why payment processing and notifications should not be tightly coupled.

---

## 40. Design Decisions

### Why PostgreSQL?

- Strong relational consistency
- ACID transactions
- Foreign keys and constraints
- Locking support
- Excellent SQL capability
- Good indexing/query optimization

### Why Spring Boot?

- Mature Java backend ecosystem
- Dependency injection
- REST support
- Security
- Validation
- Transactions
- Data access integrations
- Kafka and Redis integrations

### Why BigDecimal?

Exact decimal arithmetic is required for monetary calculations.

### Why row-level locking?

To serialize critical wallet modifications and prevent stale-balance decisions under concurrent requests.

### Why double-entry ledger?

To maintain a traceable and auditable representation of money movement.

### Why idempotency?

To make retries safe and prevent duplicate financial side effects.

### Why Kafka?

To decouple asynchronous work such as notifications and analytics from the critical transaction path.

### Why Redis?

To provide low-latency caching and rate limiting for non-authoritative information.

### Why modular monolith first?

A modular monolith is easier to develop, test, debug, and reason about while still allowing clean boundaries that can later be extracted into services.

---

## 41. Future Enhancements

Potential future improvements:

- Real payment gateway integration using a sandbox
- UPI-like payment identifiers for a demo environment
- Multi-currency wallets
- FX conversion service
- Scheduled payments
- Recurring payments
- Merchant settlement batches
- Fraud/risk rules
- OTP / MFA
- Device management
- WebSocket-based payment updates
- Outbox pattern
- Dead-letter queue
- Event replay support
- Advanced reconciliation
- Read replicas
- Sharding strategy discussion
- Kubernetes deployment
- Distributed tracing with OpenTelemetry
- Cloud deployment
- Secrets management
- CI/CD environments

Any enhancement should be implemented only when its architectural value is clear.

---

## 42. Resume Highlights

### Recommended Resume Entry

**Digital Payment & Wallet Platform | Java, Spring Boot, PostgreSQL, Redis, Kafka**

- Developed REST APIs for wallet management, peer-to-peer transfers, merchant payments, transaction history, and refunds using Spring Boot and PostgreSQL.
- Implemented **ACID-compliant money transfers with pessimistic row-level locking** to prevent concurrent balance corruption and double spending.
- Designed a **double-entry ledger and idempotency mechanism** to preserve transaction integrity and prevent duplicate payment processing.
- Integrated **Kafka-based asynchronous events** for notifications and downstream processing and Redis for caching/rate limiting while keeping PostgreSQL as the financial source of truth.
- Implemented JWT/RBAC security, PostgreSQL indexing, Flyway migrations, Testcontainers-based integration tests, Docker deployment, OpenAPI documentation, and application monitoring.

### Important

Do not claim:

- Production payment processing
- Real financial transactions
- Specific throughput
- Specific latency
- Percentage improvements

unless those things were actually implemented and measured.

---

## 43. Interview Topics Demonstrated

This project can support discussions around:

### Java

- OOP
- Collections
- Exceptions
- Streams
- `BigDecimal`
- Concurrency
- Thread safety

### Spring Boot

- Dependency Injection
- REST
- DTOs
- Validation
- Transactions
- Security
- JPA
- Exception handling

### PostgreSQL

- ACID
- Transactions
- Isolation
- Row-level locking
- Constraints
- Foreign keys
- Indexing
- Query planning
- `EXPLAIN ANALYZE`

### Distributed Systems

- Kafka
- Event-driven architecture
- Idempotent consumers
- Retry handling
- Eventual consistency for non-critical downstream work
- Failure isolation

### System Design

- Consistency
- Availability trade-offs
- Caching
- Rate limiting
- Auditability
- Scalability
- Modular architecture

---

## 44. Running the Project

### Prerequisites

Install:

```text
Java 25
Maven
Docker
Docker Compose
Git
```

### Clone

```bash
git clone <your-repository-url>
cd digital-payment-wallet-platform
```

### Start Infrastructure

```bash
docker compose up -d
```

### Build

```bash
mvn clean package
```

### Run Tests

```bash
mvn test
```

### Run Application

```bash
mvn spring-boot:run
```

### API Documentation

After startup, expose the project's configured Swagger/OpenAPI endpoint, for example:

```text
http://localhost:8080/swagger-ui.html
```

Use the actual configured endpoint from the final application.

---

## 45. Project Status

Suggested implementation status:

```text
[ ] Authentication
[ ] User management
[ ] Wallet creation
[ ] Wallet balance
[ ] Top-up
[ ] Peer-to-peer transfers
[ ] Merchant payments
[ ] Double-entry ledger
[ ] Row-level locking
[ ] Idempotency
[ ] Refunds
[ ] Kafka events
[ ] Redis caching
[ ] Redis rate limiting
[ ] Audit logging
[ ] Flyway migrations
[ ] OpenAPI documentation
[ ] Unit tests
[ ] Integration tests
[ ] Testcontainers
[ ] Docker Compose
[ ] Actuator
[ ] Prometheus
[ ] Grafana
[ ] Load testing
[ ] GitHub Actions
```

Update the checklist as each component is actually completed.

---

## 46. Disclaimer

This repository is a **learning and portfolio project** inspired by real-world payment-system design.

It should not be used to process real customer funds without a proper security review, compliance program, financial controls, operational controls, secrets management, and certified third-party integrations.

For portfolio development, use a mock/sandbox gateway and synthetic data.

---

## Suggested Repository Name

```text
digital-payment-wallet-platform
```

## Suggested GitHub Description

```text
Production-inspired digital payment and wallet backend built with Java, Spring Boot, PostgreSQL, Redis, and Kafka, featuring ACID transfers, row-level locking, double-entry ledger accounting, idempotency, merchant payments, refunds, and event-driven processing.
```

## Core Engineering Statement

> **PostgreSQL owns financial truth. Transactions protect atomicity. Row-level locks protect concurrency. The double-entry ledger preserves auditability. Idempotency makes retries safe. Kafka decouples asynchronous processing. Redis accelerates non-authoritative workloads.**
