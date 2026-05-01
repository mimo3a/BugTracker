# TASKS.md — BugTracker · Vollständige Aufgabenliste

> Alle Tasks sind nach Priorität und Abhängigkeiten geordnet.  
> **Reihenfolge einhalten** — spätere Epics setzen frühere voraus.  
> Status: ☐ offen · ✅ erledigt · 🔄 in Bearbeitung

---

## Übersicht

| Epic | Bereich | Tasks | Voraussetzung |
|------|---------|-------|---------------|
| [E1 Spezifikation & Setup](#e1-spezifikation--setup) | Docs + Infra | T001–T009 | — |
| [E2 DevOps & CI/CD](#e2-devops--cicd) | Infra | T010–T017 | E1 |
| [E3 Auth-System](#e3-auth-system) | Backend | T018–T028 | E2 |
| [E4 Bug-Verwaltung](#e4-bug-verwaltung) | Backend | T029–T038 | E3 |
| [E5 Frontend Setup & Pages](#e5-frontend-setup--pages) | Frontend | T039–T055 | E3 |
| [E6 Erweiterte Features](#e6-erweiterte-features) | Full-Stack | T056–T062 | E4 + E5 |
| [E7 Tests & Quality](#e7-tests--quality) | Tests | T063–T071 | E4 + E5 |
| [E8 Deployment](#e8-deployment) | DevOps | T072–T078 | E6 + E7 |
| [E9 Präsentation & Bericht](#e9-präsentation--bericht) | Docs | T079–T083 | E8 |

---

## E1: Spezifikation & Setup

> **Ziel:** Alle Dokumente und Werkzeuge fertig, bevor mit der Programmierung begonnen wird.  
> **Deadline:** 30.04.2026

---

### ✅ T001 · Lastenheft v1.0 finalisieren und abgeben

**Was:** Letzte Korrekturen in das Lastenheft einarbeiten, Team-Review, als PDF im Sakai abgeben.

**Schritte:**
1. Alle offenen Kommentare im Dokument klären
2. Änderungshistorie aktualisieren
3. Als PDF exportieren
4. Im Sakai hochladen
5. PDF-Version im Repo unter `/docs/` committen

**Definition of Done:**
- [ ] Lastenheft als PDF im Sakai abgegeben
- [ ] Im Repo unter `/docs/lastenheft.pdf` versioniert

---

### T002 · Pflichtenheft v1.0 schreiben

**Was:** Das Pflichtenheft beschreibt aus Sicht des Entwicklerteams, *wie* das Lastenheft umgesetzt wird. Es ist die größte Dokumentationsaufgabe in dieser Phase.

**Inhalt des Pflichtenhefts:**
- Systemarchitektur (Diagramm: Frontend → API → Backend → DB)
- Datenmodell (ER-Diagramm)
- API-Design (alle Endpoints mit Request/Response)
- Akzeptanzkriterien je Anforderung (Gegeben/Wenn/Dann)
- Technologieentscheidungen mit Begründung
- Deployment-Konzept

**Schritte:**
1. Gliederung erstellen (orientiert an Vorlage)
2. Architektur-Kapitel schreiben
3. Datenmodell-Kapitel schreiben (ER-Diagramm einbetten)
4. API-Kapitel schreiben (alle Endpoints)
5. Akzeptanzkriterien je FA eintragen
6. Review im Team
7. Als PDF exportieren und abgeben

**Definition of Done:**
- [ ] Pflichtenheft als PDF im Sakai abgegeben
- [ ] Alle Kapitel vollständig ausgefüllt

---

### T003 · ER-Datenmodell zeichnen

**Was:** Entity-Relationship-Diagramm für alle Kern-Entitäten.

**Entitäten:** `users`, `bugs`, `activities`, `comments`, `tags`

**Beziehungen:**
- User 1:N Bug (als Reporter)
- User 1:N Bug (als Assignee, optional)
- Bug 1:N Activity
- Bug 1:N Comment (KANN)
- Tag 1:N Bug

**Tools:** mermaid.js (im Markdown einbettbar) oder dbdiagram.io

**Definition of Done:**
- [ ] Diagramm als PNG/SVG im Repo unter `/docs/er-diagram.png`
- [ ] Im Pflichtenheft eingebettet

---

### T004 · OpenAPI-Spec für REST-API entwerfen

**Was:** `openapi.yaml` mit allen Backend-Endpoints. Dient als Single Source of Truth zwischen Backend und Frontend — beide Seiten implementieren gegen diese Spec.

**Inhalt:**
- Alle Endpoints (Auth, Bugs, Activities, Tags, Users, Comments)
- Request-Bodies mit Typen und Validierungsregeln
- Response-Schemas (Success + Error)
- Sicherheitsschema (Session-Cookie)

**Tool:** Swagger Editor (https://editor.swagger.io) zum Validieren

**Definition of Done:**
- [ ] `openapi.yaml` validiert ohne Fehler
- [ ] Im Repo unter `/docs/api/openapi.yaml`

---

### T005 · Architektur-Diagramm erstellen

**Was:** Visualisierung der Systemarchitektur für Pflichtenheft und Präsentation.

**Inhalt:** Frontend → REST-API → Backend (Schichten) → DB + Auth-Flow

**Definition of Done:**
- [ ] Diagramm als PNG/SVG im Repo
- [ ] Im Pflichtenheft eingebettet

---

### ✅ T006 · MoSCoW-Matrix in Excel finalisieren

**Was:** `MoSCoWMatrixtemplate.xlsx` ausfüllen. Alle FA-IDs (FA-01 bis FA-16) in die Kategorien Muss / Soll / Kann / Won't einsortieren.

**Definition of Done:**
- [ ] Alle 16 Anforderungen kategorisiert
- [ ] Excel-Datei im Repo unter `/docs/moscow_matrix.xlsx`

---

### ✅ T007 · Neues GitHub-Repo aufsetzen (sauberer Stand)

**Was:** Frisches Repository anlegen. Keinen Code aus dem alten KI-generierten Stand übernehmen.

**Schritte:**
1. Neues Repo auf GitHub anlegen: `bug-tracker`
2. Alle 5 Teammitglieder als Collaborator hinzufügen
3. Branch-Schutzregeln für `main` und `develop` aktivieren (siehe T009)
4. Grundstruktur anlegen: `/backend`, `/frontend`, `/docs`
5. `.gitignore` und `.gitattributes` anlegen (siehe T016)
6. Ersten Commit pushen

**Definition of Done:**
- [ ] Repo auf GitHub erstellt
- [ ] Alle Teammitglieder als Collaborator
- [ ] Branch-Schutz für `main` + `develop` aktiviert

---

### ✅ T008 · Jira-Board einrichten + Backlog importieren

**Was:** Jira-Projekt anlegen, CSV-Import durchführen, Epic-Zuordnungen prüfen.

**Schritte:**
1. Neues Jira-Projekt erstellen (Typ: Software / Scrum)
2. CSV-Datei `jira_backlog_bugtracker_v8.csv` importieren
3. Feld-Zuordnungen prüfen: Issue Type, Summary, Description, Priority, Labels, Parent
4. Epic-Hierarchie prüfen (Tasks sollen unter Epics erscheinen)
5. Alle Teammitglieder einladen

**Definition of Done:**
- [ ] Alle 83 Tasks importiert
- [ ] Epic-Zuordnungen korrekt
- [ ] Alle Teammitglieder im Board

---

### ✅ T009 · Branch-Protection-Rules definieren

**Was:** Schutzregeln für die Haupt-Branches konfigurieren.

**Regeln:**
- `main`: Nur Releases; direkter Push verboten; nur über PR mit mind. 1 approved Review
- `develop`: Laufende Integration; direkter Push verboten; nur über PR
- Feature-Branches: Format `feature/T0XX-kurzbeschreibung`
- CI-Pipeline muss grün sein bevor Merge möglich

**Definition of Done:**
- [ ] Branch-Schutz für `main` in GitHub aktiviert
- [ ] Branch-Schutz für `develop` in GitHub aktiviert
- [ ] PR-Reviewer-Anforderung: mind. 1 Person

---

## E2: DevOps & CI/CD

> **Ziel:** Build- und Test-Automatisierung — jeder Pull Request wird automatisch geprüft.  
> **Voraussetzung:** E1 abgeschlossen  
> **Deadline:** 06.05.2026

---

### T010 · Maven-Projekt-Setup mit Spring Boot 3.x

**Was:** `pom.xml` mit allen benötigten Dependencies erstellen. Grundstruktur des Backend-Projekts anlegen.

**Dependencies:**
```xml
spring-boot-starter-web
spring-boot-starter-security
spring-boot-starter-data-jdbc
spring-boot-starter-validation
flyway-core
flyway-database-postgresql
springdoc-openapi-starter-webmvc-ui
spring-boot-starter-actuator
bcrypt (spring-security-crypto ist bereits enthalten)
postgresql (JDBC-Treiber)
junit-jupiter
mockito-core
spring-boot-starter-test
```

**Definition of Done:**
- [ ] `mvn clean install` läuft fehlerfrei
- [ ] Alle Dependencies aufgelöst
- [ ] Spring Boot startet ohne Fehler

---

### T011 · Docker-Compose für PostgreSQL (Dev)

**Was:** `docker-compose.yml` im Repo-Root, der eine PostgreSQL-Instanz für die lokale Entwicklung startet.

```yaml
# Beispiel-Struktur
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: bugtracker
      POSTGRES_USER: bugtracker
      POSTGRES_PASSWORD: bugtracker
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
```

**Definition of Done:**
- [ ] `docker-compose up -d` startet PostgreSQL fehlerfrei
- [ ] Backend kann sich mit der DB verbinden

---

### T012 · Dockerfile für Backend

**Was:** Multi-Stage Dockerfile für das Backend.

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
# Stage 2: Runtime
FROM eclipse-temurin:21-jre
```

**Anforderungen:** Image-Größe < 300 MB

**Definition of Done:**
- [ ] `docker build` erfolgreich
- [ ] Image < 300 MB
- [ ] Container startet die Spring Boot App fehlerfrei

---

### T013 · GitHub Actions CI Pipeline (Backend)

**Was:** GitHub Actions Workflow, der auf jeden Pull Request automatisch die Backend-Tests ausführt.

**Datei:** `.github/workflows/ci-backend.yml`

**Pipeline-Schritte:**
1. Checkout
2. Java 21 Setup
3. PostgreSQL Service starten (GitHub Actions Service Container)
4. `mvn test`
5. Bei Fehler: PR kann nicht gemerged werden

**Definition of Done:**
- [ ] Pipeline wird auf jeden PR gegen `develop` und `main` ausgelöst
- [ ] Fehlerhafte Tests blockieren den Merge

---

### T014 · GitHub Actions CI Pipeline (Frontend)

**Was:** GitHub Actions Workflow für das Frontend.

**Datei:** `.github/workflows/ci-frontend.yml`

**Pipeline-Schritte:**
1. Checkout
2. Node.js 20 Setup
3. `npm install`
4. `npm run lint` (ESLint)
5. `npm run typecheck` (TypeScript-Compiler)
6. `npm run build` (Vite)

**Definition of Done:**
- [ ] Pipeline wird auf jeden PR ausgelöst
- [ ] ESLint-Fehler und TypeScript-Fehler blockieren den Merge

---

### T015 · README.md mit Setup-Anleitung

**Was:** Schritt-für-Schritt-Anleitung für neue Entwickler. Ziel: Ein neues Teammitglied kann in weniger als 15 Minuten lokal starten.

**Inhalt:** Voraussetzungen, Klonen, DB starten, Backend starten, Frontend starten, Tests ausführen.

**Definition of Done:**
- [ ] README im Repo-Root vorhanden
- [ ] Setup funktioniert nach Anleitung von Grund auf

---

### T016 · .gitignore + .gitattributes konfigurieren

**Was:** Verhindert, dass Build-Artefakte und IDE-Dateien ins Repo kommen.

**`.gitignore` enthält mindestens:**
```
# Backend
target/
*.class
*.jar

# Frontend
node_modules/
dist/

# IDE
.idea/
.vscode/
*.iml

# Environment
.env
.env.local
application-prod.yml
```

**`.gitattributes`:** Sorgt für konsistente LF Line-Endings über alle Betriebssysteme.

**Definition of Done:**
- [ ] `target/` und `node_modules/` sind nicht im Repo
- [ ] `.gitattributes` mit LF-Konfiguration vorhanden

---

### T017 · CONTRIBUTING.md schreiben

**Was:** Dokumentiert die Entwicklungskonventionen für alle Teammitglieder.

**Inhalt:**
- Branching-Strategie (main / develop / feature/xxx)
- Conventional Commits Format mit Beispielen
- PR-Template (Was wurde geändert? Welche Tests wurden hinzugefügt?)
- Code-Review-Erwartungen (mind. 1 Reviewer, kein Merge ohne grüne CI)

**Definition of Done:**
- [ ] `CONTRIBUTING.md` im Repo-Root
- [ ] PR-Template als `.github/pull_request_template.md`

---

## E3: Auth-System

> **Ziel:** User können sich registrieren, einloggen und ausloggen. Sessions werden per Cookie verwaltet.  
> **Voraussetzung:** E2 abgeschlossen  
> **Deadline:** 06.05.2026

---

### T018 · Spring Boot Projekt initialisieren

**Was:** `Application.java` anlegen und die Package-Struktur aufbauen.

**Package-Struktur:**
```
at.mci.bugtracker/
├── BugTrackerApplication.java
├── controller/
├── service/
├── dao/
├── model/
├── config/
└── auth/
```

**Definition of Done:**
- [ ] Anwendung startet fehlerfrei mit `mvn spring-boot:run`
- [ ] Alle Packages angelegt und committed

---

### T019 · Flyway Setup + V1-Migration (User + Sessions)

**Was:** Flyway konfigurieren und die erste SQL-Migration schreiben.

**Datei:** `src/main/resources/db/migration/V1__init.sql`

```sql
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50) UNIQUE NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20) NOT NULL DEFAULT 'TESTER',
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE spring_session (
    -- Spring Security Session-Tabelle (wird automatisch verwaltet)
);
```

**Definition of Done:**
- [ ] Flyway-Migration läuft fehlerfrei beim Backend-Start
- [ ] Tabellen in der DB vorhanden (prüfbar via psql oder DBeaver)

---

### T020 · User-Model + UserDao

**Was:** Java Record für den User und ein DAO für Datenbankzugriffe.

```java
// Beispiel
public record User(Long id, String username, String email,
                   String passwordHash, UserRole role, LocalDateTime createdAt) {}

public class UserDao {
    // findById, findByUsername, findByEmail, save, updateRole, findAll
}
```

**Definition of Done:**
- [ ] Unit-Tests für alle UserDao-Methoden grün
- [ ] Kein Klartext-Passwort wird jemals gespeichert

---

### T021 · Spring Security Konfiguration

**Was:** `SecurityConfig.java` — Session-basierte Auth konfigurieren.

**Anforderungen:**
- Public (kein Login nötig): `POST /api/auth/login`, `POST /api/auth/register`
- Geschützt (Login erforderlich): alle anderen `/api/**` Endpoints
- Session-Management: `SessionCreationPolicy.IF_REQUIRED`
- CSRF: für REST-API deaktivieren (wir verwenden SameSite Cookies)

**Definition of Done:**
- [ ] `GET /api/bugs` ohne Login → HTTP 401
- [ ] `POST /api/auth/login` ohne Auth erreichbar

---

### T022 · CORS-Konfiguration für React-Frontend

**Was:** `CorsConfig.java` — Backend erlaubt Cross-Origin-Requests vom Frontend.

**Konfiguration:**
```java
allowedOrigins: ["http://localhost:5173", "${app.cors.allowed-origin}"]
allowedMethods: ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"]
allowCredentials: true   // wichtig für Session-Cookies
allowedHeaders: ["*"]
```

**Definition of Done:**
- [ ] Frontend-Requests von `localhost:5173` funktionieren
- [ ] Session-Cookie wird korrekt gesetzt und bei Folgeanfragen mitgesendet

---

### T023 · POST /api/auth/login Endpoint

**Was:** Login-Endpoint.

**Request:**
```json
{ "username": "marie", "password": "secret123" }
```

**Response (Erfolg):** HTTP 200 + User-Daten + Session-Cookie gesetzt  
**Response (Fehler):** HTTP 401 + `{ "error": "Login fehlgeschlagen" }` — KEIN Hinweis ob Username existiert

**Implementiert:** US-12 AC1 (erfolgreicher Login) + AC2 (Fehlermeldung)

**Definition of Done:**
- [ ] Korrektes Login → HTTP 200 + Cookie
- [ ] Falsches Login → HTTP 401, kein Stack-Trace, kein Hinweis auf Username

---

### T024 · POST /api/auth/logout Endpoint

**Was:** Logout-Endpoint — invalidiert die Session und löscht das Cookie.

**Response:** HTTP 200, Session-Cookie gelöscht

**Implementiert:** US-12 AC3

**Definition of Done:**
- [ ] Nach Logout: Session ungültig, geschützte Endpoints → HTTP 401

---

### T025 · POST /api/auth/register Endpoint

**Was:** Selbst-Registrierung.

**Request:**
```json
{
  "username": "tom",
  "email": "tom@example.com",
  "password": "secret123",
  "passwordConfirm": "secret123"
}
```

**Validierungen:**
- Username: eindeutig, 3–50 Zeichen
- Email: valides Format, eindeutig
- Passwort: mind. 8 Zeichen
- passwordConfirm: muss mit password übereinstimmen

**Default-Rolle:** `TESTER`

**Implementiert:** US-13 (alle 5 AC)

**Definition of Done:**
- [ ] Erfolgreiche Registrierung → Auto-Login + HTTP 201
- [ ] Doppelter Username → HTTP 409
- [ ] Kurzes Passwort → HTTP 400

---

### T026 · GET /api/auth/me Endpoint

**Was:** Gibt dem Frontend Infos über den eingeloggten User zurück.

**Response (eingeloggt):**
```json
{ "id": 1, "username": "marie", "email": "marie@example.com", "role": "DEVELOPER" }
```
**Response (nicht eingeloggt):** HTTP 401

**Verwendung im Frontend:** Beim App-Start aufgerufen, um die Session zu prüfen (Session-Persistenz nach Page-Reload).

**Implementiert:** US-12 AC4 + AC5

**Definition of Done:**
- [ ] Eingeloggte Session → HTTP 200 mit User-Daten
- [ ] Keine Session → HTTP 401

---

### T027 · Password-Hashing mit BCrypt

**Was:** Helper-Klasse `PasswordHasher` für sicheres Passwort-Hashing.

```java
public class PasswordHasher {
    public String hash(String plaintext) { ... }        // BCrypt, cost 10
    public boolean verify(String plain, String hash) { ... }
}
```

**Anforderungen:**
- Cost factor: mind. 10
- Niemals Klartext-Passwörter speichern oder loggen

**Definition of Done:**
- [ ] Unit-Tests für hash() und verify() grün (mind. 3 Test-Cases)
- [ ] Coverage 100% für PasswordHasher-Klasse

---

### T028 · GlobalExceptionHandler

**Was:** `@ControllerAdvice` für einheitliche Fehlerantworten.

**Mappings:**
| Exception | HTTP-Status | Response |
|-----------|-------------|----------|
| `MethodArgumentNotValidException` | 400 | Feld-spezifische Fehler |
| `UsernameNotFoundException` | 401 | `{ "error": "Login fehlgeschlagen" }` |
| `InvalidStatusTransitionException` | 400 | `{ "error": "Ungültiger Statuswechsel" }` |
| `EntityNotFoundException` | 404 | `{ "error": "Nicht gefunden" }` |
| `Exception` (Fallback) | 500 | `{ "error": "Interner Fehler" }` — KEIN Stack-Trace |

**Definition of Done:**
- [ ] Alle definierten Fehlerfälle liefern korrekten HTTP-Status
- [ ] Kein Stack-Trace je im Response-Body sichtbar

---

## E4: Bug-Verwaltung

> **Ziel:** Backend-API für alle Bug-CRUD-Operationen. Das Herzstück der Anwendung.  
> **Voraussetzung:** E3 abgeschlossen  
> **Deadline:** 13.05.2026

---

### T029 · V2-Migration: Bug-Tabelle + Tags-Tabelle

**Was:** SQL-Migration für Bugs und Tags.

**Datei:** `V2__bugs.sql`

```sql
CREATE TABLE tags (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) UNIQUE NOT NULL,
    color      VARCHAR(7),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE bugs (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'NEU'
                CHECK (status IN ('NEU','IN_BEARBEITUNG','IM_REVIEW','ERLEDIGT','ABGELEHNT','ARCHIVIERT')),
    priority    VARCHAR(20) NOT NULL DEFAULT 'MITTEL'
                CHECK (priority IN ('NIEDRIG','MITTEL','HOCH','KRITISCH')),
    reporter_id BIGINT NOT NULL REFERENCES users(id),
    assignee_id BIGINT REFERENCES users(id),
    tag_id      BIGINT REFERENCES tags(id),
    archived    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
```

**Definition of Done:**
- [ ] Migration läuft fehlerfrei
- [ ] Enum-Constraints aktiv (falsche Werte werden abgelehnt)

---

### T030 · Bug-Model + BugDao

**Was:** Java Record für Bug + DAO für alle Datenbankoperationen.

**BugDao-Methoden:**
- `findAll(BugFilter filter, int page)` — mit Status/Priority/Assignee/Search-Filter + Pagination
- `findById(Long id)` — mit JOIN auf users (Reporter, Assignee) und tags
- `save(Bug bug)` — Insert
- `update(Bug bug)` — Update
- `archive(Long id)` — setzt archived=true
- `restore(Long id)` — setzt archived=false

**Definition of Done:**
- [ ] Unit-Tests für alle BugDao-Methoden grün
- [ ] Filter-Kombinationen getestet

---

### T031 · POST /api/bugs Endpoint

**Was:** Neuen Bug anlegen.

**Request:**
```json
{
  "title": "Login-Button reagiert nicht",
  "description": "Reproduktionsschritte: ...",
  "priority": "HOCH",
  "tagId": 1
}
```

**Verhalten:**
- Reporter = eingeloggter User (automatisch gesetzt)
- Default-Status: `NEU`
- Default-Priorität: `MITTEL` wenn nicht angegeben
- Default-Tag: falls tagId null, wird Standard-Tag gesetzt (falls vorhanden)

**Implementiert:** US-01 (alle 6 AC)

**Definition of Done:**
- [ ] Neuer Bug erscheint in der Liste
- [ ] Pflichtfeld-Validierung funktioniert
- [ ] Reporter wird automatisch gesetzt

---

### T032 · GET /api/bugs Endpoint

**Was:** Bug-Liste mit Filterung und Pagination.

**Query-Parameter:**
- `status` (multi): z.B. `?status=NEU&status=IN_BEARBEITUNG`
- `priority`: z.B. `?priority=HOCH`
- `assigneeId`: z.B. `?assigneeId=3`
- `search`: z.B. `?search=login` (case-insensitive Suche in title)
- `page`: z.B. `?page=0` (50 Bugs pro Seite)
- `archived`: `?archived=true` für Archiv-Ansicht (Default: false)

**Response:**
```json
{
  "bugs": [...],
  "total": 73,
  "page": 0,
  "pageSize": 50
}
```

**Implementiert:** US-02 + US-09

**Definition of Done:**
- [ ] Alle Filter-Parameter funktionieren
- [ ] Archivierte Bugs standardmäßig ausgeblendet
- [ ] Pagination korrekt

---

### T033 · GET /api/bugs/{id} Endpoint

**Was:** Einzelnen Bug mit allen Details abrufen.

**Response enthält:** Alle Bug-Felder + `reporterName` + `assigneeName` + `tagName`

**Fehlerfall:** HTTP 404 wenn Bug nicht existiert

**Implementiert:** US-03

**Definition of Done:**
- [ ] Alle Felder inkl. JOIN-Daten im Response
- [ ] HTTP 404 bei nicht existierendem Bug

---

### T034 · PUT /api/bugs/{id} Endpoint

**Was:** Bug bearbeiten (Titel, Beschreibung, Priorität).

**Request:**
```json
{ "title": "Neuer Titel", "description": "Neue Beschreibung", "priority": "KRITISCH" }
```

**Verhalten:** Erzeugt automatisch Activity-Einträge für jedes geänderte Feld.

**Implementiert:** US-04 (alle 5 AC)

**Definition of Done:**
- [ ] Geänderte Werte in DB persistiert
- [ ] Activity-Einträge werden erzeugt
- [ ] Leerer Titel → HTTP 400

---

### T035 · Soft-Delete + Reaktivierung

**Was:** Archivieren und Reaktivieren eines Bugs.

**DELETE `/api/bugs/{id}`:** Setzt `archived = true` — physisches Löschen ist nicht vorgesehen.  
**PATCH `/api/bugs/{id}/restore`:** Setzt `archived = false`.

**Implementiert:** US-05 (alle 5 AC)

**Definition of Done:**
- [ ] Archivierter Bug verschwindet aus Standardliste
- [ ] Reaktivierung funktioniert
- [ ] Bug existiert nach Archivierung weiterhin in der DB

---

### T036 · PATCH /api/bugs/{id}/status Endpoint

**Was:** Status-Wechsel mit State-Machine-Validierung.

**Request:** `{ "status": "IN_BEARBEITUNG" }`

**State-Machine (erlaubte Übergänge):**
```
NEU → IN_BEARBEITUNG
IN_BEARBEITUNG → IM_REVIEW
IM_REVIEW → ERLEDIGT
IM_REVIEW → IN_BEARBEITUNG
NEU/IN_BEARBEITUNG/IM_REVIEW/ERLEDIGT → ABGELEHNT
ALLE → ARCHIVIERT
```

**Verbotener Übergang:** HTTP 400 + `{ "error": "Ungültiger Statuswechsel von ERLEDIGT zu NEU" }`

**Implementiert:** US-06 (alle 4 AC)

**Definition of Done:**
- [ ] Alle erlaubten Übergänge funktionieren
- [ ] Verbotene Übergänge → HTTP 400
- [ ] Activity-Eintrag wird erzeugt

---

### T037 · PATCH /api/bugs/{id}/assignee Endpoint

**Was:** Bearbeiter zuweisen oder entfernen.

**Request:** `{ "assigneeId": 3 }` oder `{ "assigneeId": null }` für "Kein Bearbeiter"

**Validierung:** User muss existieren (HTTP 404 sonst)

**Implementiert:** US-08 (alle 4 AC)

**Definition of Done:**
- [ ] Zuweisung wird gespeichert
- [ ] `assigneeId: null` entfernt Bearbeiter
- [ ] Activity-Eintrag wird erzeugt

---

### T038 · Bean-Validation für alle Request-Bodies

**Was:** Jakarta Validation Annotations auf alle Request-DTOs.

**Beispiel:**
```java
public record CreateBugRequest(
    @NotBlank(message = "Titel ist erforderlich")
    @Size(max = 255)
    String title,

    @NotBlank(message = "Beschreibung ist erforderlich")
    String description,

    String priority
) {}
```

**Definition of Done:**
- [ ] Alle Pflichtfelder in allen Requests annotiert
- [ ] Validierungsfehler → HTTP 400 mit Feld-spezifischen Messages via GlobalExceptionHandler

---

## E5: Frontend Setup & Pages

> **Ziel:** React-SPA, die mit dem Backend kommuniziert. Alle UI-Seiten und Komponenten.  
> **Voraussetzung:** E3 abgeschlossen (Backend-Auth läuft)  
> **Deadline:** 13.05.2026

---

### T039 · React + Vite + TypeScript Projekt-Setup

**Was:** Frontend-Projekt initialisieren.

```bash
npm create vite@latest frontend -- --template react-ts
```

**Konfiguration:**
- `tsconfig.json`: strict mode aktiviert
- Verzeichnisstruktur: `src/pages`, `src/components`, `src/hooks`, `src/lib`

**Definition of Done:**
- [ ] `npm run dev` startet fehlerfrei auf Port 5173
- [ ] TypeScript strict mode aktiv

---

### T040 · Tailwind CSS Setup

```bash
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

**Definition of Done:**
- [ ] Tailwind-Klassen werden korrekt angewendet

---

### T041 · React Router Setup

**Was:** Routing für alle Seiten.

**Routes:**
```typescript
/           → Redirect zu /bugs (wenn eingeloggt) oder /login
/login      → LoginPage
/register   → RegisterPage
/bugs       → BugListPage
/bugs/new   → BugCreatePage
/bugs/:id   → BugDetailPage
/admin/users → AdminUsersPage  (nur ADMIN)
/admin/tags  → AdminTagsPage   (nur ADMIN)
```

**ProtectedRoute:** Leitet nicht eingeloggte User zu `/login` um.  
**AdminRoute:** Leitet User ohne ADMIN-Rolle zu `/bugs` um.

**Definition of Done:**
- [ ] Alle Routes navigierbar
- [ ] Nicht eingeloggte User werden zu `/login` umgeleitet

---

### T042 · API-Client mit fetch + Custom Hooks

**Was:** Zentraler HTTP-Client und Custom Hooks für alle API-Calls.

```typescript
// lib/api.ts
const api = {
  get: (url) => fetch(url, { credentials: 'include' }),
  post: (url, body) => fetch(url, { method: 'POST', credentials: 'include', body: JSON.stringify(body), headers: {'Content-Type': 'application/json'} }),
  // put, patch, delete analog
}

// Hooks: useBugs(), useBug(id), useUsers(), useTags()
// Jeder Hook liefert: { data, loading, error, refetch }
```

**Definition of Done:**
- [ ] `credentials: 'include'` auf allen Requests
- [ ] Loading- und Error-States vorhanden
- [ ] TypeScript-Typen für alle API-Responses

---

### T043 · Auth-Context + AuthProvider

**Was:** React Context für den eingeloggten User.

```typescript
interface AuthContext {
  user: User | null;
  loading: boolean;
  login: (username, password) => Promise<void>;
  logout: () => Promise<void>;
}
```

**Verhalten:** Beim App-Start wird `GET /api/auth/me` aufgerufen — so bleibt der User nach Page-Reload eingeloggt.

**Implementiert:** US-12 AC5 (Session-Persistenz)

**Definition of Done:**
- [ ] User bleibt nach Page-Reload eingeloggt
- [ ] Logout leert den Context und leitet zu `/login` um

---

### T044 · Login-Seite + Auth-Flow

**Was:** Login-Formular.

**Felder:** Username, Passwort  
**Bei Erfolg:** Redirect zu `/bugs`  
**Bei Fehler:** Fehlermeldung anzeigen, Passwort-Feld leeren

**Implementiert:** US-12 AC1 + AC2

---

### T045 · Register-Seite

**Was:** Registrierungsformular mit Client-seitiger Validierung.

**Felder:** Username, E-Mail, Passwort, Passwort bestätigen  
**Validierung:** react-hook-form + zod (gleiche Regeln wie Backend)

**Implementiert:** US-13 (alle 5 AC)

---

### T046 · Bug-Liste-Seite (/bugs)

**Was:** Hauptseite mit Tabelle aller Bugs.

**Spalten:** ID, Titel, Status (Badge), Priorität, Tag, Bearbeiter, Erstelldatum  
**Interaktion:** Klick auf Zeile → Detail-Seite  
**Leerer Zustand:** Hinweis „Noch keine Bugs erfasst" + Button „Neuer Bug"

**Implementiert:** US-02 (alle 5 AC)

---

### T047 · Bug-Detail-Seite (/bugs/:id)

**Was:** Vollständige Anzeige aller Bug-Felder + Aktionen.

**Enthält:**
- Alle Bug-Felder angezeigt
- Edit-Button → öffnet Bearbeiten-Formular
- Status-Dropdown (direkt änderbar)
- Bearbeiter-Dropdown (direkt änderbar)
- Tag-Dropdown (direkt änderbar)
- Archivieren-Button (mit Bestätigungsdialog)
- Bug-Historie als Timeline (nach E6 fertig)

**Implementiert:** US-03 (alle 4 AC)

---

### T048 · Bug-Erstellen-Formular

**Was:** Formular zum Anlegen eines neuen Bugs.

**Felder:** Titel (Pflicht), Beschreibung (Pflicht), Priorität (optional), Tag (optional)  
**Submit:** `POST /api/bugs` → Redirect zur Detail-Seite  
**Validierung:** react-hook-form + zod

**Implementiert:** US-01

---

### T049 · Bug-Bearbeiten-Formular

**Was:** Vorbefülltes Formular zum Bearbeiten.

**Felder:** Titel, Beschreibung, Priorität  
**Submit:** `PUT /api/bugs/{id}`  
**Abbrechen:** Verwirft alle Änderungen

**Implementiert:** US-04

---

### T050 · Status-Dropdown + Bearbeiter-Dropdown (Inline-Editing)

**Was:** Direkte Änderung von Status und Bearbeiter in der Detail-Seite ohne separaten Edit-Modus.

**Ruft auf:** `PATCH /api/bugs/{id}/status` und `PATCH /api/bugs/{id}/assignee`

**Implementiert:** US-06 + US-08

---

### T051 · Archivieren-Button + Reaktivieren

**Was:**
- Archivieren: Button in Detail-Seite + Bestätigungsdialog → `DELETE /api/bugs/{id}`
- Reaktivieren: Button bei archivierten Bugs → `PATCH /api/bugs/{id}/restore`

**Implementiert:** US-05

---

### T052 · Hauptlayout + Navigation

**Was:** Konsistenter Header über alle Seiten.

**Enthält:** Logo, Nav-Links (Bugs, Admin für ADMIN-User), eingeloggter Username, Logout-Button

---

### T053 · Filter-UI in Bug-Liste

**Was:** Status-Filter + Prioritäts-Filter + Tag-Filter.

**Besonderheit:** Filter werden als Query-Parameter in die URL geschrieben → URL ist teilbar  
**Reset-Button:** Setzt alle Filter zurück

**Implementiert:** US-09 (alle 5 AC)

---

### T054 · Toast-Notifications

**Was:** Visuelles Feedback bei Aktionen.

| Aktion | Meldung | Dauer |
|--------|---------|-------|
| Bug erstellt | „Bug erfolgreich erstellt" | 3 Sek |
| Bug gespeichert | „Änderungen gespeichert" | 3 Sek |
| Bug archiviert | „Bug archiviert" | 3 Sek |
| Fehler | „Speichern fehlgeschlagen: [Fehler]" | 5 Sek |

---

### T055 · Loading-States + Error-Handling

**Was:**
- Skeleton-Loader oder Spinner während API-Calls
- Error-Boundary für unerwartete Fehler
- Benutzerfreundliche Fehlermeldungen (kein „undefined" oder leere Seite)

---

## E6: Erweiterte Features

> **Ziel:** Bug-Historie, Suche, Tags, Rollen, optional Kommentare.  
> **Voraussetzung:** E4 + E5 abgeschlossen  
> **Deadline:** 20.05.2026

---

### T056 · V3-Migration: Activity-Tabelle

**Datei:** `V3__activities.sql`

```sql
CREATE TABLE activities (
    id         BIGSERIAL PRIMARY KEY,
    bug_id     BIGINT NOT NULL REFERENCES bugs(id),
    user_id    BIGINT NOT NULL REFERENCES users(id),
    action     VARCHAR(50) NOT NULL,
    field      VARCHAR(50),
    old_value  TEXT,
    new_value  TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

### T057 · ActivityDao + Activity-Tracking in BugService

**Was:** Bei jedem Bug-Update automatisch Activity-Einträge erzeugen.

**Erfasste Änderungen:** Status, Priorität, Bearbeiter, Titel, Beschreibung, Tag

**Beispiel-Activity:**
```json
{
  "action": "STATUS_CHANGED",
  "field": "status",
  "oldValue": "NEU",
  "newValue": "IN_BEARBEITUNG",
  "user": "marie",
  "createdAt": "2026-05-10T14:23:00"
}
```

**Implementiert:** US-14 AC2 + AC3

---

### T058 · GET /api/bugs/{id}/activities Endpoint

**Was:** Chronologische Liste aller Änderungen (neueste oben).

**Implementiert:** US-14 AC1 + AC4

---

### T059 · Bug-Historie-Anzeige im Frontend

**Was:** Timeline-Komponente in der Bug-Detail-Seite.

**Anzeige:** Zeitstempel · User · Aktion (z.B. „Status geändert: NEU → IN_BEARBEITUNG")  
**Leerer Zustand:** „Bug erstellt von [Reporter]"

**Implementiert:** US-14 (alle 5 AC)

---

### T060 · Suchfeld in Bug-Liste (Frontend + Backend)

**Was:**
- **Frontend:** Suchfeld mit 300ms Debounce, Treffer im Titel optisch hervorgehoben
- **Backend:** `GET /api/bugs?search=login` — case-insensitive ILIKE-Suche in `title`

**Kombinierbar:** Suchfeld und Status-Filter wirken gleichzeitig

**Implementiert:** US-10 (alle 5 AC)

---

### T061 · (KANN) Kommentare-Backend

**Was:** V4-Migration + Endpoints für Kommentare.

**Datei:** `V4__comments.sql`  
**Endpoints:** `GET /api/bugs/{id}/comments`, `POST /api/bugs/{id}/comments`

**Implementiert:** US-11 Backend

---

### T062 · (KANN) Kommentare-Frontend

**Was:** Kommentar-Liste + Eingabe in der Bug-Detail-Seite.

**Anforderungen:** Plain-Text (kein HTML-Rendering), chronologische Sortierung (älteste oben), Submit-Button deaktiviert wenn leer

**Implementiert:** US-11 Frontend

---

## E7: Tests & Quality

> **Ziel:** Coverage ≥ 60% in Service-Schicht. Alle wichtigen User Stories durch Tests abgedeckt.  
> **Deadline:** 20.05.2026

---

### T063 · Unit-Tests für PasswordHasher

Mind. 3 Test-Cases: korrektes Hash + verify, falsches Passwort, verschiedene Inputs.  
Ziel: 100% Coverage für PasswordHasher-Klasse.

---

### T064 · Unit-Tests für AuthService

Testet: Login mit gültigen/ungültigen Credentials, Registrierungs-Logik, Doppelter-Username-Check.  
**Alle AC von US-12 und US-13 als Unit-Tests implementiert.**

---

### T065 · Unit-Tests für BugService

Testet: Bug-CRUD, alle State-Machine-Übergänge (erlaubt + verboten), Soft-Delete + Reaktivierung, Bearbeiter-Zuweisung.  
**Alle AC von US-01, US-04, US-05, US-06, US-08 als Unit-Tests implementiert.**

---

### T066 · Integration-Tests für Auth-Endpoints

End-to-End via MockMvc: vollständiger Flow Login → `/me` → Logout.  
Prüft korrekte HTTP-Status-Codes und Cookie-Handling.

---

### T067 · Integration-Tests für Bug-Endpoints

Vollständiger Bug-Lifecycle: Erstellen → Bearbeiten → Status ändern → Archivieren.  
Deckt Anwendungsfälle UC-01, UC-02, UC-03 aus dem Lastenheft ab.

---

### T068 · Frontend Unit-Tests (Vitest + Testing Library)

Mind. 5 Komponenten-Tests: Login-Formular, Bug-Erstellen-Formular, Auth-Context, ProtectedRoute, StatusDropdown.

---

### T069 · JaCoCo Code-Coverage-Report

Maven-Plugin konfigurieren. Report unter `target/site/jacoco/index.html`.  
**Ziel: ≥ 60% Coverage in der Service-Schicht.**  
Report wird in CI als Artefakt gespeichert.

---

### T070 · Usability-Test mit 3 Probanden

**Aufgabe für Probanden:** Ohne Erklärung einen neuen Bug anlegen.  
**Messung:** Zeit (Ziel: < 3 Minuten), Beobachtung von Problemen.  
**Protokoll:** Im Repo unter `/docs/usability/usability_test.md`

---

### T071 · SonarLint-Cleanup

IDE-Warnings in IntelliJ IDEA prüfen. Alle Critical-Findings beheben.  
**Ziel: Keine Critical-Findings** in der IDE-Analyse.

---

## E8: Deployment

> **Ziel:** App online erreichbar für Live-Demo am 22.05.2026.  
> **Deadline:** 20.05.2026

---

### T072 · Hosting-Plattform wählen und dokumentieren

**Optionen:** Railway / Render / MCI-Server  
**Kriterien:** Kosten, Java 21 Support, PostgreSQL Add-on, Static Hosting für Frontend  
**Ergebnis:** Begründete Entscheidung im Pflichtenheft dokumentieren

---

### T073 · Production-DB-Konfiguration

PostgreSQL auf Hosting-Plattform einrichten. Connection-String als Environment-Variable konfigurieren. Flyway-Migrationen laufen automatisch beim Backend-Start.

---

### T074 · Environment-Variablen-Setup

**Variables:**
```
DATABASE_URL=postgresql://...
SPRING_PROFILES_ACTIVE=prod
SESSION_SECRET=...
CORS_ALLOWED_ORIGIN=https://bugtracker.example.com
```

**Regel:** Keine Secrets im Git-Repo. `application-prod.yml` referenziert nur Env-Variablen.

---

### T075 · Frontend-Build + Static-Hosting

`npm run build` erzeugt `/dist`. Hosting via Vercel, Netlify oder Spring Boot `static/`-Ordner.  
**SPA-Fallback:** Direkter URL-Aufruf (z.B. `/bugs/42`) muss funktionieren.

---

### T076 · Deployment-Workflow in CI

GitHub Action: Bei Push auf `main` → automatisches Deployment Backend + Frontend.  
Deployment-Log sichtbar in GitHub Actions.

---

### T077 · Health-Check-Endpoint

`GET /actuator/health` → HTTP 200 wenn alles OK.  
Wird vom Hosting-Provider als Liveness/Readiness-Check verwendet.

---

### T078 · Smoke-Test nach Deployment

Nach jedem Deployment manueller Test auf Production:
1. Login funktioniert
2. Bug anlegen funktioniert
3. Status-Wechsel funktioniert

Protokoll unter `/docs/deployments/smoke_test_DATUM.md`.

---

## E9: Präsentation & Bericht

> **Ziel:** Abschlusspräsentation + Projektbericht.  
> **Bewertungsanteil:** 40% Präsentation · 30% Bericht  
> **Deadline:** 22.05.2026 (Präsentation) · 22.05.2026 23:59 Uhr (Bericht)

---

### T079 · Präsentations-Slides erstellen

**Umfang:** 10–15 Folien  
**Struktur:**
1. Problemstellung (Warum BugTracker?)
2. Personas + Use Cases
3. Architektur-Überblick
4. Live-Demo (interaktiver Teil)
5. Tech-Stack + Entscheidungen
6. Lessons Learned
7. Ausblick

**Speicherort:** `/docs/presentation/` im Repo

---

### T080 · Demo-Daten vorbereiten

Mind. 5 realistische Beispiel-Bugs in verschiedenen Stati. Reproduzierbar via `seed.sql` oder Java-Seeder.  
Demo-Daten müssen alle Features zeigen: Filter, Suche, Historie, Tags, Rollenunterschiede.

---

### T081 · Probelauf mit Team

Komplette Präsentation inkl. Live-Demo durchspielen.  
**Ziel:** < 20 Minuten Gesamtzeit.  
Identifizierte Schwächen dokumentieren und beheben.

---

### T082 · Projektbericht schreiben

**Umfang:** Mind. 10 Seiten  
**Inhalt:**
1. Projektverlauf (Zeitleiste, was lief gut / schlecht)
2. Architektur-Entscheidungen mit Begründung
3. Herausforderungen + Lösungen
4. Lessons Learned
5. Fazit

**Abgabe:** Als PDF im Sakai bis 22.05.2026, 23:59 Uhr

---

### T083 · Slides finalisieren + Demo-Skript

Letzter Polish nach dem Probelauf.  
**Demo-Skript:** Klare Schritte was wer klickt, in welcher Reihenfolge.  
**Fallback-Plan:** Video als Backup, falls die Live-Demo crasht.

---

## Abhängigkeitsübersicht

```
E1 (Setup)
  └── E2 (CI/CD)
        └── E3 (Auth)
              ├── E4 (Bug-Backend)
              │     └── E6 (Erweiterte Features)
              │           └── E7 (Tests)
              │                 └── E8 (Deployment)
              │                       └── E9 (Präsentation)
              └── E5 (Frontend)
                    └── E6 (Erweiterte Features)
```

---

*Letzte Aktualisierung: Mai 2026 · Gruppe DE 3 · MCI Innsbruck*
