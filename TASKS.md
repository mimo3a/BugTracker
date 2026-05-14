# TASKS.md — BugTracker · Vollständige Aufgabenliste

**Stand:** Mai 2026 · Version 1.1
**Bezugsdokumente:** Lastenheft v1.1, Pflichtenheft v1.1, MoSCoW-Matrix
**Status-Symbole:** ☐ offen · 🔄 in Bearbeitung · ✅ erledigt

> **Reihenfolge einhalten** — spätere Epics setzen frühere voraus.
> Die Priorität entspricht der MoSCoW-Klassifikation aus dem Pflichtenheft.

---

## Änderungshistorie dieser Datei

| Version | Datum       | Änderung |
|---------|-------------|----------|
| 1.0     | 30.04.2026  | Erstausgabe (83 Tasks) |
| 1.1     | 04.05.2026  | Tags-CRUD, Admin-User-Mgmt, FA-07-Endpoint, JMeter-Performance-Test, FA-Mapping-Tabelle, Release-Plan-Sync, CSRF/Spring-Session-Klarstellungen ergänzt (jetzt 92 Tasks) |

---

## Anforderungs-Mapping (FA → Tasks)

Diese Tabelle dient dem Nachweis, dass jede Pflichtenheft-Anforderung implementiert wird.
**Sie ist auch fürs Projektbericht-Kapitel "Anforderungs-Compliance" gedacht.**

| FA-ID | Titel | Prio | Backend-Tasks | Frontend-Tasks |
|-------|-------|------|---------------|----------------|
| FA-01 | Bug anlegen                       | Muss | T031 | T048 |
| FA-02 | Bugliste anzeigen                 | Muss | T032 | T046 |
| FA-03 | Bug-Detailansicht                 | Muss | T033 | T047 |
| FA-04 | Bug bearbeiten                    | Muss | T034 | T049 |
| FA-05 | Bug archivieren (Soft-Delete)     | Muss | T035 | T051 |
| FA-06 | Status verwalten (State-Machine)  | Muss | T036 | T050 |
| FA-07 | Priorität setzen                  | Soll | **T036b** *(neu)* | T050 |
| FA-08 | Bearbeiter zuweisen               | Soll | T037 | T050 |
| FA-09 | Nach Status filtern               | Soll | T032 (Query-Param) | T053 |
| FA-10 | Nach Titel suchen                 | Kann | T060 | T060 |
| FA-11 | Kommentarfunktion                 | Kann | T061 | T062 |
| FA-12 | Benutzeranmeldung (Login/Logout/Register) | Muss | T023, T024, T025, T026 | T043, T044, T045 |
| FA-13 | E-Mail-Benachrichtigung           | Wird nicht | — (bewusst nicht implementiert, vgl. PH 5.13) | — |
| FA-14 | Bug-Historie                      | Soll | T056, T057, T058 | T059 |
| FA-15 | Rollen-basierte Zugriffskontrolle | Muss | T021, **T021b, T038b** *(neu)* | T041 (AdminRoute), **T053b** *(neu)* |
| FA-16 | Tags verwalten                    | Soll | T029, **T038a** *(neu)* | **T053a** *(neu)* |

| NFA-ID | Anforderung | Tasks |
|--------|-------------|-------|
| NFA-01 | Benutzbarkeit (Bug anlegen < 3 Min)  | T070 |
| NFA-02 | Technologie (Spring Boot, React)      | T010, T039 |
| NFA-03 | Plattform (Webbrowser)                | T039, T040 |
| NFA-04 | Antwortzeit < 2 Sek (JMeter)          | **T070b** *(neu)* |
| NFA-05 | Wartbarkeit (Coverage ≥ 60%)          | T063–T069, T071 |
| NFA-06 | Zuverlässigkeit (Transaktionen)       | T028, T065 |
| NFA-07 | Sicherheit (BCrypt, HttpOnly, CSRF)   | T021, T027 *(siehe CSRF-Hinweis bei T021)* |
| NFA-08 | Architektur (Backend/Frontend getrennt) | T010, T039 (durch Setup gewährleistet) |

---

## Sprint-Übersicht (synchronisiert mit Pflichtenheft Abschnitt 13)

| Epic | Bereich | Tasks | Voraussetzung | Sprint-Deadline | Pflichtenheft-Release |
|------|---------|-------|---------------|-----------------|-----------------------|
| E1 Spezifikation & Setup | Docs + Infra | T001–T009b | — | 30.04.2026 | — |
| E2 DevOps & CI/CD       | Infra        | T010–T017 | E1 | 06.05.2026 | — |
| E3 Auth-System          | Backend      | T018–T028 | E2 | 09.05.2026 | **MVP v0.1** |
| E4 Bug-Verwaltung       | Backend      | T029–T038b | E3 | 16.05.2026 | **Beta v0.5** |
| E5 Frontend Setup & Pages | Frontend   | T039–T055 | E3 | 16.05.2026 | **Beta v0.5** |
| E6 Erweiterte Features  | Full-Stack   | T056–T062 | E4 + E5 | 20.05.2026 | **Release v1.0** |
| E7 Tests & Quality      | Tests        | T063–T071 | E4 + E5 | 20.05.2026 | **Release v1.0** |
| E8 Deployment           | DevOps       | T072–T078 | E6 + E7 | 21.05.2026 | **Release v1.0** |
| E9 Präsentation & Bericht | Docs       | T079–T083 | E8 | 22.05.2026 | — |

> **Anmerkung zur Sprintplanung:** Pflichtenheft Abschnitt 13 nennt einen MVP-Termin am 09.05.2026, der ursprünglich nur FA-01, FA-02, FA-03, FA-12 enthielt. In der überarbeiteten Planung wird der MVP-Stand auf "lauffähiges Auth-System mit DB-Anbindung" reduziert; die Bug-CRUD-Endpoints folgen in Beta v0.5 bis 16.05.2026. Falls der MVP-Termin im Pflichtenheft strikt eingehalten werden soll, müssten T031–T034 in E3 vorgezogen werden.

---

# E1: Spezifikation & Setup

**Ziel:** Alle Dokumente und Werkzeuge fertig, bevor mit der Programmierung begonnen wird.
**Deadline:** 30.04.2026

---

### ✅ T001 · Lastenheft v1.1 finalisieren und abgeben
**Was:** Letzte Korrekturen einarbeiten, Team-Review, als PDF im Sakai abgeben.

**Definition of Done:**
- Lastenheft v1.1 als PDF im Sakai abgegeben
- Im Repo unter `/docs/lastenheft.pdf` versioniert

---

### ✅ T002 · Pflichtenheft v1.1 schreiben
**Was:** Das Pflichtenheft beschreibt aus Sicht des Entwicklerteams, wie das Lastenheft umgesetzt wird.

**Inhalt:**
- Systemarchitektur (siehe T005)
- Datenmodell mit ER-/Klassendiagramm (siehe T003, T003b)
- API-Design (siehe T004)
- Use-Case-Übersicht (siehe T003a)
- Sequenzdiagramm Bug anlegen (siehe T003c)
- Akzeptanzkriterien je Anforderung (Gegeben/Wenn/Dann)
- Technologieentscheidungen mit Begründung
- Deployment-Konzept

**Definition of Done:**
- Pflichtenheft v1.1 als PDF im Sakai abgegeben
- Alle 4 UML-Diagramme (Architektur, Use-Case, Klassendiagramm, Sequenzdiagramm) eingebettet
- Anforderungs-IDs aus Lastenheft (FA-xx, NFA-xx, UC-xx) durchgehend referenziert

---

### ✅ T003 · ER-Datenmodell zeichnen
**Was:** Entity-Relationship-Diagramm für alle Kern-Entitäten.

**Entitäten:** users, bugs, activities, comments, tags

**Beziehungen:**
- User 1:N Bug (als Reporter)
- User 1:N Bug (als Assignee, optional)
- Bug 1:N Activity
- Bug 1:N Comment (KANN)
- Tag 1:N Bug (optional)

**Tools:** mermaid.js oder dbdiagram.io

**Definition of Done:**
- Diagramm als PNG/SVG im Repo unter `/docs/diagrams/er-diagram.png`
- In Pflichtenheft Abschnitt 6 eingebettet

---

### ✅ T003a · Use-Case-Diagramm erstellen *(neu)*
**Was:** UML-Use-Case-Diagramm mit allen 3 Akteuren (Tester, Developer, Admin) und ihren Use Cases.

**Anforderungen:**
- Akteure mit Rollen-Vererbung (`«extends»`-Pfeile: Admin ⊂ Developer ⊂ Tester)
- Use Cases farblich nach Rollenbereich gruppiert
- Querverweise auf FA-IDs und UC-IDs aus dem Lastenheft

**Tool:** draw.io oder Visual Paradigm Online

**Definition of Done:**
- Diagramm als PNG im Repo unter `/docs/diagrams/use-case-diagram.png`
- Source-Datei (`.drawio`) ebenfalls committed
- In Pflichtenheft Abschnitt 5.0 eingebettet

---

### ✅ T003b · Klassendiagramm erstellen *(neu)*
**Was:** UML-Klassendiagramm des Datenmodells.

**Klassen:** User, Bug, Activity, Tag, Comment

**Wichtig:** `Activity`-Klasse mit Feldern `action`, `field`, `oldValue`, `newValue` (NICHT nur `content`!) — Konsistenz zum Datenbankschema in Pflichtenheft 6.4.

