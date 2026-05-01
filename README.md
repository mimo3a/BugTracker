# BugTracker — SE II · Gruppe DE 3 · MCI Innsbruck

Eine schlanke Webanwendung zur strukturierten Erfassung und Verwaltung von Software-Fehlern (Bugs).  
Entwickelt im Rahmen des Kurses **Software Engineering II** bei Prof. Andrea Corradini.

---

## Inhaltsverzeichnis

- [Projektübersicht](#projektübersicht)
- [Features](#features)
- [Tech-Stack](#tech-stack)
- [Architektur](#architektur)
- [Voraussetzungen](#voraussetzungen)
- [Lokales Setup](#lokales-setup)
- [Tests ausführen](#tests-ausführen)
- [Projektstruktur](#projektstruktur)
- [Team](#team)
- [Dokumentation](#dokumentation)

---

## Projektübersicht

Kleine Teams arbeiten oft ohne dediziertes Bug-Tracking-Tool — Fehlermeldungen landen in E-Mails, Slack-Nachrichten oder gehen ganz verloren. Der **BugTracker** löst dieses Problem mit einer zentralen, intuitiv bedienbaren Webanwendung.

**Kernfunktionen auf einen Blick:**
- Bugs anlegen, bearbeiten und archivieren (Soft-Delete)
- Status-Workflow mit State Machine (NEU → IN_BEARBEITUNG → IM_REVIEW → ERLEDIGT)
- Bearbeiter zuweisen, nach Status filtern, nach Titel suchen
- Vollständige Änderungshistorie pro Bug
- Rollenbasierte Zugriffskontrolle (TESTER / DEVELOPER / ADMIN)
- Tag-Verwaltung durch Admins
- Session-basierte Authentifizierung (Login / Logout / Registrierung)

---

## Features

| Priorität | Feature |
|-----------|---------|
| **Muss** | Bug anlegen, anzeigen, bearbeiten, archivieren |
| **Muss** | Status-Workflow mit State Machine |
| **Muss** | Benutzeranmeldung (Login / Logout / Registrierung) |
| **Muss** | Rollenbasierte Zugriffskontrolle (TESTER, DEVELOPER, ADMIN) |
| **Soll** | Bearbeiter zuweisen, Priorität setzen |
| **Soll** | Nach Status filtern, Bug-Historie |
| **Soll** | Tags verwalten |
| **Kann** | Titelsuche, Kommentarfunktion |

---

## Tech-Stack

| Bereich | Technologie |
|---------|-------------|
| Backend | Java 21 · Spring Boot 3.x · Spring Web · Spring Security · Spring Data JDBC |
| Frontend | React 18+ · TypeScript · Vite · Tailwind CSS |
| Datenbank | PostgreSQL (Dev & Prod) |
| Migrationen | Flyway |
| Build | Maven (Backend) · Vite (Frontend) |
| Tests | JUnit 5 · Mockito · MockMvc · Vitest · Testing Library |
| CI/CD | GitHub Actions |
| Code-Qualität | JaCoCo · SonarLint |

---

## Architektur

```
┌─────────────────────┐        REST/JSON        ┌──────────────────────┐
│   React SPA         │ ◄──────────────────────► │  Spring Boot API     │
│   (TypeScript)      │    Session-Cookie Auth    │  (Java 21)           │
│   Vite · Tailwind   │                           │  Port 8080           │
└─────────────────────┘                           └──────────┬───────────┘
                                                             │
                                                             │ JDBC
                                                             ▼
                                                  ┌──────────────────────┐
                                                  │  PostgreSQL           │
                                                  │  Flyway Migrations    │
                                                  └──────────────────────┘
```

- **Frontend** und **Backend** sind vollständig getrennt und können unabhängig deployed werden
- Kommunikation ausschließlich über JSON-REST
- Auth über Session-Cookies (HttpOnly, Secure)
- Passwörter mit BCrypt gehasht (cost factor ≥ 10)

---

## Voraussetzungen

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.9+ |
| Node.js | 20+ |
| Docker + Docker Compose | aktuell |
| Git | aktuell |

---

## Lokales Setup

### 1. Repository klonen

```bash
git clone https://github.com/Cmort-bot/bug-tracker.git
cd bug-tracker
```

### 2. Datenbank starten (Docker)

```bash
docker-compose up -d
```

PostgreSQL läuft danach auf `localhost:5432`.  
Flyway-Migrationen werden beim Backend-Start automatisch ausgeführt.

### 3. Backend starten

```bash
cd backend
mvn spring-boot:run
```

API erreichbar unter: `http://localhost:8080`  
Health-Check: `http://localhost:8080/actuator/health`

### 4. Frontend starten

```bash
cd frontend
npm install
npm run dev
```

App erreichbar unter: `http://localhost:5173`

### 5. Demo-Daten laden (optional)

```bash
# Seed-Skript ausführen (lädt Beispiel-Bugs in verschiedenen Stati)
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments=--seed
```

---

## Tests ausführen

### Backend (JUnit + Mockito)

```bash
cd backend
mvn test
```

Coverage-Report (JaCoCo):

```bash
mvn verify
# Report: backend/target/site/jacoco/index.html
```

### Frontend (Vitest + Testing Library)

```bash
cd frontend
npm run test
```

---

## Projektstruktur

```
bug-tracker/
├── backend/
│   ├── src/
│   │   ├── main/java/at/mci/bugtracker/
│   │   │   ├── controller/      # REST-Controller
│   │   │   ├── service/         # Business-Logik
│   │   │   ├── dao/             # Datenbankzugriff (JDBC)
│   │   │   ├── model/           # Java Records / DTOs
│   │   │   ├── config/          # Spring Security, CORS
│   │   │   └── auth/            # Auth-Logik
│   │   └── resources/
│   │       ├── db/migration/    # Flyway SQL-Migrationen
│   │       └── application.yml
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── pages/               # Seiten (Login, Bugs, Detail, …)
│   │   ├── components/          # Wiederverwendbare Komponenten
│   │   ├── hooks/               # Custom React Hooks
│   │   └── lib/                 # API-Client, Utilities
│   ├── package.json
│   └── vite.config.ts
├── docs/
│   ├── lastenheft.pdf
│   ├── pflichtenheft.pdf
│   ├── presentation/
│   ├── usability/
│   └── deployments/
├── docker-compose.yml
├── .github/workflows/
│   ├── ci-backend.yml
│   └── ci-frontend.yml
└── README.md
```

---

## Team

| Name | E-Mail | Rolle |
|------|--------|-------|
| Amort Julian-Alexander | ju.amort@mci4me.at | Entwicklung |
| Demianov Oleksandr | o.demianov@mci4me.at | Entwicklung |
| Gulenko Maksym | m.gulenko@mci4me.at | Entwicklung |
| Lamesic Patrick | p.lamesic@mci4me.at | Entwicklung |
| Widner Josef | j.widner@mci4me.at | Entwicklung |

---

## Dokumentation

| Dokument | Ablageort |
|----------|-----------|
| Lastenheft v1.1 | `/docs/lastenheft.pdf` |
| Pflichtenheft v1.0 | `/docs/pflichtenheft.pdf` |
| MoSCoW-Matrix | `/docs/moscow_matrix.xlsx` |
| OpenAPI-Spec | `/docs/api/openapi.yaml` |
| Abschlusspräsentation | `/docs/presentation/` |
| Projektbericht | `/docs/bericht.pdf` |

---

## Lizenz

Universitätsprojekt — MCI Innsbruck · Software Engineering II · 2026
