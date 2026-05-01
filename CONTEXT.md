# AI_CONTEXT.md — BugTracker · Vollständiger Projektkontext für KI-Assistenten

> Dieses Dokument ist die primäre Informationsquelle für KI-Assistenten (Claude, Copilot, Cursor, etc.).  
> Es beschreibt präzise, was gebaut wird, wie es gebaut wird und welche Entscheidungen bereits getroffen wurden.  
> **Vor jeder Code-Generierung oder Architekturentscheidung dieses Dokument lesen.**

---

## 1. Projektzusammenfassung

**Projektname:** BugTracker  
**Typ:** Webanwendung (SPA + REST-API)  
**Kontext:** Universitätsprojekt, Kurs Software Engineering II, MCI Innsbruck  
**Auftraggeber:** Prof. Andrea Corradini  
**Team:** Gruppe DE 3 (5 Personen)  
**Deadline:** 20.05.2026 (Implementierung) · 22.05.2026 (Präsentation + Bericht)  
**Repository:** https://github.com/Cmort-bot/bug-tracker (privat)  
**MCI Gitea:** git.mci4me.at/gm5410/bugtracker-se2-gr3 (privat)

**Zweck:** Schlanke Webanwendung zur strukturierten Erfassung, Verwaltung und Nachverfolgung von Software-Fehlern (Bugs) für kleine Teams (5–20 Personen).

---

## 2. Tech-Stack (verbindlich, keine Abweichungen)

### Backend
- **Sprache:** Java 21
- **Framework:** Spring Boot 3.x
- **Module:** Spring Web, Spring Security, Spring Data JDBC (kein JPA/Hibernate)
- **Datenbank-Zugriff:** JdbcTemplate (kein ORM)
- **Migrationen:** Flyway
- **Passwörter:** BCrypt, cost factor ≥ 10
- **Auth:** Session-basiert (kein JWT), HttpOnly Session-Cookies
- **API-Dokumentation:** SpringDoc OpenAPI (Swagger UI)
- **Validation:** Jakarta Validation (@NotBlank, @Size, @Email)
- **Tests:** JUnit 5, Mockito, MockMvc / TestRestTemplate
- **Coverage:** JaCoCo, Ziel ≥ 60% in Service-Schicht
- **Build:** Maven

### Frontend
- **Framework:** React 18+ mit TypeScript
- **Build-Tool:** Vite
- **Styling:** Tailwind CSS
- **Routing:** react-router-dom v6+
- **Formulare:** react-hook-form + zod
- **HTTP:** fetch API mit `credentials: 'include'` (Cookie-Sessions)
- **Tests:** Vitest + Testing Library
- **Notifications:** react-hot-toast oder sonner

### Datenbank
- **Entwicklung:** PostgreSQL (via Docker Compose)
- **Produktion:** PostgreSQL

### CI/CD
- **Plattform:** GitHub Actions
- **Backend-Pipeline:** mvn test auf jeden PR
- **Frontend-Pipeline:** ESLint + TypeScript-Check + Vite-Build auf jeden PR
- **Deployment:** Auto-Deploy auf main-Branch

---

## 3. Architektur

```
┌──────────────────────────────────────────────────────────────┐
│                        React SPA (Vite)                       │
│  TypeScript · Tailwind · react-router · react-hook-form       │
│  Läuft auf: localhost:5173 (Dev) / statische Dateien (Prod)  │
└─────────────────────┬────────────────────────────────────────┘
                      │ fetch() mit credentials:'include'
                      │ JSON over HTTP
                      ▼
┌──────────────────────────────────────────────────────────────┐
│                   Spring Boot REST-API                        │
│  Java 21 · Spring Web · Spring Security · Spring Data JDBC   │
│  Läuft auf: localhost:8080                                    │
│                                                               │
│  controller/ ──► service/ ──► dao/ ──► JdbcTemplate ──► DB  │
│  config/ (Security, CORS)                                     │
│  auth/ (Login, Register, Session)                             │
└─────────────────────┬────────────────────────────────────────┘
                      │ JDBC / SQL
                      ▼
┌──────────────────────────────────────────────────────────────┐
│                      PostgreSQL                               │
│  Flyway Migrationen (V1, V2, V3, V4)                         │
│  Tabellen: users, bugs, activities, comments, tags           │
└──────────────────────────────────────────────────────────────┘
```

### Wichtige Architektur-Entscheidungen

- **Kein JPA/Hibernate** — ausschließlich Spring Data JDBC mit JdbcTemplate und SQL-Migrationen
- **Kein JWT** — Session-basierte Auth mit Spring Security Sessions
- **CORS** — Backend erlaubt `localhost:5173` und Production-URL mit `credentials: true`
- **Soft-Delete** — Bugs werden nie physisch gelöscht, nur `archived = true`
- **State Machine** — Statusübergänge werden serverseitig validiert (kein freies Setzen)