**Definition of Done:**
- Diagramm als PNG im Repo unter `/docs/diagrams/class-diagram.png`
- Source-Datei (`.drawio`) ebenfalls committed
- In Pflichtenheft Abschnitt 6.6 eingebettet

---

### ✅ T003c · Sequenzdiagramm „Bug anlegen" erstellen *(neu)*
**Was:** UML-Sequenzdiagramm für UC-01 / FA-01.

**Lifelines:** User → BugForm → ApiClient → BugController → BugService → BugDao → ActivityService → ActivityDao → PostgreSQL

**Inhalt:**
- HTTP-Request mit Session-Cookie
- `@Valid` und `@PreAuthorize`-Check
- `@Transactional`-Loop (DB-Insert + Activity-Logging)
- Alt-Frame für Validierungsfehler

**Definition of Done:**
- Diagramm als PNG im Repo unter `/docs/diagrams/sequence-bug-create.png`
- In Pflichtenheft Abschnitt 5.1 eingebettet

---

### ✅T004 · OpenAPI-Spec für REST-API entwerfen
**Was:** `openapi.yaml` mit allen Backend-Endpoints. Single Source of Truth zwischen Backend und Frontend.

**Inhalt:**
- Auth-Endpoints (`/api/auth/login`, `/logout`, `/register`, `/me`)
- Bug-Endpoints (`/api/bugs`, `/api/bugs/{id}`, `/{id}/status`, `/{id}/priority`, `/{id}/assignee`, `/{id}/restore`)
- Activity-Endpoint (`/api/bugs/{id}/activities`)
- Tag-Endpoints (`/api/tags`)
- User-Endpoints (`/api/users`, `/api/users/{id}/role`)
- Optional Comments (`/api/bugs/{id}/comments`)
- Sicherheitsschema (Session-Cookie)

