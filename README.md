# 🎾 Scoring System – Tennis Kata with Kafka

This project implements a simple tennis game scoring engine.

It includes:

- A **Kafka producer** that takes a sequence like `"ABBABBBABABABABAAAB..."` and emits `BallWonEvent` events. The character ‘A’ corresponding to “player A won the ball”, and ‘B’ corresponding to “player B won the ball”. 
- A **Kafka consumer** that listens to those events and tracks the **live score**, following real tennis rules (including deuce, advantage, win). For example, reading the following sequence `“ABABAA”`, the consumer prints :

```
“Player A : 15 / Player B : 0”
“Player A : 15 / Player B : 15”
“Player A : 30 / Player B : 15”
“Player A : 30 / Player B : 30”
“Player A : 40 / Player B : 30”
“Player A wins the game
```

---

## 🧱 Tech Stack

- **Java 21**
- **Maven**
- **Spring Boot 3.4**
- **Apache Kafka**
- **Docker Compose**

---

## 🚀 Modules

This is a **multi-module Maven project**:

```
scoring-system/
├── producer/         # Spring Boot Kafka producer
├── consumer/         # Spring Boot Kafka consumer
├── common/           # Shared library (BallWonEvent DTO, etc.)
└── docker-compose.yml
```

---

## 🧪 Build the project

Before running the services, build the full project using Maven wrapper:

```bash
./mvnw clean install
```

This will compile and install all modules.

During the build, the following tests are executed:

- Unit tests for the `GameStateMachine` covering basic tennis scoring scenarios.
- Integration tests using Testcontainers:
  - `ProducerIntegrationTest` ensures Kafka events are correctly emitted.
  - `ConsumerIntegrationTest` ensures the consumer receives the sequence and updates the game status accordingly.

---

## 🏗️ How to Run

### ▶️ Start infrastructure

```bash
docker-compose up -d
```

Accessible services:
- AKHQ: http://localhost:8080

---

### ▶️ Run the producer with a custom sequence

```bash
./mvnw spring-boot:run -pl producer -Dspring-boot.run.arguments=--sequence=ABBABBBABABABABAAAB
```

---

### ▶️ Run the consumer

In another terminal, run:

```bash
./mvnw spring-boot:run -pl consumer
```

The consumer listens to the topic `ball-won-events` and prints updates like:

```
Player A : 15 / Player B : 0
Player A : 15 / Player B : 15
...
Deuce
Advantage A
Player A wins the game
```

---

## 📜 License

MIT – use it, break it, learn from it 🚀