---

## 4. Datenmodell

### Tabelle: `users`
```sql
id            BIGSERIAL PRIMARY KEY
username      VARCHAR(50) UNIQUE NOT NULL
email         VARCHAR(255) UNIQUE NOT NULL
password_hash VARCHAR(255) NOT NULL          -- BCrypt
role          VARCHAR(20) NOT NULL           -- TESTER | DEVELOPER | ADMIN
created_at    TIMESTAMP NOT NULL DEFAULT NOW()
```

### Tabelle: `tags`
```sql
id         BIGSERIAL PRIMARY KEY
name       VARCHAR(50) UNIQUE NOT NULL
color      VARCHAR(7)                        -- Hex-Farbe z.B. #FF5733
created_at TIMESTAMP NOT NULL DEFAULT NOW()
```

### Tabelle: `bugs`
```sql
id          BIGSERIAL PRIMARY KEY
title       VARCHAR(255) NOT NULL
description TEXT NOT NULL
status      VARCHAR(20) NOT NULL             -- Enum (siehe unten)
priority    VARCHAR(20) NOT NULL DEFAULT 'MITTEL'
reporter_id BIGINT NOT NULL REFERENCES users(id)
assignee_id BIGINT REFERENCES users(id)     -- nullable
tag_id      BIGINT REFERENCES tags(id)      -- nullable
archived    BOOLEAN NOT NULL DEFAULT FALSE
created_at  TIMESTAMP NOT NULL DEFAULT NOW()
updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
```

### Tabelle: `activities`
```sql
id         BIGSERIAL PRIMARY KEY
bug_id     BIGINT NOT NULL REFERENCES bugs(id)
user_id    BIGINT NOT NULL REFERENCES users(id)
action     VARCHAR(50) NOT NULL             -- z.B. STATUS_CHANGED
field      VARCHAR(50)                      -- z.B. status
old_value  TEXT
new_value  TEXT
created_at TIMESTAMP NOT NULL DEFAULT NOW()
```

### Tabelle: `comments` (KANN-Feature)
```sql
id         BIGSERIAL PRIMARY KEY
bug_id     BIGINT NOT NULL REFERENCES bugs(id)
user_id    BIGINT NOT NULL REFERENCES users(id)
content    TEXT NOT NULL
created_at TIMESTAMP NOT NULL DEFAULT NOW()
```

---

## 5. Status-State-Machine

Erlaubte Übergänge (serverseitig erzwungen):

```
NEU ──────────────────────► IN_BEARBEITUNG
IN_BEARBEITUNG ───────────► IM_REVIEW
IM_REVIEW ────────────────► ERLEDIGT
IM_REVIEW ────────────────► IN_BEARBEITUNG   (Review fehlgeschlagen)
ERLEDIGT ─────────────────► (kein weiterer Übergang außer ARCHIVIERT)
NEU / IN_BEARBEITUNG / IM_REVIEW / ERLEDIGT ──► ABGELEHNT
ALLE STATUS ──────────────► ARCHIVIERT       (via Soft-Delete Endpoint)
```

Verbotene Übergänge werden mit HTTP 400 abgelehnt. Jeder erlaubte Übergang erzeugt automatisch einen Activity-Eintrag.

---

## 6. API-Endpunkte (vollständige Übersicht)

### Auth
| Method | Endpoint | Beschreibung | Auth erforderlich |
|--------|----------|--------------|-------------------|
| POST | `/api/auth/login` | Login, setzt Session-Cookie | Nein |
| POST | `/api/auth/logout` | Logout, invalidiert Session | Ja |
| POST | `/api/auth/register` | Registrierung (Default-Rolle: TESTER) | Nein |
| GET | `/api/auth/me` | Eingeloggten User abfragen | Ja |

### Bugs
| Method | Endpoint | Beschreibung | Auth erforderlich |
|--------|----------|--------------|-------------------|
| GET | `/api/bugs` | Bugliste (Filter: status, priority, assignee_id, search, page) | Ja |
| POST | `/api/bugs` | Neuen Bug anlegen | Ja |
| GET | `/api/bugs/{id}` | Bug-Details | Ja |
| PUT | `/api/bugs/{id}` | Bug bearbeiten (title, description, priority) | Ja |
| DELETE | `/api/bugs/{id}` | Bug archivieren (Soft-Delete, setzt archived=true) | Ja |
| PATCH | `/api/bugs/{id}/restore` | Bug reaktivieren (setzt archived=false) | Ja |
| PATCH | `/api/bugs/{id}/status` | Status ändern (State-Machine-Validierung) | Ja |
| PATCH | `/api/bugs/{id}/assignee` | Bearbeiter zuweisen oder entfernen | Ja |

