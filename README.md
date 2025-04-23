# 🎾 Tennis Scoring System

This repository hosts a simple tennis game scoring engine using **Kafka** and **Spring Boot**.

It features:

- 🟢 A **Kafka producer** that emits `BallWonEvent` messages based on a sequence like `"ABBABAABABABAB..."`, where each character represents the player who won the ball (`'A'` or `'B'`).

- 🟡 A **Kafka consumer** that listens to these events and updates the **live game score** using real tennis rules (including *deuce*, *advantage*, *win*).

📘 Example:

Given the input `"ABABAA"`, the output would be:

```
Player A : 15 / Player B : 0
Player A : 15 / Player B : 15
Player A : 30 / Player B : 15
Player A : 30 / Player B : 30
Player A : 40 / Player B : 30
Player A wins the game
```

---

## 🧱 Tech Stack

- **Java 21**
- **Maven**
- **Spring Boot 3.4**
- **Apache Kafka 7.5.0**
- **Docker Compose**
- **Testcontainers**

---

## 📁 Modules Overview

This is a **multi-module Maven project**:

```
scoring-system/
├── producer/         # Spring Boot Kafka producer
├── consumer/         # Spring Boot Kafka consumer
├── common/           # Shared library (BallWonEvent DTO, etc.)
└── docker-compose.yml
```

---

## 🧪 Building the Project

To build everything:

```bash
./mvnw clean install
```

This compiles all modules and runs the following tests:

- Unit tests for the tennis scoring logic (`GameStateMachine`).
- Integration tests (using Testcontainers):
  - `ProducerIntegrationTest` – verifies Kafka emits the correct events.
  - `ConsumerIntegrationTest` – verifies the score updates based on incoming events.

---

## 🏗️ Running the App

### 🧱 1. Start the infrastructure

```bash
docker-compose up -d
```

➡️ Available services:
- **AKHQ UI** (Kafka topic browser): http://localhost:8080

---

### 🎯 2. Run the Kafka Producer

```bash
./mvnw spring-boot:run -pl producer -Dspring-boot.run.arguments=--sequence=ABBABBBABABABABAAAB
```

This sends events to Kafka (topic `ball-won-events`) based on the sequence.

---

### 📢 3. Run the Kafka Consumer

In a separate terminal:

```bash
./mvnw spring-boot:run -pl consumer
```

➡️ The consumer listens to events and prints game status updates like:

```
Player A : 30 / Player B : 40
Deuce
Advantage A
Player A wins the game
```

---

## 📜 License

**MIT** — use it, break it, learn from it 🚀
