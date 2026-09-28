# 🏏 CricPulse - Live Cricket Score Management System

![Java 17](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-blue.svg)
![H2 Database](https://img.shields.io/badge/Database-H2-blueviolet.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)

> Developed by **Jasleen Kaur Multani** (ID: `12410014`)

---

## 📌 Project Overview
**CricPulse** is a feature-rich **Live Cricket Scoring and Match Management Platform** built with **Spring Boot**, **Spring Data JPA**, **H2 Database**, **Swagger OpenAPI**, and an interactive dark-themed **Web Dashboard**.

It simulates live international and league cricket matches (T20, ODI, Test), complete with ball-by-ball commentary, player statistics tracking, automated strike rotation logic, target chasing calculations, and instant simulation controls.

---

## ✨ Key Features

- **🏏 Live Match Management**: Create, schedule, and manage ongoing and completed cricket matches across T20, ODI, and Test formats.
- **📊 Real-time Scoring Engine**:
  - Ball-by-ball run recording (0, 1, 2, 3, 4, 6).
  - Extras handling (Wides, No-Balls, Byes, Leg-Byes).
  - Dismissals tracking (Bowled, Caught, LBW, Run Out, Stumped).
  - Strike rotation logic (odd runs & end of over strike swap, bowler changes, next batter entry).
- **⚡ Active Player Statistics**: Real-time tracking of striker, non-striker, and bowler statistics (Strike Rates, Economy Rates, 4s, 6s, Maidens, Wickets).
- **🎮 Simulation Engines**:
  - `⚡ Simulate 1 Ball`: Generates realistic single-ball outcomes with commentary.
  - `🚀 Fast-Forward Match`: Auto-simulates complete matches until completion.
  - `▶ Auto-Play Broadcast`: Continuous real-time ball updates every 2 seconds.
- **👥 Teams & Squad Roster**: Maintain teams and player profiles with jersey numbers, batting/bowling styles, and roles.
- **📖 OpenAPI / Swagger REST Specs**: Fully documented REST APIs accessible at `/swagger-ui.html`.
- **💾 Database Connectivity**: Built-in persistent H2 embedded database with console interface at `/h2-console`.

---

## 🛠 Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.5, Spring Data JPA, Hibernate, Maven
- **Database**: H2 Database (In-memory / Persistent)
- **API Documentation**: SpringDoc OpenAPI 2.5.0 (Swagger UI)
- **Frontend**: Vanilla HTML5, Vanilla CSS3 (Glassmorphism & Dark Neon Sports Theme), JavaScript (ES6 fetch API)

---

## 🏗 Architecture & Domain Model

```
Match (1) ───< Innings (2) ───< BallEvent (N)
  │                 │
  ├── Team A        ├── Striker & Non-Striker
  ├── Team B        └── Bowler
  └── Toss Winner   └── PlayerMatchStat (N)
```

---

## 🚀 REST API Endpoints

| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/matches` | Fetch all matches |
| **GET** | `/api/matches/summaries` | Fetch concise match scores & result summaries |
| **GET** | `/api/matches/{id}/scorecard` | Fetch live detailed scorecard, batter & bowler stats |
| **POST** | `/api/matches` | Create/Schedule a new cricket match |
| **POST** | `/api/matches/{id}/ball` | Record a manual ball event (runs, extras, wickets) |
| **POST** | `/api/matches/{id}/simulate-ball` | Auto-simulate single ball event |
| **POST** | `/api/matches/{id}/simulate-match` | Auto-simulate entire match to completion |
| **GET** | `/api/teams` | List all registered teams and squads |
| **POST** | `/api/teams` | Register a new team |
| **GET** | `/api/players` | Retrieve player profiles (filtered by `teamId`) |
| **POST** | `/api/players` | Add a new player to a team squad |

---

## 💻 Getting Started & Local Setup

### Prerequisites
- Java 17 or higher
- Apache Maven 3.8+

### Running the Application

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/jasleenk8999/CricPulse-Cricket-Score-Management-System.git
   cd CricPulse-Cricket-Score-Management-System
   ```

2. **Build and Run**:
   ```bash
   mvn clean spring-boot:run
   ```

3. **Access Application**:
   - **Dashboard**: [http://localhost:8080](http://localhost:8080)
   - **Swagger API Specs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) *(JDBC URL: `jdbc:h2:mem:cricketdb`, User: `sa`, Password: `password`)*

---

## 👤 Author
- **Jasleen Kaur Multani** (ID: `12410014`)