**Tool:** Swagger Editor (https://editor.swagger.io)

**Definition of Done:**
- `openapi.yaml` validiert ohne Fehler
- Im Repo unter `/docs/api/openapi.yaml`

---

### ✅ T005 · Architektur-Diagramm erstellen
**Was:** Visualisierung der Systemarchitektur (3-Schichten).

**Inhalt:** Browser → React SPA → REST/JSON → Spring Boot (Controller → Service → DAO) → JDBC → PostgreSQL · Spring Security als Cross-Cutting-Komponente

**Definition of Done:**
- Diagramm als PNG im Repo unter `/docs/diagrams/architecture.png`
- In Pflichtenheft Abschnitt 4 eingebettet

---

### ✅ T006 · MoSCoW-Matrix in Excel finalisieren
**Was:** Alle FA-IDs (FA-01 bis FA-16) in die Kategorien Muss / Soll / Kann / Won't einsortieren.

**Definition of Done:**
- Alle 16 Anforderungen kategorisiert
- Excel-Datei im Repo unter `/docs/moscow_matrix.xlsx`

---

### ✅ T007 · Neues GitHub-Repo aufsetzen
**Was:** Frisches Repository ohne KI-generierten Altcode.

**Schritte:**
- Repo `bug-tracker` auf GitHub anlegen
- Alle 5 Teammitglieder als Collaborator
- Branch-Schutzregeln (siehe T009)
- Grundstruktur: `/backend`, `/frontend`, `/docs`

**Definition of Done:**
- Repo erstellt, Team eingeladen, Branch-Schutz aktiv

---

### ✅ T008 · Jira-Board einrichten + Backlog importieren
**Was:** Jira-Projekt anlegen, CSV importieren, Epic-Hierarchie prüfen.

**Definition of Done:**
- Alle 92 Tasks importiert (in dieser Version inkl. Ergänzungen)
- Epic-Zuordnungen korrekt
- Alle Teammitglieder im Board

---

### ✅ T009 · Branch-Protection-Rules definieren
**Was:** Schutzregeln für `main` und `develop`.

**Regeln:**
- `main`: Nur Releases; direkter Push verboten; nur via PR mit ≥ 1 Approval
- `develop`: Laufende Integration; nur via PR
- Feature-Branches: `feature/T0XX-kurzbeschreibung`
- CI grün als Merge-Voraussetzung

---

### T009b · Anforderungs-Mapping-Tabelle pflegen *(neu)*
**Was:** Die FA → Task-Mapping-Tabelle (oben in dieser Datei) wird über das gesamte Projekt aktuell gehalten.

**Verantwortlich:** Wer einen Task fertigstellt, prüft, ob die Mapping-Tabelle stimmt.

**Definition of Done:**
- Bei jedem Pull Request, der einen FA implementiert, wird in der PR-Beschreibung die FA-ID genannt
- Im Projektbericht (T082) wird diese Tabelle aufgeführt

---

# E2: DevOps & CI/CD

**Ziel:** Build- und Test-Automatisierung — jeder Pull Request wird automatisch geprüft.
**Voraussetzung:** E1 abgeschlossen
**Deadline:** 06.05.2026

---

### ✅ T010 · Maven-Projekt-Setup mit Spring Boot 3.x
**Was:** `pom.xml` mit allen Dependencies erstellen.

**Dependencies:**
- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jdbc`
- `spring-boot-starter-validation`
- `flyway-core`, `flyway-database-postgresql`
- `springdoc-openapi-starter-webmvc-ui`
- `spring-boot-starter-actuator`
- `postgresql` (JDBC-Treiber)
- Test: `junit-jupiter`, `mockito-core`, `spring-boot-starter-test`

> **Hinweis:** Bewusst KEINE `spring-session-jdbc`-Abhängigkeit. Sessions werden über eine eigene `SessionStore`-Komponente in-memory verwaltet (siehe T021). Keine `spring_session`-Tabelle in der DB.

**Definition of Done:**
- `mvn clean install` läuft fehlerfrei
- Spring Boot startet ohne Fehler

---

### ✅ T011 · Docker-Compose für PostgreSQL (Dev)
**Was:** `docker-compose.yml` startet PostgreSQL für lokale Entwicklung.

```yaml
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
- `docker-compose up -d` startet PostgreSQL fehlerfrei
- Backend kann sich verbinden

---

### ✅ T012 · Dockerfile für Backend
**Was:** Multi-Stage Dockerfile.

**Status (09.05.2026, PR #8):** Implementiert + gemerged.

**Anforderungen:** Image-Größe < 300 MB

**Definition of Done:**
- `docker build` erfolgreich
- Image < 300 MB
- Container startet die Spring Boot App

---

### ✅ T013 · GitHub Actions CI Pipeline (Backend)
**Datei:** `.github/workflows/ci-backend.yml`

**Pipeline:** Checkout → Java 21 → PostgreSQL Service → `mvn test` → Fail blockiert Merge

---

### T014 · CI Pipeline (Frontend) + Hosting/Deploy
**Status (15.05.2026):** Backend-CI existiert bereits in `.github/workflows/ci-backend.yml` (Java 21 + Postgres 16, läuft auf GitHub-Spiegelung). Frontend-CI fehlt komplett. Repo-Origin ist MCI-Gitea, GitHub ist Spiegel-Target.

**Datei:** `.github/workflows/ci-frontend.yml` (analog zum Backend-Pendant).

**Pipeline (CI):** Checkout → Node 20 → `cd frontend && npm ci` → `npm run lint` (sofern vorhanden, sonst skip) → `npm run typecheck` (oder `tsc --noEmit`) → `npm run test:run` → `npm run build`. Trigger: push/PR auf `main`/`develop`, nur bei Änderungen in `frontend/**`.

**Hosting/CD:**
- **Frontend → Vercel** (Hobby, dauerhaft kostenlos). `frontend/vercel.json` ist bereits da, nur Project Import + Build Settings (Framework=Vite, Build Command=`npm run build`, Output Directory=`dist`, Root Directory=`frontend`) eintragen.
- **Backend → Render** Web Service (free 750h/Monat, schläft nach 15min) oder **Fly.io** (3 Apps gratis, kein Sleep). Verwendet `backend/Dockerfile` + `application-prod.yml`.
- **DB → Neon** Postgres (0.5 GB dauerhaft frei, kein Sleep, kein 90-Tage-Limit). Connection-String als Env-Var ins Backend.
- **Gitea ↔ GitHub-Mirror** einrichten (Gitea Settings → Repo-Mirroring), damit Vercel/Render Push-getriggert deployen können.

**Definition of Done:**
- Frontend-CI läuft grün auf jeden PR (über Gitea→GitHub-Spiegelung).
- Frontend ist unter einer öffentlichen Vercel-URL erreichbar (z.B. `bugtracker-se2.vercel.app`).
- Backend ist unter Render/Fly.io-URL erreichbar (z.B. `bugtracker-api.onrender.com`).
- Demo-Login mit `admin/admin123` funktioniert gegen die Produktion.
- README.md verlinkt beide URLs.

---

### ✅ T015 · README.md mit Setup-Anleitung
**Ziel:** Neues Teammitglied kann in unter 15 Minuten lokal starten.

**Inhalt:** Voraussetzungen, Klonen, DB starten, Backend starten, Frontend starten, Tests ausführen.

---

### ✅ T016 · .gitignore + .gitattributes konfigurieren
**Inhalte:** `target/`, `node_modules/`, `dist/`, IDE-Folder, `.env` ausschließen · LF-Konfiguration für plattformübergreifende Konsistenz.

---

### ✅ T017 · CONTRIBUTING.md schreiben
**Inhalt:** Branching-Strategie, Conventional Commits, PR-Template, Code-Review-Erwartungen.

---

# E3: Auth-System

**Ziel:** User können sich registrieren, einloggen und ausloggen. Sessions per Cookie.
**Voraussetzung:** E2 abgeschlossen
**Deadline:** 09.05.2026 *(MVP v0.1 laut Pflichtenheft)*

---

### ✅ T018 · Spring Boot Projekt initialisieren
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

**Definition of Done:** `mvn spring-boot:run` startet fehlerfrei.

---

### ✅ T019 · Flyway Setup + V1-Migration (User)
**Datei:** `src/main/resources/db/migration/V1__init.sql`

```sql
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50) UNIQUE NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20) NOT NULL DEFAULT 'TESTER'
                  CHECK (role IN ('TESTER','DEVELOPER','ADMIN')),
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);
```

> **Korrigiert gegenüber v1.0:** Keine `spring_session`-Tabelle. Sessions werden über eine eigene `SessionStore`-Komponente in-memory verwaltet (siehe T021).

**Definition of Done:**
- Migration läuft beim Backend-Start
- `users`-Tabelle in der DB vorhanden
- CHECK-Constraint auf `role` verhindert ungültige Rollen

---

### ✅ T020 · User-Model + UserDao
**Was:** Java Record `User` und DAO mit Methoden `findById`, `findByUsername`, `findByEmail`, `save`, `updateRole`, `findAll`.

**Definition of Done:**
- Unit-Tests für alle UserDao-Methoden grün
- Klartext-Passwörter werden nie gespeichert oder geloggt

---

### ✅ T021 · Spring Security Konfiguration
**Was:** `SecurityConfig.java` — Cookie-basierte Auth mit dediziertem `SessionStore`.

**Anforderungen:**
- Public: `POST /api/auth/login`, `POST /api/auth/register`
- Geschützt: alle anderen `/api/**`
- Session-Management: `SessionCreationPolicy.STATELESS` (kein Spring-`HttpSession`)
- Sessions werden über eine dedizierte `SessionStore`-Komponente verwaltet (siehe unten)
- Password-Encoder: `BCryptPasswordEncoder` (cost 10)

> **Architektur-Entscheidung (Abweichung von v1.0-Spec):**
> Statt `SessionCreationPolicy.IF_REQUIRED` mit Spring's `HttpSession` setzen wir auf eine eigene `SessionStore`-Komponente:
> - **`SessionStore`** (`@Component`, in-memory `ConcurrentHashMap`): erzeugt 32-Byte-Tokens via `SecureRandom`, mappt Token → `Session(userId, username, role)`.
> - **`SessionAuthFilter`** (`OncePerRequestFilter`): liest `session`-Cookie, lädt aus `SessionStore`, setzt `UsernamePasswordAuthenticationToken` in den `SecurityContextHolder`.
> - **`SessionCreationPolicy.STATELESS`** signalisiert Spring, keine eigene `HttpSession` anzulegen — die Session-Logik liegt komplett in unserer Komponente.
>
> **Vorteile:** Kontrolle über Token-Format/Cookie-Flags, keine Abhängigkeit von Spring-Session, einfacher zu testen.
>
> **Bekannte Limitierungen** (im Projektbericht zu nennen):
> - Sessions im Speicher → bei Backend-Restart sind alle User ausgeloggt
> - Kein Multi-Instance-Support (nicht kritisch für unser Single-Instance-Deployment)
> - Kein Session-Timeout/TTL implementiert (kann nachgerüstet werden, wenn nötig)

> **CSRF-Entscheidung (Abweichung von Pflichtenheft NFA-07):**
> Pflichtenheft NFA-07 nennt "CSRF-Schutz aktiv". Wir setzen stattdessen auf eine Token-/Cookie-Kombination, die für unser Setup (REST-API + SPA-Frontend) ausreichend ist:
>
> 1. **`HttpOnly`-Session-Cookie** — JavaScript kann den Session-Token nicht auslesen, also kann eine fremde Seite den Token nicht in einen Header schreiben.
> 2. **`SameSite=Lax`** — Browser sendet das Cookie bei Cross-Site-`POST`/`PUT`/`DELETE`-Requests **nicht** mit. Das ist exakt der Angriffsvektor, den klassisches CSRF abdecken soll.
> 3. **CORS mit `allowCredentials=true` + Whitelist** — nur `localhost:5173` (Dev) und ggf. konfigurierte Prod-Origin (siehe Issue 6) dürfen Credentials senden.
>
> Spring's CSRF-Schutz (`CookieCsrfTokenRepository`) ist primär für formularbasierte HTML-Anwendungen gedacht. Für eine REST-API mit Session-Cookies bringt er bei vorhandenem `SameSite=Lax` keinen zusätzlichen Schutz, würde aber Frontend-Komplexität (CSRF-Token-Header bei jedem Mutating-Request) erzeugen.
>
> **Im Pflichtenheft NFA-07 wird die Formulierung daher angepasst auf:** *„Schutz vor CSRF durch HttpOnly-Cookies + SameSite=Lax + CORS-Whitelist (kein Token-basierter CSRF-Schutz)"*.
>
> **Code:** `SecurityConfig.csrf(AbstractHttpConfigurer::disable)` mit Kommentar, der auf diese Entscheidung verweist.

**Definition of Done:**
- `GET /api/bugs` ohne Login → HTTP 401
- `POST /api/auth/login` ohne Auth erreichbar
- CSRF-Verhalten dokumentiert (entweder aktiv mit Frontend-Anpassung oder dokumentiert deaktiviert)
- `SessionStore` + `SessionAuthFilter` als Komponenten implementiert

---

### T021b · @PreAuthorize-Annotationen auf Controllern *(neu — FA-15)*
**Was:** Methoden-Level-Security mit `@EnableMethodSecurity`.

**Beispiel:**
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/api/tags/{id}")
public ResponseEntity<Void> deleteTag(@PathVariable Long id) { ... }

@PreAuthorize("hasAnyRole('DEVELOPER','ADMIN')")
@PutMapping("/api/bugs/{id}")
public ResponseEntity<Bug> updateBug(...) { ... }
```

**Rollen-Matrix (vgl. Pflichtenheft 9.3):**
| Rolle      | Zugriff |
|------------|---------|
| TESTER     | FA-01, FA-02, FA-03, FA-09, FA-10, FA-11 |
| DEVELOPER  | + FA-04, FA-05, FA-06, FA-07, FA-08, FA-14 |
| ADMIN      | + FA-15, FA-16 |

**Definition of Done:**
- Alle Endpoints mit passendem `@PreAuthorize` annotiert
- Integration-Tests prüfen Zugriffskontrolle (TESTER versucht Tag-Delete → HTTP 403)

---

### ✅ T022 · CORS-Konfiguration für React-Frontend
**Was:** `CorsConfig.java` — Cross-Origin-Requests vom Frontend erlauben.

```
allowedOrigins: ["http://localhost:5173", "${app.cors.allowed-origin}"]
allowedMethods: ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"]
allowCredentials: true   // wichtig für Session-Cookies
allowedHeaders: ["*", "X-XSRF-TOKEN"]
exposedHeaders: ["X-XSRF-TOKEN"]
```

---

### ✅ T023 · POST /api/auth/login Endpoint
**Request:** `{ "username": "marie", "password": "secret123" }`
**Response (Erfolg):** HTTP 200 + User-Daten + Session-Cookie
**Response (Fehler):** HTTP 401 + `{ "error": "Login fehlgeschlagen" }` (kein Hinweis ob Username existiert)

**Implementiert:** US-12 AC1 + AC2

---

### ✅ T024 · POST /api/auth/logout Endpoint
**Was:** Invalidiert Session, löscht Cookie. **Implementiert:** US-12 AC3.

---

### ✅ T025 · POST /api/auth/register Endpoint
**Request-Payload (alle vier Felder Pflicht):**
```json
{
  "username": "marie",
  "email": "marie@example.com",
  "password": "secret123",
  "passwordConfirm": "secret123"
}
```

**Validierungen (via `jakarta.validation`):**
- Username: `@NotBlank @Size(min=3, max=50)` + Eindeutigkeitsprüfung in DAO
- Email: `@NotBlank @Email` + Eindeutigkeitsprüfung in DAO
- Passwort: `@NotBlank @Size(min=8)`
- `passwordConfirm`: `@NotBlank` + Inline-Match-Check gegen `password`

**Fehler-Responses:**
- 400 mit Feld-zu-Fehler-Map bei Annotation-Verletzung (über `MethodArgumentNotValidException` → `GlobalExceptionHandler`)
- 400 `{"error": "Passwörter stimmen nicht überein"}` bei `passwordConfirm`-Mismatch
- 409 `{"error": "Username or email already taken"}` bei Konflikt

**Default-Rolle:** TESTER · **Implementiert:** US-13 (alle 5 AC)

> **Frontend-Hinweis (T045):** Die Register-Form muss alle vier Felder senden — `passwordConfirm` ist Pflicht und wird sowohl auf NotBlank als auch auf Gleichheit mit `password` geprüft. Ohne dieses Feld wird die Antwort 400 sein.

---

### ✅ T026 · GET /api/auth/me Endpoint
**Was:** Gibt eingeloggten User zurück, im Frontend bei App-Start für Session-Persistenz aufgerufen.

**Implementiert:** US-12 AC4 + AC5

---

### ✅ T027 · Password-Hashing mit BCrypt
**Was:** Helper `PasswordHasher` mit `hash()` und `verify()`.

**Anforderungen:** Cost factor ≥ 10 · Niemals Klartext speichern oder loggen

**Definition of Done:**
- Unit-Tests grün (mind. 3 Cases)
- 100% Coverage für `PasswordHasher`

---

### ✅ T028 · GlobalExceptionHandler
**Was:** `@ControllerAdvice` für einheitliche Fehlerantworten.

| Exception | HTTP | Response |
|-----------|------|----------|
| MethodArgumentNotValidException | 400 | Feld-spezifische Fehler |
| UsernameNotFoundException | 401 | `{ "error": "Login fehlgeschlagen" }` |
| InvalidStatusTransitionException | 400 | `{ "error": "Ungültiger Statuswechsel" }` |
| AccessDeniedException | 403 | `{ "error": "Keine Berechtigung" }` |
| EntityNotFoundException | 404 | `{ "error": "Nicht gefunden" }` |
| Exception (Fallback) | 500 | `{ "error": "Interner Fehler" }` — KEIN Stack-Trace |

---

# E4: Bug-Verwaltung

**Ziel:** Backend-API für alle Bug-CRUD-Operationen + Tags + Admin-User-Mgmt.
**Voraussetzung:** E3 abgeschlossen
**Deadline:** 16.05.2026 *(Beta v0.5)*

---

### ✅ T029 · V2-Migration: Bug-Tabelle + Tags-Tabelle + Junction
**Datei:** `V2__bugs.sql`

```sql
CREATE TABLE tags (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) UNIQUE NOT NULL,
    color      VARCHAR(7),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    archived    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Many-to-Many: ein Bug kann beliebig viele Tags haben, ein Tag kann an
-- beliebig vielen Bugs hängen.
CREATE TABLE bug_tags (
    bug_id BIGINT NOT NULL REFERENCES bugs(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE RESTRICT,
    PRIMARY KEY (bug_id, tag_id)
);

-- Standard-Tags für Demo
INSERT INTO tags (name, color) VALUES
    ('Backend', '#3B82F6'),
    ('Frontend', '#10B981'),
    ('Bug', '#EF4444'),
    ('Feature', '#8B5CF6');
```

> **Schema-Entscheidungen:**
> - **Many-to-Many statt Single-Tag:** Pflichtenheft FA-16 verlangt „Tags verwalten" — wir interpretieren das als „mehrere Tags pro Bug möglich". Die Junction-Table `bug_tags` mit Composite-PK `(bug_id, tag_id)` verhindert duplikate.
> - **`bug_id ON DELETE CASCADE`:** Bei Hard-Delete eines Bugs (passiert über `archived` normalerweise nicht) verschwinden die Junction-Einträge mit. Sicherheitsnetz.
> - **`tag_id ON DELETE RESTRICT`:** Admin kann einen Tag nicht löschen, solange er noch zugewiesen ist. Verhindert versehentlichen Datenverlust.
> - **`TIMESTAMP WITH TIME ZONE` + `CURRENT_TIMESTAMP`:** konsistent mit V1 (Auth-System). Standardwahl für mehrsprachige/multi-region Apps.

**Definition of Done:**
- Migration läuft fehlerfrei
- Enum-Constraints aktiv
- Standard-Tags vorhanden
- `bug_tags`-Junction-Table mit Composite-PK + FKs angelegt

---

### ✅ T030 · Bug-Model + BugDao
**Was:** Java Record `Bug` + DAO.

**BugDao-Methoden:**
- `findAll(BugFilter filter, int page)` — mit Status/Priority/Assignee/Tag/Search-Filter + Pagination
- `findById(Long id)` — mit JOIN auf users (Reporter, Assignee) und tags
- `save(Bug bug)`, `update(Bug bug)`
- `archive(Long id)` / `restore(Long id)`

**Definition of Done:**
- Unit-Tests für alle Methoden grün
- Filter-Kombinationen getestet

---

### ✅ T031 · POST /api/bugs Endpoint *(FA-01)*
**Request:**
```json
{
  "title": "Login-Button reagiert nicht",
  "description": "Reproduktionsschritte: ...",
  "priority": "HOCH",
  "tagId": 1
}
```

**Verhalten:** Reporter = eingeloggter User · Default-Status NEU · Default-Priorität MITTEL

**Implementiert:** US-01 (alle 6 AC)

---

### ✅ T032 · GET /api/bugs Endpoint *(FA-02 + FA-09)*
**Query-Parameter:** `status` (multi), `priority`, `assigneeId`, `tagIds` (multi, OR-Semantik), `search`, `page`, `archived`

**Response:**
```json
{ "bugs": [...], "total": 73, "page": 0, "pageSize": 50 }
```

**Status (12.05.2026, PR #15):** Implementiert (Oleksandr) + Review-Adapt (Maksim).
- Sortierung `created_at DESC, id DESC` als Default
- `pageSize` fest bei 50 (nicht client-konfigurierbar — MVP-Entscheidung)
- snake_case-Alias `assignee_id` entfernt — durchgängig camelCase
- `tagId` (single) → `tagIds` (List<Long>) mit OR-Semantik für Multi-Tag-Filter
- `BugPage` als eigene Datei `service/BugPage.java` (statt nested Record)
- Alle authentifizierten User dürfen lesen — Rollen-Restriktion nur beim Editieren (T034)
- 40/40 Tests grün (BugControllerTest + erweiterter BugDaoTest mit Multi-Tag-OR-Test)

---

### ✅ T033 · GET /api/bugs/{id} Endpoint *(FA-03)*
**Response:** Alle Bug-Felder + reporterName + assigneeName + tagName

**Status (13.05.2026, PR #21):** Implementiert (Patrick) — Trivial-Endpoint (+11 LOC), kein Adapt nötig.
- Auth-Pflicht greift via globaler `SecurityConfig.anyRequest().authenticated()` — kein expliziter `CurrentSession.require()` nötig
- `BugDao.findById` enriched bereits Reporter/Assignee/Tags via JOIN
- Archivierte Bugs lesbar (FA-03 ist Read-Only, konsistent mit T035-Semantik)
- 404 mit `"Bug nicht gefunden"` — gleiche Fehlersprache wie T034
- Unit-Tests folgen mit T065

---

### ✅ T034 · PUT /api/bugs/{id} Endpoint *(FA-04)*
**Was:** Bug bearbeiten (Titel, Beschreibung — Priorität via separatem PATCH, siehe T036b).

**Verhalten:** Erzeugt Activity-Einträge für jedes geänderte Feld.

**Status (10.05.2026, PR #13):** Implementiert (Patrick) + Review-Adapt (Maksim).
- Authorization: DEVELOPER + ADMIN für jeden Bug, TESTER nur eigenen als Reporter → 403 sonst
- 409 CONFLICT bei archivierten Bugs (Soft-Delete-Konsistenz mit T035)
- `priority` aus UpdateBugRequest entfernt — läuft über T036b PATCH /priority
- OpenAPI auf M:N-Tag-Modell synchronisiert (`tagIds: array`)
- Activity-Logging folgt mit T057 (blockierte vorher durch T056)
- Unit-Tests folgen mit T065

---

### ✅ T035 · Soft-Delete + Reaktivierung *(FA-05)*
- `PATCH /api/bugs/{id}/archive`: setzt `archived = true` + `status = ARCHIVIERT`
- `PATCH /api/bugs/{id}/restore`: setzt `archived = false` + `status = NEU` (Workflow-Reset, weil `ARCHIVIERT` terminal ist)

**Status (13.05.2026, PR #22):** Implementiert (Patrick) + Review-Adapt (Maksim).
- HTTP-Method-Drift gegenüber alter Spec-Variante (`DELETE /api/bugs/{id}`): Symmetrische PATCH-Paar-Lösung gewählt (konsistent mit `/status`, `/priority`).
- Restore-Status-Quirk gefixt: `BugDao.setArchived` setzt beim Restore jetzt `status = NEU` (vorher blieb `ARCHIVIERT`, was archived=false + status=ARCHIVIERT als inkonsistenten Zustand hinterließ).
- Rolle: DEVELOPER + ADMIN → 403 für TESTER
- 409 wenn schon im Zielzustand (idempotente Fehlersemantik)
- Activity-Logging via `field=archived` + alte/neue Werte (`recordChange`-Pattern)
- OpenAPI auf PATCH /archive + /restore mit 409 synchronisiert
- Unit-Tests folgen mit T065

---

### ✅ T036 · PATCH /api/bugs/{id}/status Endpoint *(FA-06)*
**State-Machine (erlaubte Übergänge):**
- `NEU → IN_BEARBEITUNG, ARCHIVIERT`
- `IN_BEARBEITUNG → IM_REVIEW, ARCHIVIERT`
- `IM_REVIEW → ERLEDIGT, ABGELEHNT, ARCHIVIERT`
- `ERLEDIGT → ARCHIVIERT`
- `ABGELEHNT → ARCHIVIERT`
- `ARCHIVIERT → (terminal, Restore via T035)`

**Verbotener Übergang:** HTTP 409 (Conflict) + `{ "error": "Status-Wechsel nicht erlaubt: ERLEDIGT → NEU" }`

**Status (12.05.2026, PR #18):** Implementiert (Maksim, ersetzt Oleksandrs verworfenen Branch).
- `service/BugStatusStateMachine.java` als @Component
- DEVELOPER + ADMIN dürfen, TESTER → 403
- Activity-Tracking automatisch (T057)
- 6 BugStatusStateMachineTest + Live-Test gegen Postgres
- Status-Code-Änderung vs. Spec: 409 statt 400 (semantisch korrekter — Request war wohlgeformt, Server-State erlaubt's nicht)

---

### ✅ T036b · PATCH /api/bugs/{id}/priority Endpoint *(neu — FA-07)*
**Was:** Eigener Endpoint für Prioritätsänderung (gemäß Pflichtenheft 5.7).

**Request:** `{ "priority": "KRITISCH" }`

**Validierung:** Eindeutiger gültiger Wert: `NIEDRIG`, `MITTEL`, `HOCH`, `KRITISCH`

**Verhalten:**
- Erzeugt Activity-Eintrag (FA-14)
- Default beim Erstellen ist `MITTEL`

**Implementiert:** US-07 (alle 5 AC, vgl. Pflichtenheft 5.7)

**Definition of Done:**
- Erfolg: HTTP 200
- Ungültiger Wert: HTTP 400
- Activity-Eintrag in DB

**Status (15.05.2026, PR #26):** Implementiert (Oleksandr) + Review-Adapt (Maksim).
- Folgt exakt dem T037-Muster: `requireEditable` (404/409), `requireActor` (401), `isPrivileged` (403)
- DTO `UpdatePriorityRequest` mit `@NotNull(message = "Priorität ist erforderlich") BugPriority priority` (Pflicht, anders als T037's nullable assigneeId)
- Rolle: DEVELOPER + ADMIN dürfen, TESTER → 403 (FA-07)
- Activity-Logging via `recordChange`-Konvention (`field=priority`, alte/neue Enum als String); kein Log bei identischem Wert
- Controller-Reihenfolge nach Merge: `status → priority → assignee → archive → restore`
- **Bonus**: `HttpMessageNotReadableException`-Handler in `GlobalExceptionHandler` — kaputtes JSON oder unbekannte Enum-Werte liefern jetzt 400 statt 500 (alle Endpoints profitieren)
- **Adapt:** Branch zweigte von `c6e49aa` ab — auf aktuellen `develop` rebased, 3 Konflikte (BugController + GlobalExceptionHandler auto-merged, BugControllerTest manuell: alle 4 Tests behalten + Imports dedupliziert).
- **Flaky-Test-Fix dabei:** `AuthControllerTest.registerReturnsFieldValidationMessages` hatte `password="short"` (verletzt `@Size` UND `@Pattern`); Hibernate Validator-Reihenfolge bei Multi-Constraint-Failures nicht-deterministisch → Test flaky seit T038-Merge. Payload auf `"1234567"` geändert (nur `@Size` feuert deterministisch).
- **Tests:** 5 Service-Unit-Tests (`BugServicePriorityTest`) + 2 Controller-Tests; 68/68 Backend-Tests grün.

---

### ✅ T037 · PATCH /api/bugs/{id}/assignee Endpoint *(FA-08)*
**Request:** `{ "assigneeId": 3 }` oder `{ "assigneeId": null }` (entfernt Bearbeiter)

**Validierung:** User muss existieren (HTTP 404 sonst)

**Status (13.05.2026, PR #23):** Implementiert (Patrick) + Review-Adapt (Maksim).
- Folgt sauber dem `updateStatus`-Muster: `requireEditable` (404/409), `requireActor` (401), `isPrivileged` (403)
- DTO mit `Long` (boxed) — erlaubt `null` als legitime Eingabe (= Bearbeiter entfernen, statt 0 durch Jackson-Default)
- Rolle: DEVELOPER + ADMIN dürfen Bearbeiter setzen, TESTER → 403
- 404 wenn `assigneeId` auf nicht-existierenden User zeigt (Spec-konform)
- Activity-Logging via existierender `recordChange`-Konvention (`field=assigneeId`, alte/neue ID als String)
- **Adapt:** Branch zweigte von `fb0ebc8` ab — auf aktuellen `develop` rebased, 2 Konflikte in `BugController` + `BugService` manuell aufgelöst (T035 + T037 wollten beide direkt hinter `updateStatus` einfügen; Reihenfolge jetzt: `status → assignee → archive → restore`)
- **Tests nachgeliefert:** `BugServiceAssigneeTest` mit 7 Mockito-Unit-Tests (TESTER→403, Happy-Path mit Activity, null=Unassign, User-not-found→404, archived→409, Bug-not-found→404, No-Op skip)

---

### ✅ T038 · Bean-Validation für alle Request-Bodies
**Beispiel:**
```java
public record CreateBugRequest(
    @NotBlank(message = "Titel ist erforderlich") @Size(max = 255) String title,
    @NotBlank(message = "Beschreibung ist erforderlich") String description,
    String priority,
    Long tagId
) {}
```

**Status (14.05.2026, PR #25):** Implementiert (Oleksandr) + Review-Adapt (Maksim).
- `@Valid` + Constraints auf Auth-DTOs in `Requests.java` (Register/Login/ChangePassword): `@NotBlank`, `@Size`, `@Email`; Passwort behält zusätzlich `@Pattern(.*\d.*)` aus develop (Sicherheits-Regel — Zahl im Passwort Pflicht).
- `AuthController`: manuelle `isBlank`-Checks entfernt → ersetzt durch `@Valid`.
- `BugController.updateAssignee` (T037): `@Valid` ergänzt (DTO selbst noch ohne Constraints — `null = unassign` bleibt zulässig).
- `GlobalExceptionHandler.handleValidation`: `LinkedHashMap` für deterministische Feld-Reihenfolge, null-safe Default-Message, Merge-Function bei doppelten Constraints (behält erste).
- **Adapt:** Müll-Commit `e1db824` (fremde Dev-Setup-Reste: AGENTS.md, leere `package-lock.json` im Repo-Root, `skills/spring-security/SKILL.md`) per Rebase entfernt; Konflikt in `Requests.java` als Union beider Seiten gelöst (develop's `@Pattern` + T038's `@Size(min=8, max=255)`); zerstörte Umlaute restauriert ("Ungueltiges" → "Ungültiges", "Passwort-Bestaetigung" → "Passwort-Bestätigung"); `BugControllerTest.pageSize`-Assertion auf `DEFAULT_PAGE_SIZE=20` korrigiert.
- **Tests nachgeliefert:** `AuthControllerTest` (3 Tests: Login/Register/ChangePassword mit deutschen Field-Messages), `BugControllerTest` (Validation für POST/PUT `/api/bugs` + Filter/Pagination-Coverage). 61/61 Backend-Tests grün.
- **Follow-up offen (außerhalb Scope):** Tote Records in `Requests.java` (`CreateBug`, `UpdateBug`, `BulkUpdateBugs`, `CreateProject`, …) werden von keinem Controller mehr genutzt — echte Bug-DTOs leben in `controller/dto/`. Cleanup-Ticket wert.

---

### T038a · Tag-CRUD Backend *(neu — FA-16)*
**Was:** Vollständige CRUD-API für Tags (nur ADMIN).

**Endpoints:**
| Methode | URL | Beschreibung | Rolle |
|---------|-----|--------------|-------|
| `GET`    | `/api/tags`         | Alle Tags abrufen (für Dropdown) | alle eingeloggten |
| `POST`   | `/api/tags`         | Neuen Tag anlegen | ADMIN |
| `PUT`    | `/api/tags/{id}`    | Tag bearbeiten (Name + Farbe) | ADMIN |
| `DELETE` | `/api/tags/{id}`    | Tag löschen (vorher: tag_id auf null bei betroffenen Bugs) | ADMIN |

**Komponenten:**
- `Tag` Java Record
- `TagDao`
- `TagService` (mit Validierung: Name nicht leer, Farbe valides Hex-Format)
- `TagController` mit `@PreAuthorize("hasRole('ADMIN')")` auf POST/PUT/DELETE

**Definition of Done:**
- TESTER versucht POST /api/tags → HTTP 403
- ADMIN kann CRUD durchführen
- Beim Löschen wird `bugs.tag_id` auf NULL gesetzt (kein Cascade-Delete der Bugs)

**Implementiert:** US-16 (alle 3 AC, vgl. Pflichtenheft 5.16)

---

### ✅ T038b · Admin-User-Management Backend *(neu — FA-15 AC1+AC2)*
**Was:** Endpoints für Admin-Übersicht aller User und Rollen-Änderung.

**Endpoints (final, an Frontend-Contract angepasst):**
| Methode | URL | Beschreibung | Rolle |
|---------|-----|--------------|-------|
| `GET`   | `/api/users`     | Alle User (UserWithoutHash); aus AuthController nach `UserController` refactored | eingeloggt (alle) |
| `PATCH` | `/api/users/{id}` | Body `{ role?: UserRole, active?: boolean }` — beide optional, mind. eines gesetzt | ADMIN |

**Status (15.05.2026, PR #29):** Implementiert (Maksim) — Solo-Build nach Sprint1-Backend-Review-Cluster.
- Neue Klasse `UserController` + `UserService` + DTO `UpdateUserRequest` (in `controller/dto/`); `UserDao.updateActive(id, active)` ergänzt; `listUsers` aus `AuthController` rausgezogen.
- **Lock-out-Schutz (FA-15)**: ADMIN darf weder eigene Rolle ändern noch sich selbst deaktivieren → 400 mit deutscher Fehlermeldung. ADMIN darf eigene Rolle auf "ADMIN" setzen (no-op, kein Lock-out-Risiko).
- **403** für nicht-ADMIN auf PATCH; **404** bei unbekanntem Target.
- **Tests**: 10 `UserServiceTest` (Mockito) + 6 `UserControllerTest` (WebMvcTest), 96/96 Backend-Tests grün (+16 netto).
- **GET `/api/users`-Semantik**: bleibt für alle eingeloggten User (Frontend ruft das im BugForm für Assignee-Dropdown auf — wäre sonst Breaking-Change). PasswordHash war sowieso nie im Response.

**Validierung:**
- Rolle muss in `{ TESTER, DEVELOPER, ADMIN }` sein
- ADMIN kann sich nicht selbst die ADMIN-Rolle entziehen (Schutz vor Lock-out)

**Implementiert:** US-15 (AC1: User-Übersicht, AC2: Rolle ändern)

**Definition of Done:**
- DEVELOPER versucht GET /api/users → HTTP 403
- ADMIN kann Rolle eines anderen Users ändern
- ADMIN kann seine eigene ADMIN-Rolle nicht ändern → HTTP 400 + Fehlermeldung
- Activity-Eintrag wird erzeugt (optional, falls über Activity-Mechanismus)

---

# E5: Frontend Setup & Pages

**Ziel:** React-SPA mit allen UI-Seiten.
**Voraussetzung:** E3 abgeschlossen
**Deadline:** 16.05.2026

---

### ✅ T039 · React + Vite + TypeScript Projekt-Setup
**Befehl:** `npm create vite@latest frontend -- --template react-ts`

**Konfiguration:** `tsconfig.json` strict mode · Verzeichnisstruktur `src/{pages,components,hooks,context,lib}`

---

### ✅ T040 · Tailwind CSS Setup
```bash
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

---

### ✅ T041 · React Router Setup
**Routes:**
```
/             → /bugs (wenn eingeloggt) sonst /login
/login        → LoginPage
/register     → RegisterPage
/bugs         → BugListPage
/bugs/new     → BugCreatePage
/bugs/:id     → BugDetailPage
/admin/users  → AdminUsersPage  (nur ADMIN)
/admin/tags   → AdminTagsPage   (nur ADMIN)
```

**Wrapper-Komponenten:**
- `<ProtectedRoute>` — leitet zu `/login` um wenn nicht eingeloggt
- `<AdminRoute>` — leitet zu `/bugs` um wenn nicht ADMIN

---

### ✅ T042 · API-Client mit fetch + Custom Hooks
**Was:** Zentraler HTTP-Client + Hooks `useBugs`, `useBug(id)`, `useUsers`, `useTags`.

**Wichtig:** `credentials: 'include'` auf allen Requests · CSRF-Token-Handling falls aktiviert (siehe T021)

---

### ✅ T043 · Auth-Context + AuthProvider
**Was:** React Context mit `user`, `loading`, `login()`, `logout()`.

**Verhalten:** Beim App-Start `GET /api/auth/me` → User bleibt nach Page-Reload eingeloggt.

**Implementiert:** US-12 AC5

**Status (12.05.2026, PR #16):** Implementiert (Maksim).
- `context/AuthContext.tsx` mit `AuthProvider` + `useAuth`-Hook
- Auto-Restore via `GET /api/auth/me` beim App-Mount
- `components/ProtectedRoute.tsx` als Route-Guard (redirect → /login wenn unauth)

---

### ✅ T044 · Login-Seite + Auth-Flow *(US-12 AC1+AC2)*
**Felder:** Username, Passwort · Bei Erfolg Redirect zu `/bugs` · Bei Fehler Meldung + Passwort-Feld leeren

**Status (12.05.2026, PR #16):** Implementiert (Maksim).
- `pages/LoginPage.tsx` mit username/password Form
- 401 → "Falscher Benutzername oder Passwort" (deutsch)
- BugListPage-Header: User-Anzeige (`username · role`) + Logout-Button
- `ApiError`-Klasse für status-spezifische Fehler-Behandlung im UI
- Mit-gefixt: UserDao-Bug in `findByUsername`/`findById`/`findByEmail`/`findAll` (SELECT-Spalten passten nicht zum ResultSet-Mapping, Login warf 500)
- Mit-gefixt: Frontend-Bug-Type auf M:N-Tags (`tagIds`/`tagNames` Arrays) aligned mit Backend nach T029

---

### ✅ T045 · Register-Seite *(US-13)*
**Felder:** Username, E-Mail, Passwort, Passwort bestätigen · Validation mit `react-hook-form` + `zod`

**API-Aufruf:** `POST /api/auth/register` mit Body `{ username, email, password, passwordConfirm }` — **alle vier Felder Pflicht**, sonst 400 vom Backend (siehe T025).

**Frontend-seitige Validation (zod-Schema, sollte mit Backend-Regeln übereinstimmen):**
- `username`: 3–50 Zeichen
- `email`: valides E-Mail-Format
- `password`: min. 8 Zeichen
- `passwordConfirm`: muss `password` entsprechen (`refine`-Check in zod)

**Fehler-Anzeige:** Bei 400 → Feld-spezifische Fehler aus der Response-Map anzeigen (z. B. unter dem jeweiligen Input). Bei 409 → globale Meldung „Username oder E-Mail bereits vergeben".

---

### ✅ T046 · Bug-Liste-Seite (/bugs) *(US-02)*
**Spalten:** ID, Titel, Status (farbiges Badge), Priorität, Tag, Bearbeiter, Erstelldatum
**Leerer Zustand:** Hinweis + Button „Neuer Bug"
**Pagination:** 50 pro Seite

---

### ✅ T047 · Bug-Detail-Seite (/bugs/:id) *(US-03)*
**Enthält:**
- Alle Bug-Felder
- Edit-Button → öffnet Bearbeiten-Formular
- Status-Dropdown (Inline-Edit)
- Bearbeiter-Dropdown (Inline-Edit)
- Tag-Dropdown (Inline-Edit)
- Priorität-Dropdown (Inline-Edit, ruft T036b auf)
- Archivieren-Button mit Bestätigungsdialog
- Bug-Historie als Timeline (siehe T059)

---

### ✅ T048 · Bug-Erstellen-Formular *(US-01)*
**Felder:** Titel (Pflicht), Beschreibung (Pflicht), Priorität (optional), Tag (optional)

**Status (12.05.2026, PR #17):** Implementiert (Maksim).
- `pages/BugCreatePage.tsx` — Form mit Title (max 255 + Live-Counter), Description, Priority-Dropdown, Tags als Toggle-Buttons
- `lib/api.ts`: `api.createBug()` + `CreateBugInput`-Type
- `lib/seedTags.ts`: 4 Seed-Tags aus V2-Migration hardcoded (TODO → T038a für `GET /api/tags`)
- Route `/bugs/new` mit `ProtectedRoute`, „+ new"-Button im BugListPage-Header
- Mit-gefixt: BugDao-Bug in BugRowMapper (`TIMESTAMP WITH TIME ZONE` → `OffsetDateTime` statt direkt `LocalDateTime`, sonst PSQLException sobald Tabelle nicht leer)

---

### ✅ T049 · Bug-Bearbeiten-Formular *(US-04)*
**Vorbefülltes Formular** · Submit `PUT /api/bugs/{id}` · Abbrechen verwirft Änderungen

---

### ✅ T050 · Inline-Editing-Dropdowns *(US-06, US-07, US-08)*
**Was:** Status, Priorität, Bearbeiter, Tag direkt in Detail-Seite ändern.

**Ruft auf:**
- `PATCH /api/bugs/{id}/status` (T036)
- `PATCH /api/bugs/{id}/priority` (T036b)
- `PATCH /api/bugs/{id}/assignee` (T037)
- `PUT /api/bugs/{id}` (für Tag-Wechsel, falls kein eigener Endpoint)

---

### ✅ T051 · Archivieren-Button + Reaktivieren *(US-05)*

---

### ✅ T052 · Hauptlayout + Navigation
**Header:** Logo · Nav-Links (Bugs, Admin-Bereich für ADMIN) · Username · Logout-Button

---

### ✅ T053 · Filter-UI in Bug-Liste *(US-09)*
**Filter:** Status, Priorität, Tag, Bearbeiter
**URL-Sync:** Filter als Query-Parameter → URL teilbar
**Reset-Button** stellt Default-Liste wieder her

---

### ✅ T053a · AdminTagsPage (Frontend) *(neu — FA-16)*
**Was:** UI für Tag-Verwaltung, nur ADMIN sichtbar.

**Features:**
- Liste aller Tags mit Name + Farbvorschau
- Button „Neuer Tag" → Modal mit Name + Color-Picker
- Edit-Button pro Tag
- Delete-Button pro Tag mit Bestätigungsdialog („Wirklich löschen? Bugs verlieren ihre Tag-Zuordnung.")
- Validierung: Name nicht leer

**Route:** `/admin/tags` (geschützt durch `<AdminRoute>`)
**API:** Nutzt T038a-Endpoints

**Implementiert:** US-16 (Pflichtenheft 5.16)

---

### ✅ T053b · AdminUsersPage (Frontend) *(neu — FA-15)*
**Was:** UI für Benutzerverwaltung, nur ADMIN sichtbar.

**Features:**
- Tabelle: Username, E-Mail, Rolle, Erstelldatum
- Rollen-Dropdown pro User (Inline-Edit) → ruft `PATCH /api/users/{id}/role` (T038b) auf
- Eigener User in der Liste deaktiviert / nicht änderbar (Lock-out-Schutz)

**Route:** `/admin/users` (geschützt durch `<AdminRoute>`)
**API:** Nutzt T038b-Endpoints

**Implementiert:** US-15 AC1 + AC2 (Pflichtenheft 5.15)

---

### ✅ T054 · Toast-Notifications

| Aktion | Meldung | Dauer |
|--------|---------|-------|
| Bug erstellt | „Bug erfolgreich erstellt" | 3 Sek |
| Bug gespeichert | „Änderungen gespeichert" | 3 Sek |
| Bug archiviert | „Bug archiviert" | 3 Sek |
| Tag erstellt | „Tag erfolgreich erstellt" | 3 Sek |
| Rolle geändert | „Rolle aktualisiert" | 3 Sek |
| Fehler | „Speichern fehlgeschlagen: [Fehler]" | 5 Sek |

---

### ✅ T055 · Loading-States + Error-Handling
**Skeleton-Loader oder Spinner** während API-Calls · Error-Boundary für unerwartete Fehler · Benutzerfreundliche Meldungen

---

# E6: Erweiterte Features

**Ziel:** Bug-Historie, Suche, optional Kommentare.
**Voraussetzung:** E4 + E5 abgeschlossen
**Deadline:** 20.05.2026

---

### ✅ T056 · V3-Migration: Activity-Tabelle
```sql
CREATE TABLE activities (
    id         BIGSERIAL PRIMARY KEY,
    bug_id     BIGINT NOT NULL REFERENCES bugs(id),
    user_id    BIGINT NOT NULL REFERENCES users(id),
    action     VARCHAR(50) NOT NULL,
    field      VARCHAR(50),
    old_value  TEXT,
    new_value  TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activities_bug_created ON activities(bug_id, created_at DESC);
```

**Status (10.05.2026, PR #14):** Implementiert (Patrick) + Review-Adapt (Maksim).
- `TIMESTAMP WITH TIME ZONE` statt nur `TIMESTAMP` (konsistent mit V1/V2)
- `DEFAULT CURRENT_TIMESTAMP` statt `NOW()` (Stil-Konsistenz mit V1/V2)
- Index `idx_activities_bug_created` ergänzt für T058-Performance (composite passt zu `WHERE bug_id ORDER BY created_at DESC`)
- Branch wurde vor Merge auf aktuellen develop rebased (war von Stand vor T034 abgezweigt)
- T057 jetzt entblockt

---

### ✅ T057 · ActivityDao + Activity-Tracking in BugService
**Was:** Bei jedem Bug-Update automatisch Activity-Einträge erzeugen.

**Erfasste Änderungen:** Status, Titel, Beschreibung, Tag (Priorität + Bearbeiter folgen mit T036b + T037)

**Status (12.05.2026, PR #18):** Implementiert (Maksim, ersetzt Oleksandrs verworfenen Branch).
- `model/Activity.java` Record mit polymorphem action/field/old/new Schema
- `dao/ActivityDao.java`: insert + findByBugId, nutzt Index aus T056
- `service/BugService.java`: createBug logt CREATED, updateBug logt UPDATED pro geändertem Feld, updateStatus logt UPDATED-status
- `@Transactional` auf alle Write-Methoden — atomar Bug + Activity
- 5 ActivityDaoTest + Live-Test gegen Postgres

---

### ✅ T058 · GET /api/bugs/{id}/activities Endpoint
**Was:** Chronologische Liste (neueste oben).

**Status (12.05.2026, PR #18):** Implementiert (Maksim, ersetzt Oleksandrs verworfenen Branch).
- `service/ActivityService.java` mit Existenz-Check auf Bug (404 statt leere Liste)
- `controller/ActivityController.java` mit GET-Endpoint
- `controller/dto/ActivityResponse.java` als API-Form
- Auth: jeder authentifizierte User darf lesen

---

### ✅ T059 · Bug-Historie-Anzeige im Frontend *(US-14)*
**Timeline-Komponente** in Bug-Detail-Seite.

**Anzeige:** Zeitstempel · User · Aktion (z.B. „Status geändert: NEU → IN_BEARBEITUNG")
**Leerer Zustand:** „Bug erstellt von [Reporter]"

---

### ✅ T060 · Suchfeld in Bug-Liste (Frontend + Backend) *(US-10)*
**Frontend:** Suchfeld mit 300ms Debounce · Treffer-Hervorhebung
**Backend:** `GET /api/bugs?search=login` — case-insensitive ILIKE
**Kombinierbar** mit Status/Priority/Tag-Filter

---

### T061 · (KANN) Kommentare-Backend
**Datei:** `V4__comments.sql` · Endpoints: GET/POST `/api/bugs/{id}/comments`

---

### ✅ T062 · (KANN) Kommentare-Frontend
**Plain-Text** · Chronologische Sortierung (älteste oben) · Submit-Button deaktiviert wenn leer

---

# E7: Tests & Quality

**Ziel:** Coverage ≥ 60% in Service-Schicht. Alle wichtigen User Stories durch Tests abgedeckt.
**Deadline:** 20.05.2026

---

### T063 · Unit-Tests für PasswordHasher
Mind. 3 Cases · 100% Coverage für `PasswordHasher`.

---

### T064 · Unit-Tests für AuthService *(US-12, US-13)*
Login mit gültigen/ungültigen Credentials · Registrierung · Doppelter-Username-Check.

---

### ✅ T065 · Unit-Tests für BugService *(US-01 bis US-08)*
Bug-CRUD · **Alle State-Machine-Übergänge (erlaubt + verboten)** · Soft-Delete + Reaktivierung · Bearbeiter-Zuweisung · **Prioritätsänderung** *(neu — FA-07)*

**Status (15.05.2026, PR #27):** Implementiert (Oleksandr) + Review-Adapt (Maksim).
- 14 Mockito-Unit-Tests in neuer Klasse `BugServiceCrudStatusArchiveTest`: `createBug` (defaults Status/Priority), `updateBug` (Reporter + 403 fremder Tester + 409 archived), `updateStatus` (allowed/forbidden Transition via Mock + 403 Tester), `archiveBug` (Happy + 409 doppelt), `restoreBug` (Happy + 409 aktiv).
- **Design**: `BugStatusStateMachine` wird gemockt (`@Mock`), nicht real verwendet — BugService-Tests sind dadurch immun gegen Policy-Änderungen der State-Machine. Bearbeiter-Zuweisung (T037) und Priority (T036b) haben bereits eigene Test-Klassen (`BugServiceAssigneeTest`, `BugServicePriorityTest`).
- **Adapt:** Branch zweigte von `c6e49aa` ab. Git's 3-way-Merge mergte `BugStatusStateMachineTest` automatisch, **erkannte aber keinen semantischen Konflikt**: T065 ergänzte `allTransitionsMatchDocumentedPolicy` mit ALTER strikter Policy (NEU → IN_BEARBEITUNG/ARCHIVIERT only), develop hatte parallel im Sprint1-Merge die State-Machine gelockert. Beide Tests im selben File widersprachen sich (`NEU → IM_REVIEW`: T065 sagt `false`, develop sagt `true`). T065's veralteter Test + `allowedTargets`-Helper gedroppt, develop's neue Tests behalten. T065 reduziert sich netto auf die reine Hinzufügung von `BugServiceCrudStatusArchiveTest`.
- **Tests:** 79/79 Backend-Tests grün (vorher 68, +11 netto).

---

---

### T065b · Unit-Tests für TagService *(neu — FA-16)*
**Was:** CRUD-Tests für Tag-Service.

**Test-Cases:**
- Tag erstellen mit gültigem Hex-Code
- Tag erstellen mit ungültigem Hex-Code → Fehler
- Doppelter Name → Fehler
- Tag löschen, Bug verliert Tag-Zuordnung (`tag_id` auf NULL)

---

### T065c · Unit-Tests für UserService Rollen-Mgmt *(neu — FA-15)*
**Test-Cases:**
- ADMIN ändert Rolle eines anderen Users → erfolgreich
- ADMIN ändert eigene Rolle → Fehler (Lock-out-Schutz)
- DEVELOPER versucht Rollen-Änderung → AccessDeniedException

---

### T066 · Integration-Tests für Auth-Endpoints
End-to-End via MockMvc: Login → /me → Logout · HTTP-Status + Cookie-Handling.

---

### ✅ T067 · Integration-Tests für Bug-Endpoints
**Bug-Lifecycle:** Erstellen → Bearbeiten → Status ändern → Archivieren.
**Deckt UC-01, UC-02, UC-03 ab.**

**Status (15.05.2026, PR #28):** Implementiert (Oleksandr) + Review-Adapt (Maksim).
- `BugLifecycleIntegrationTest` mit einem End-to-End-`@Test` (`fullBugLifecycle_createEditChangeStatusAndArchive`): Login als DEVELOPER → POST `/api/bugs` (priority=HOCH, 2 Tags) → PUT `/api/bugs/{id}` (Titel+Beschreibung+neue Tags) → PATCH `/status` (NEU → IN_BEARBEITUNG) → PATCH `/archive` → Verifikation `archived` hidden by default + sichtbar mit `?archived=true`.
- **Setup**: `@SpringBootTest` + H2 In-Memory (PostgreSQL-Mode), Flyway disabled, Schema via `@BeforeEach` mit raw `CREATE TABLE` (users, tags, bugs, bug_tags, activities); BCrypt-Hash für `admin123` passt zum V4-Seed.
- **Echtes Auth-Flow** mit Cookie-Session, nicht gemockt. Prüft tagIds, reporterName, status-Transitionen, archived-Filter.
- **Adapt:** Branch zweigte von `c6e49aa` ab — Rebase auf `develop` konfliktfrei (einzige neue Datei, keine Überschneidungen). Keine semantische Adaption nötig.
- **Tests:** 80/80 Backend-Tests grün (vorher 79, +1 Lifecycle-Test).
- **Follow-ups offen (außerhalb Scope):** (a) Activity-Log-Verifikation im E2E (`activities`-Tabelle wird angelegt, aber nicht in Assertions geprüft — Coverage liegt bei T065-Unit-Tests); (b) Schema-Drift-Risiko da Test-Schema manuell gepflegt statt via Flyway; (c) Fehlerpfade (403/409/404) sind in Unit-Tests abgedeckt, nicht im E2E.

---

### T067b · Integration-Tests für Rollenkontrolle *(neu — FA-15)*
**Test-Cases:**
- TESTER versucht POST /api/tags → HTTP 403
- DEVELOPER versucht GET /api/users → HTTP 403
- ADMIN kann alle Endpoints aufrufen

---

### ✅ T068 · Frontend Unit-Tests (Vitest + Testing Library)
Mind. 5 Tests: Login-Formular, Bug-Erstellen-Formular, Auth-Context, ProtectedRoute, StatusDropdown.

---

### T069 · JaCoCo Code-Coverage-Report
Maven-Plugin · Report unter `target/site/jacoco/index.html` · Ziel: ≥ 60% Service-Schicht · CI-Artefakt.

---

### T070 · Usability-Test mit 3 Probanden *(NFA-01)*
**Aufgabe:** Ohne Erklärung einen neuen Bug anlegen.
**Messung:** Zeit (Ziel: < 3 Minuten), Beobachtung von Problemen.
**Protokoll:** `/docs/usability/usability_test.md`

---

### T070b · Performance-Test mit JMeter *(neu — NFA-04)*
**Was:** Lasttest mit JMeter gegen `GET /api/bugs` (Bug-Liste laden) und `POST /api/bugs` (Bug anlegen).

**Setup:**
- 100 Beispiel-Bugs in der Datenbank (via Seed-Script)
- JMeter-Testplan: 50 simulierte User, je 10 Requests
- Hardware: Standard-Laptop (16 GB RAM, Intel i5+)

**Ziel-Metrik:** 95% der Requests < 2 Sekunden Roundtrip

**Definition of Done:**
- JMeter-Testplan im Repo unter `/docs/performance/jmeter-testplan.jmx`
- Ergebnis-Report unter `/docs/performance/results.html`
- NFA-04 erfüllt oder Abweichung dokumentiert

---

### T071 · SonarLint-Cleanup
IDE-Warnings prüfen · Alle Critical-Findings beheben · Ziel: keine Critical-Findings.

---

# E8: Deployment

**Ziel:** App online erreichbar für Live-Demo am 22.05.2026.
**Deadline:** 21.05.2026

---

### T072 · Hosting-Plattform wählen und dokumentieren
**Optionen:** Railway · Render · MCI-Server
**Kriterien:** Kosten · Java 21 Support · PostgreSQL Add-on · Static Hosting

---

### T073 · Production-DB-Konfiguration
PostgreSQL auf Hosting-Plattform · Connection-String als Env-Variable · Flyway läuft beim Start.

---

### ✅ T074 · Environment-Variablen-Setup
```
DATABASE_URL=postgresql://...
SPRING_PROFILES_ACTIVE=prod
SESSION_SECRET=...
CORS_ALLOWED_ORIGIN=https://bugtracker.example.com
```
**Regel:** Keine Secrets im Repo · `application-prod.yml` referenziert nur Env-Variablen.

---

### T075 · Frontend-Build + Static-Hosting
`npm run build` → `/dist` · Hosting via Vercel/Netlify/Spring Boot static · **SPA-Fallback:** Direkter URL-Aufruf (`/bugs/42`) muss funktionieren.

---

### T076 · Deployment-Workflow in CI
GitHub Action: Push auf `main` → automatisches Deployment Backend + Frontend · Logs in GitHub Actions.

---

### T077 · Health-Check-Endpoint
`GET /actuator/health` → HTTP 200 · Hosting-Provider nutzt es als Liveness/Readiness-Check.

---

### T078 · Smoke-Test nach Deployment
Manueller Test: Login · Bug anlegen · Status-Wechsel · Tag zuweisen · Admin-Funktionen · Protokoll unter `/docs/deployments/smoke_test_DATUM.md`.

---

# E9: Präsentation & Bericht

**Ziel:** Abschlusspräsentation + Projektbericht.
**Bewertungsanteil:** 40% Präsentation · 30% Bericht
**Deadline:** 22.05.2026 (Präsentation) · 22.05.2026 23:59 (Bericht)

---

### T079 · Präsentations-Slides erstellen
**Umfang:** 10–15 Folien

**Struktur:**
1. Problemstellung (Warum BugTracker?)
2. Personas + Use Cases (Use-Case-Diagramm zeigen)
3. Architektur-Überblick (Architekturdiagramm)
4. Datenmodell (Klassendiagramm)
5. **Anforderungs-Compliance** (FA → Task-Mapping-Tabelle als Beweis)
6. Live-Demo (interaktiver Teil)
7. Tech-Stack + Entscheidungen
8. Lessons Learned
9. Ausblick

**Speicherort:** `/docs/presentation/`

---

### T080 · Demo-Daten vorbereiten
Mind. 5 realistische Beispiel-Bugs in verschiedenen Stati · Reproduzierbar via `seed.sql` oder Java-Seeder.

**Demo zeigt:** Filter, Suche, Historie, Tags, **Rollenunterschiede (Tester vs. Developer vs. Admin)**, Inline-Edit, State-Machine.

**Mind. 1 User pro Rolle für Demo-Login.**

---

### T081 · Probelauf mit Team
Komplette Präsentation inkl. Live-Demo · Ziel: < 20 Minuten · Schwächen dokumentieren und beheben.

---

### T082 · Projektbericht schreiben
**Umfang:** Mind. 10 Seiten

**Inhalt:**
1. Projektverlauf (Zeitleiste, was lief gut/schlecht)
2. Architektur-Entscheidungen mit Begründung
3. **Anforderungs-Compliance** (FA → Task-Mapping aus dieser Datei einbinden)
4. Herausforderungen + Lösungen
5. Lessons Learned
6. Fazit

**Abgabe:** Als PDF im Sakai bis 22.05.2026, 23:59

---

### T083 · Slides finalisieren + Demo-Skript
Letzter Polish · **Demo-Skript:** Klare Schritte, wer was klickt · **Fallback-Plan:** Demo-Video als Backup falls Live-Demo crasht.

---

## Abhängigkeitsübersicht

```
E1 (Setup)
  └── E2 (CI/CD)
        └── E3 (Auth)
              ├── E4 (Bug-Backend + Tags-Backend + Admin-Backend)
              │     └── E6 (Erweiterte Features)
              │           └── E7 (Tests + Performance)
              │                 └── E8 (Deployment)
              │                       └── E9 (Präsentation)
              └── E5 (Frontend + Admin-Pages)
                    └── E6 (Erweiterte Features)
```

---

## Was sich gegenüber v1.0 dieser Datei geändert hat

**Neu hinzugefügte Tasks (9 Stück):**
- T003a Use-Case-Diagramm
- T003b Klassendiagramm
- T003c Sequenzdiagramm
- T009b Anforderungs-Mapping pflegen
- T021b @PreAuthorize-Annotationen
- T036b PATCH /api/bugs/{id}/priority (FA-07)
- T038a Tag-CRUD Backend (FA-16)
- T038b Admin-User-Mgmt Backend (FA-15 AC1+AC2)
- T053a AdminTagsPage Frontend (FA-16)
- T053b AdminUsersPage Frontend (FA-15)
- T065b Tag-Service Tests
- T065c User-Rollen-Tests
- T067b Rollenkontrolle Integration-Tests
- T070b JMeter Performance-Test (NFA-04)

**Korrigiert / präzisiert:**
- T002: v1.0 → v1.1
- T010: Klarstellung "keine spring-session-jdbc"
- T019: spring_session-Tabelle entfernt + CHECK-Constraint auf role
- T021: CSRF-Diskrepanz zum Pflichtenheft NFA-07 dokumentiert
- T028: AccessDeniedException ergänzt
- T029: Standard-Tags als Demo-Daten in Migration eingefügt
- T080: Mind. 1 User pro Rolle erwähnt

**Sprint-Plan synchronisiert** mit Pflichtenheft Abschnitt 13:
- E3 → MVP v0.1 (09.05.2026)
- E4 + E5 → Beta v0.5 (16.05.2026)
- E6 + E7 + E8 → Release v1.0 (22.05.2026)

---

**Letzte Aktualisierung:** 04.05.2026 · Gruppe DE 3 · MCI Innsbruck