### Activities (Bug-Historie)
| Method | Endpoint | Beschreibung | Auth erforderlich |
|--------|----------|--------------|-------------------|
| GET | `/api/bugs/{id}/activities` | Änderungshistorie eines Bugs | Ja |

### Comments (KANN)
| Method | Endpoint | Beschreibung | Auth erforderlich |
|--------|----------|--------------|-------------------|
| GET | `/api/bugs/{id}/comments` | Kommentare eines Bugs | Ja |
| POST | `/api/bugs/{id}/comments` | Kommentar hinzufügen | Ja |

### Tags
| Method | Endpoint | Beschreibung | Rolle erforderlich |
|--------|----------|--------------|-------------------|
| GET | `/api/tags` | Alle Tags auflisten | Ja (alle) |
| POST | `/api/tags` | Tag anlegen | ADMIN |
| PUT | `/api/tags/{id}` | Tag bearbeiten | ADMIN |
| DELETE | `/api/tags/{id}` | Tag löschen | ADMIN |

### Users (Admin)
| Method | Endpoint | Beschreibung | Rolle erforderlich |
|--------|----------|--------------|-------------------|
| GET | `/api/users` | Alle User auflisten (für Dropdowns + Admin) | Ja |
| PATCH | `/api/users/{id}/role` | Rolle eines Users ändern | ADMIN |

### Health
| Method | Endpoint | Beschreibung |
|--------|----------|--------------|
| GET | `/actuator/health` | Health-Check für Hosting-Provider |

---

## 7. Rollen & Berechtigungen

| Aktion | TESTER | DEVELOPER | ADMIN |
|--------|--------|-----------|-------|
| Bug anlegen | ✅ | ✅ | ✅ |
| Bug bearbeiten | ✅ | ✅ | ✅ |
| Bug archivieren | ✅ | ✅ | ✅ |
| Status ändern | ✅ | ✅ | ✅ |
| Bearbeiter zuweisen | ✅ | ✅ | ✅ |
| Tags anzeigen | ✅ | ✅ | ✅ |
| Tags verwalten (CRUD) | ❌ | ❌ | ✅ |
| User-Rollen verwalten | ❌ | ❌ | ✅ |
| User-Liste sehen | ✅ | ✅ | ✅ |

---

## 8. Flyway-Migrationen (Reihenfolge)

```
V1__init.sql          → users-Tabelle, sessions-Tabelle
V2__bugs.sql          → tags-Tabelle, bugs-Tabelle
V3__activities.sql    → activities-Tabelle
V4__comments.sql      → comments-Tabelle (KANN, nur wenn Feature implementiert)
```

---

## 9. Sicherheitsanforderungen (verbindlich)

