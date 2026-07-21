# 💳 Wallet Service

> A UPI-style digital wallet microservice for the **Banking Application** platform — load money from your bank, spend it at merchants, and pull it right back to your account. Built with Spring Boot, secured by design.

<p align="left">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-4.0.7-6DB33F?logo=springboot&logoColor=white">
  <img alt="Spring Cloud" src="https://img.shields.io/badge/Spring%20Cloud-2025.1.2-6DB33F?logo=spring&logoColor=white">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-wallet__db-4479A1?logo=mysql&logoColor=white">
  <img alt="OpenFeign" src="https://img.shields.io/badge/OpenFeign-enabled-blueviolet">
  <img alt="Eureka" src="https://img.shields.io/badge/Eureka-client-critical">
  <img alt="Swagger" src="https://img.shields.io/badge/Swagger-UI-85EA2D?logo=swagger&logoColor=black">
</p>

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Core Features](#-core-features)
- [The Golden Rule — Why Wallet-to-Wallet is Blocked](#-the-golden-rule--why-wallet-to-wallet-is-blocked)
- [Architecture](#-architecture)
- [Domain Model — Why These Entities?](#-domain-model--why-these-entities)
- [API Reference](#-api-reference)
- [Money Flows](#-money-flows)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)

---

## 🌟 Overview

The **Wallet Service** is a microservice that gives every customer a **PhonePe / Paytm–style wallet** on top of their existing bank accounts. It sits inside a larger banking system and talks to sibling services over **OpenFeign** for customer verification, account debits/credits, and audit logging.

A wallet here is deliberately **not** a peer-to-peer money box. It is a **spending instrument**:

1. You **load** money into it from your own **savings account**.
2. You **spend** it — but only at **verified merchant scanners**.
3. You can **withdraw** the balance **back to the same account** you own.

That's it. No sending money to a friend's wallet. Ever. (More on *why* below — this is the heart of the design.)

- **Port:** `9000`
- **Service name (Eureka):** `WALLET-SERVICE`
- **Base path:** `/wallet`
- **Database:** `wallet_db` (MySQL)

---

## ✨ Core Features

### 1️⃣ Withdraw to the Same Account
A customer can pull money **out of the wallet and back into their own bank account** — the reverse of a top-up.

- Endpoint: `PUT /wallet/topup/withdraw`
- The wallet balance is debited, and the **same customer's account** is credited via the `account-service`.
- Guarded by a **sufficient-balance check** — you can never withdraw more than you hold.
- Provider must be `PHONEPE` or `PAYTM`, validated at the controller boundary.

> 🔒 **Why "same account"?** The withdraw path is a *return* channel, not a transfer channel. Money that entered from a bank account can only flow back out to a bank account owned by the same customer — it can never become a payment to someone else. This keeps the wallet a closed loop between *you* and *your* bank.

### 2️⃣ UPI Payments
The wallet supports **UPI-style merchant payments** with two hard rules baked into the design:

| Scenario | Allowed? | Reason |
|---|---|---|
| 💸 **Wallet → Wallet** (friend to friend) | ❌ **Never** | Not a P2P transfer product — see [The Golden Rule](#-the-golden-rule--why-wallet-to-wallet-is-blocked) |
| 🏪 **Scan & Pay a Merchant** (PhonePe scanner) | ✅ **Yes** | Only scanners registered as **merchants** can receive money |
| 🧑‍🤝‍🧑 **Scan & Pay a Friend** | ❌ **No** | A personal/customer scanner is not a valid payee |

- Endpoint: `POST /wallet/pay`
- A payment resolves the destination **scanner by its bank account**. If no merchant scanner is registered for that account → `Merchant Not Found`.
- Every successful payment writes an immutable **`WalletHistory`** row with a generated `UPI Transaction ID` and `Reference Number`.

---

## 🛡️ The Golden Rule — Why Wallet-to-Wallet is Blocked

> **Money can never move directly from one wallet to another wallet — in any scenario.**

This is the single most important design decision in the service, and it is enforced structurally, not just by an `if` statement.

**How is it enforced?**
The `payToMerchant` flow does **not** accept a destination *wallet*. It accepts an **account number**, and that account number is looked up in the **`Scanner`** table:

```java
Scanner scanner = scannerRepository.findByBankAccount(accountNumber)
        .orElseThrow(() -> new RuntimeException("Merchant Not Found"));
```

If the account doesn't belong to a registered scanner, the payment is rejected. There is **no code path** that credits another wallet's `balance`. A wallet can only ever be credited from:
- a **top-up** (money coming from the owner's own bank account), or
- being debited (spend / withdraw).

**Why this rule exists:**

| Motivation | What it prevents |
|---|---|
| 🚫 **Not a P2P money-transfer license** | Peer-to-peer wallet transfers carry regulatory weight this service intentionally avoids. |
| 🕵️ **Anti-money-laundering (AML)** | Blocking wallet→wallet chains removes an easy way to shuffle funds anonymously between users. |
| 🧾 **Auditability** | Every rupee that leaves a wallet lands in a *known* bank account tied to a *known* merchant scanner, captured in `WalletHistory`. |
| 🎯 **Product clarity** | The wallet is a **spending wallet** (load → spend at merchants → withdraw), not a social payments app. |

**Merchant-only scanner payments:**
Even the scan-and-pay flow is restricted. A scanner is only a valid payee when it is a **merchant** (`ScannerCategory.MERCHANT`). A personal/`CUSTOMER` scanner — i.e. paying a friend — is not a receivable destination. This is why the `Scanner` entity carries both a `category` and a `merchantName`: the system needs to know *who* is on the other side and *whether they're allowed to be paid*.

---

## 🏛️ Architecture

The Wallet Service is a **Eureka-registered** microservice that orchestrates three downstream services via **OpenFeign**.

```
                         ┌──────────────────────────┐
                         │      API Gateway          │
                         └────────────┬─────────────┘
                                      │  /wallet/**
                                      ▼
        ┌───────────────────────────────────────────────────────┐
        │                    WALLET-SERVICE  (:9000)             │
        │                                                        │
        │   WalletController → WalletService → Repositories      │
        │                          │                             │
        │              ┌───────────┼────────────┐                │
        └──────────────┼───────────┼────────────┼───────────────┘
                       ▼           ▼            ▼
             ┌──────────────┐ ┌──────────┐ ┌──────────────┐
             │ customer-    │ │ account- │ │ audit-       │
             │ service      │ │ service  │ │ service      │
             │ (verify cust)│ │ (debit / │ │ (log errors) │
             │              │ │  credit) │ │              │
             └──────────────┘ └──────────┘ └──────────────┘
                       │
                       ▼
              ┌──────────────────┐
              │   MySQL: wallet_db │
              │  wallet · wallet_history · scanner │
              └──────────────────┘
```

**Feign clients**

| Client | Target service | Used for |
|---|---|---|
| `CustomerFeignClient` | `customer-service` | Verify the customer exists before creating a wallet |
| `AccountFeignClient` | `account-service` | List savings accounts, read balances, **withdraw** from & **deposit** to bank accounts |
| `AuditClient` | `audit-service` | Fire-and-forget error logging (`logFailure`) — failures here never break the main flow |

---

## 🧩 Domain Model — Why These Entities?

Three entities model the whole system. Each exists for a deliberate reason.

### 🪪 `Wallet` — *the customer's spending instrument*
The core account holding a `balance`. Chosen as its own entity (separate from a bank account) because a wallet has a **different lifecycle and rules** than a bank account.

| Field | Why it's here |
|---|---|
| `walletId` | Public, human-friendly id (`WLT-xxxxxxxx`) — decoupled from the DB primary key |
| `customerId` | Links the wallet to its owner (verified via `customer-service`) |
| `walletProvider` | `PHONEPE` / `PAYTM` — a customer may hold **one wallet per provider**, enforced on creation |
| `balance` | `BigDecimal(19,2)` — money is **never** stored as a float |
| `scannerCategory` | Marks the wallet's role (`CUSTOMER` by default) |
| `status` | `ACTIVE` / `BLOCKED` / `CLOSED` lifecycle control |
| `dailyTransferAmount` / `dailyTransferDate` | Groundwork for **per-day spending limits** (velocity checks) |
| `createdAt` / `updatedAt` / `deletedAt` | Full audit + **soft-delete** support |

> **Why one wallet per provider per customer?** Creation checks existing wallets and rejects a duplicate `PHONEPE`/`PAYTM` wallet. This mirrors real UPI apps — you don't have two PhonePe wallets on one number.

### 🧾 `WalletHistory` — *the immutable ledger*
A **write-once transaction record** for every payment. It's a separate entity (not just columns on `Wallet`) because a ledger must be **append-only and independently queryable**, while a wallet's balance is mutable.

| Field | Purpose |
|---|---|
| `transactionType` | e.g. `UPI` |
| `amount` / `status` | How much moved and whether it `COMPLETED` |
| `upiTransactionId` / `referenceNumber` | Generated trace ids for reconciliation & customer receipts |
| `source` / `destination` | Payer wallet → payee account |
| `merchantId` / `scannerId` | **Which merchant** received the money (the accountability anchor) |
| `failureReason` | Populated when a transaction fails, for audit |
| `createdAt` | `updatable = false` — history is never rewritten |

> **Why store the merchant + scanner id on every row?** Because of the [Golden Rule](#-the-golden-rule--why-wallet-to-wallet-is-blocked): every outbound rupee must be traceable to a *named, registered merchant*. This entity is what makes the "no anonymous transfers" promise auditable.

### 🏪 `Scanner` — *the gatekeeper of who can be paid*
Represents a **registered QR/UPI scanner**. This entity is the **enforcement point** for merchant-only payments.

| Field | Purpose |
|---|---|
| `scannerId` | The QR/scanner identity (primary key) |
| `merchantName` | Human-readable payee name (shown in remarks/receipts) |
| `bankAccount` | The **only** destination money can land in — looked up during payment |
| `category` | `MERCHANT` vs `CUSTOMER` — **only merchants are payable** |
| `status` | Active/inactive control of the scanner |

> **Why does `Scanner` exist at all?** Without it, "pay by scanning" would just be "send to any account/wallet" — exactly the P2P behaviour the service forbids. The `Scanner` table is the allow-list: if your account isn't a merchant scanner, you cannot be paid. That single lookup is what turns "UPI payment" into "**merchant-only** UPI payment."

### 🔢 Enums — *typed, not stringly*

| Enum | Values | Role |
|---|---|---|
| `WalletProvider` | `PHONEPE`, `PAYTM` | Which UPI provider backs the wallet |
| `ScannerCategory` | `CUSTOMER`, `MERCHANT` | Distinguishes payable merchants from non-payable customers |
| `WalletStatus` | `ACTIVE`, `BLOCKED`, `CLOSED` | Wallet lifecycle |
| `AccountType` | `SAVINGS`, `CURRENT` | Used to filter top-up sources to **savings only** |

---

## 📡 API Reference

Base path: **`/wallet`** · Interactive docs: **`/swagger-ui.html`**

### Create a wallet
```http
POST /wallet/create
```
Verifies the customer via `customer-service`, rejects duplicate providers, and initialises the wallet with a zero balance and `ACTIVE` status.

### List top-up sources (savings accounts)
```http
GET /wallet/topup/{customerId}
```
Returns only the customer's **`SAVINGS`** accounts — the eligible sources to load money from.

### Load money into the wallet
```http
PUT /wallet/topup/update?customerId=&accountNumber=&walletProvider=
Body: { "balance": 500.00 }
```
Debits the bank account and credits the wallet. Rejects if the account has insufficient funds or the provider isn't `PHONEPE`/`PAYTM`.

### 💰 Withdraw back to the same account  *(Feature 1)*
```http
PUT /wallet/topup/withdraw?customerId=&accountNumber=&walletProvider=
Body: { "amount": 200.00 }
```
Debits the wallet and deposits into the **owner's own account**. Blocked on insufficient wallet balance.

### 🏪 Pay a merchant  *(Feature 2 — UPI)*
```http
POST /wallet/pay?walletId=&accountNumber=
Body: { "amount": 150.00 }
```
Resolves the merchant `Scanner` by `accountNumber`, debits the wallet, deposits to the merchant, and records a `WalletHistory` entry. Fails with `Merchant Not Found` if the account isn't a registered merchant scanner — **this is where wallet-to-friend payments are blocked.**

> ⚠️ Errors on every endpoint are caught, logged to `audit-service` via `logFailure(...)`, and re-thrown — so no failure goes unrecorded.

---

## 🔁 Money Flows

**Top-up (bank → wallet)**
```
Customer → [account-service: withdraw] → wallet.balance += amount
```

**Withdraw (wallet → same bank account)** — *Feature 1*
```
wallet.balance -= amount → [account-service: deposit into owner's account]
```

**Pay merchant (wallet → merchant)** — *Feature 2*
```
Scanner lookup by account ──► merchant? ──yes──► wallet.balance -= amount
                                │                 [account-service: deposit to merchant]
                                │                 WalletHistory.save(COMPLETED)
                                └──no──► ❌ "Merchant Not Found"
```

**Wallet → Wallet** — *always*
```
❌ No code path exists. By design.
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | **Java 21** |
| Framework | **Spring Boot 4.0.7** (Web MVC, Data JPA, Validation, Actuator) |
| Cloud | **Spring Cloud 2025.1.2** — OpenFeign + Netflix Eureka client |
| Mapping | **MapStruct 1.6.3** (entity ↔ DTO) |
| Boilerplate | **Lombok** |
| Database | **MySQL** (`wallet_db`) |
| API Docs | **springdoc-openapi / Swagger UI 2.8.9** |

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven (wrapper included: `./mvnw`)
- A running MySQL instance with a `wallet_db` schema
- Sibling services on Eureka: `customer-service`, `account-service`, `audit-service` (+ a Eureka registry)

### Run it
```bash
# from the wallet-service directory
./mvnw spring-boot:run
```

The service starts on **`http://localhost:9000`**.

- Swagger UI → `http://localhost:9000/swagger-ui.html`
- OpenAPI spec → `http://localhost:9000/v3/api-docs`
- Health → `http://localhost:9000/actuator/health`

---

## ⚙️ Configuration

Key settings from `src/main/resources/application.yml`:

```yaml
server:
  port: 9000

spring:
  application:
    name: WALLET-SERVICE
  datasource:
    url: jdbc:mysql://localhost:3306/wallet_db
    username: root
    password: "put your password"
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

> 🔐 **Note:** The datasource password is currently hard-coded in `application.yml`. For any non-local environment, move it to an environment variable / secret manager before deploying.

---

<p align="center"><em>Load it. Spend it at merchants. Withdraw what's left — to your own account, and no one else's.</em> 💳</p>
