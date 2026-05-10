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
git clone https://git.mci4me.at/gm5410/bugtracker-se2-gr3.git
cd bugtracker-se2-gr3
```

### 2. Backend + Datenbank starten (Docker)

```bash
docker compose up -d
```

Docker Compose startet PostgreSQL und das Backend.  
PostgreSQL läuft danach auf `localhost:5432` (User/Passwort/DB jeweils `bugtracker`).  
Das Backend ist unter `http://localhost:8080` erreichbar. Flyway-Migrationen werden beim Backend-Start automatisch ausgeführt.

**Verifikation:**
```bash
docker compose ps           # postgres muss "healthy" zeigen, backend muss laufen
```

### 3. Backend lokal mit Maven starten

Wenn das Backend lokal ohne Docker laufen soll, nur PostgreSQL starten:

```bash
docker compose up -d postgres
```

Danach das Backend lokal starten:

```bash
cd backend
mvn spring-boot:run
```

API erreichbar unter: `http://localhost:8080`  
Health-Check: `http://localhost:8080/actuator/health` → muss `{"status":"UP"}` zurückgeben.

### 4. Frontend starten *(geplant — wird in E5 angelegt, siehe TASKS.md T039)*

```bash
cd frontend
npm install
npm run dev
```

App erreichbar unter: `http://localhost:5173`

### 5. Demo-Daten laden *(geplant — siehe TASKS.md T080)*

Wird über ein Seed-Skript bereitgestellt, sobald die Bug-Verwaltung (E4) implementiert ist.

---

## Tests ausführen

### Backend (JUnit + Mockito)

```bash
cd backend
mvn test
```

Coverage-Report (JaCoCo) *(geplant — wird in T069 konfiguriert)*:

```bash
mvn verify
# Report: backend/target/site/jacoco/index.html
```

### Frontend (Vitest + Testing Library) *(geplant — siehe T068)*

```bash
cd frontend
npm run test
```

---

## Troubleshooting

| Problem | Ursache | Lösung |
|---------|---------|--------|
| `Port 5432 already in use` | lokales PostgreSQL läuft bereits | `lsof -i :5432` (macOS/Linux) → Prozess stoppen oder `docker-compose.yml` Port auf z.B. `5433:5432` ändern |
| `Port 8080 already in use` | anderer Spring-Boot/Tomcat läuft | Prozess stoppen oder Backend mit `mvn spring-boot:run -Dserver.port=8081` starten |
| Backend startet nicht, Flyway-Fehler | DB-Schema in Datenbank kollidiert | `docker compose down -v` (löscht das Volume!) und neu hochfahren |
| `mvn spring-boot:run` → DB-Connection-Error | DB-Container noch nicht gesund | warten bis `docker compose ps` `healthy` zeigt, dann erneut versuchen |
| Java-Version-Fehler | falsche JDK aktiv | `java -version` prüfen — muss `21` sein (z.B. via SDKMAN: `sdk use java 21-tem`) |

---

## Branching & Pull Requests

- `main` → nur Releases (geschützt, kein direkter Push)
- `develop` → laufende Integration (geschützt, nur via PR)
- Feature-Branches: `feature/T0XX-kurzbeschreibung`

PRs werden gegen `develop` geöffnet, brauchen mind. 1 Approval und einen grünen CI-Lauf zum Mergen.  
Detaillierte Richtlinien folgen in `CONTRIBUTING.md` *(siehe TASKS.md T017)*.

---

## Projektstruktur

```
bugtracker-se2-gr3/
├── backend/                     # Spring Boot Anwendung (Java 21, Maven)
│   ├── src/
│   │   ├── main/java/at/mci/bugtracker/
│   │   │   ├── controller/      # REST-Controller            (geplant E3+)
│   │   │   ├── service/         # Business-Logik             (geplant E3+)
│   │   │   ├── dao/             # Datenbankzugriff (JDBC)    (geplant E3+)
│   │   │   ├── model/           # Java Records / DTOs        (geplant E3+)
│   │   │   ├── config/          # Spring Security, CORS      (geplant E3+)
│   │   │   └── auth/            # Auth-Logik                 (geplant E3+)
│   │   └── resources/
│   │       ├── db/migration/    # Flyway SQL-Migrationen     (geplant E3+)
│   │       └── application.yml
│   └── pom.xml
├── frontend/                    # React-SPA                  (geplant E5)
│   ├── src/
│   │   ├── pages/               # Seiten (Login, Bugs, Detail, …)
│   │   ├── components/          # Wiederverwendbare Komponenten
│   │   ├── hooks/               # Custom React Hooks
│   │   └── lib/                 # API-Client, Utilities
│   ├── package.json
│   └── vite.config.ts
├── docs/                        # Dokumentation (Lastenheft, MoSCoW, …)
├── docker-compose.yml           # PostgreSQL 16 für Dev
├── .github/workflows/
│   ├── ci-backend.yml           # CI Backend Pipeline (T013)
│   └── ci-frontend.yml          # geplant (T014)
├── TASKS.md                     # vollständige Aufgabenliste (92 Tasks)
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

| Dokument | Ablageort | Status |
|----------|-----------|--------|
| Lastenheft v1.1 | `/docs/Lastenheft_BugTracker-2.pdf` | vorhanden |
| MoSCoW-Matrix | `/docs/MoSCoW_Matrix_BugTracker_gr_3.pdf` | vorhanden |
| Aufgabenliste | `/TASKS.md` | vorhanden |
| Pflichtenheft v1.1 | `/docs/pflichtenheft.pdf` | geplant (T002) |
| OpenAPI-Spec | `/docs/api/openapi.yaml` | geplant (T004) |
| Abschlusspräsentation | `/docs/presentation/` | geplant (T079) |
| Projektbericht | `/docs/bericht.pdf` | geplant (T082) |

---

## Lizenz

Universitätsprojekt — MCI Innsbruck · Software Engineering II · 2026