- Passwörter: **BCrypt**, cost factor ≥ 10, niemals Klartext speichern
- Session-Cookies: **HttpOnly** und **Secure** Flag gesetzt
- SQL: ausschließlich **PreparedStatements** (via JdbcTemplate), kein String-Concatenation in SQL
- Auth-Fehler: **kein Hinweis** ob Username existiert (immer: „Login fehlgeschlagen")
- Stack-Traces: **nie** im HTTP-Response-Body sichtbar
- Secrets: **niemals** im Git-Repository (Environment-Variablen)

---

## 10. Package-Struktur Backend

```
at.mci.bugtracker/
├── BugTrackerApplication.java
├── controller/
│   ├── AuthController.java
│   ├── BugController.java
│   ├── ActivityController.java
│   ├── CommentController.java      (KANN)
│   ├── TagController.java
│   └── UserController.java
├── service/
│   ├── AuthService.java
│   ├── BugService.java
│   ├── ActivityService.java
│   ├── CommentService.java         (KANN)
│   ├── TagService.java
│   └── UserService.java
├── dao/
│   ├── UserDao.java
│   ├── BugDao.java
│   ├── ActivityDao.java
│   ├── CommentDao.java             (KANN)
│   └── TagDao.java
├── model/
│   ├── User.java                   (Java Record)
│   ├── Bug.java                    (Java Record)
│   ├── Activity.java               (Java Record)
│   ├── Comment.java                (Java Record)
│   ├── Tag.java                    (Java Record)
│   ├── BugStatus.java              (Enum)
│   ├── BugPriority.java            (Enum)
│   └── UserRole.java               (Enum)
├── config/
│   ├── SecurityConfig.java         (Spring Security)
│   └── CorsConfig.java
└── auth/
    ├── PasswordHasher.java
    └── GlobalExceptionHandler.java
```

---

## 11. Frontend-Struktur

```
src/
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── BugListPage.tsx
│   ├── BugDetailPage.tsx
│   ├── BugCreatePage.tsx
│   ├── AdminUsersPage.tsx
│   └── AdminTagsPage.tsx
├── components/
│   ├── layout/
│   │   ├── MainLayout.tsx
│   │   └── Navigation.tsx
│   ├── bugs/
│   │   ├── BugTable.tsx
│   │   ├── BugForm.tsx
│   │   ├── StatusBadge.tsx
│   │   ├── PriorityBadge.tsx
│   │   ├── StatusDropdown.tsx
│   │   ├── AssigneeDropdown.tsx
│   │   └── ActivityTimeline.tsx
│   └── ui/
│       ├── ConfirmDialog.tsx
│       └── LoadingSkeleton.tsx
├── hooks/
│   ├── useAuth.tsx
│   ├── useBugs.ts
│   ├── useBug.ts
│   └── useUsers.ts
├── lib/
│   ├── api.ts                      (fetch-Wrapper mit credentials:'include')
│   └── routes.ts                   (Route-Konstanten)
└── main.tsx
```

---

## 12. Personas (für Demo-Daten und Tests)

| Persona | Rolle | Username | Beschreibung |
|---------|-------|----------|--------------|
| Tom | TESTER | tom | 24 J., Werkstudent QA, meldet täglich Bugs |
| Marie | DEVELOPER | marie | 28 J., Backend-Entwicklerin, bearbeitet Bugs |
| Sandra | ADMIN | sandra | 35 J., Tech Lead, verwaltet Zuweisung + Überblick |

---

## 13. Demo-Daten (Seed)

Mindestens 5 Beispiel-Bugs mit verschiedenen Stati und Tags, um alle Features in der Live-Demo zeigen zu können:

| # | Titel | Status | Priorität | Bearbeiter | Tag |
|---|-------|--------|-----------|------------|-----|
| 1 | Login-Button reagiert nicht | IN_BEARBEITUNG | Hoch | Marie | Frontend |
| 2 | Passwort-Reset schickt keine E-Mail | NEU | Kritisch | — | Backend |
| 3 | Bugliste lädt zu langsam | IM_REVIEW | Mittel | Tom | Performance |
| 4 | Falscher Status-Code bei 404 | ERLEDIGT | Niedrig | Marie | API |
| 5 | Registrierung akzeptiert leere E-Mail | NEU | Hoch | — | Backend |
| 6 | Dashboard zeigt falsche Zahlen | ABGELEHNT | Mittel | — | Frontend |

---

## 14. Nicht-funktionale Anforderungen

| ID | Kategorie | Anforderung | Nachweis |
|----|-----------|-------------|----------|
| NFA-01 | Benutzbarkeit | Bug anlegen in < 3 Min ohne Hilfe | Usability-Test mit 3 Probanden |
| NFA-02 | Technologie | Java 21 + Spring Boot 3.x + React + TypeScript | Tech-Stack-Entscheidung |
| NFA-03 | Plattform | Läuft im Webbrowser (Chrome, Firefox, Edge) | Manuelle Tests |
| NFA-04 | Antwortzeit | Einfache Aktionen < 2 Sek bei ≤ 100 Bugs | JMeter lokal |
| NFA-05 | Wartbarkeit | Coverage ≥ 60% Service-Schicht, keine SonarLint Critical-Findings | JaCoCo-Report |
| NFA-06 | Zuverlässigkeit | Kein unkontrollierter Absturz, kein Datenverlust | Integrationstests |
| NFA-07 | Sicherheit | BCrypt, HttpOnly Cookies, PreparedStatements | Code-Review |
| NFA-08 | Architektur | Getrenntes Backend/Frontend, JSON-REST | Architektur-Diagramm |

---

## 15. Was bewusst NICHT gebaut wird

- Integration mit Jira, GitHub Issues oder E-Mail-Servern
- Mobile App (iOS/Android)
- Komplexes Rechtemanagement (nur 3 Rollen)
- Kanban/Sprint-Board mit Drag-and-Drop
- Zeiterfassung pro Bug
- Multi-Tenancy
- Externe REST-API für Drittsysteme
- Mehrsprachigkeit (nur Deutsch)
- Versionierung der Bug-Beschreibung (nur Status/Feld-Historie)

---

## 16. Konventionen

### Git Commits (Conventional Commits)
```
feat: implement bug create endpoint
fix: resolve CORS issue with credentials
test: add unit tests for BugService
docs: update README setup instructions
chore: configure JaCoCo coverage report
```

### Branch-Strategie
```
main         → nur Releases, geschützt
develop      → laufende Integration, geschützt
feature/xxx  → Feature-Branches, per PR mit mind. 1 Review
```

### Code-Stil
- Java: Standard Java Naming Conventions, JavaDoc auf public Methoden in Service + Controller
- TypeScript: strict mode, keine `any`-Types
- SQL: UPPER_CASE Keywords, snake_case Tabellen/Spalten

---

*Letzte Aktualisierung: Mai 2026 · Gruppe DE 3 · MCI Innsbruck*
