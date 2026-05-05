# Umsetzungsplan — BugTracker

**Stand:** 05.05.2026
**Zweck:** Optimale Reihenfolge der noch offenen Tasks aus `TASKS.md`, mit Begründung warum genau **diese** Reihenfolge. Soll Diskussionen im Team reduzieren, Engpässe vermeiden und sicherstellen, dass alle 5 Personen parallel ausgelastet sind.

> Dieses Dokument ist **lebend** — bei größeren Änderungen am Plan einfach updaten.

---

## TL;DR

Drei parallele Tracks ab Tag 2:

| Track | Personen | Fokus |
|---|---|---|
| **Backend** | 1–2 Personen | E3 Auth → E4 Bugs → E6 Activities |
| **Frontend** | 1–2 Personen | E5 Foundation → Pages mit MSW-Mocks → echte API |
| **DevOps & Specs** | 1 Person | T004 OpenAPI → T012 Docker → T014 CI Frontend → E8 Deployment |

**Pflichtreihenfolgen** (nicht verhandelbar):
1. **T004 (OpenAPI) vor allem Frontend-API-Code** — sonst weichen Frontend-Mocks vom Backend ab
2. **T021 (SecurityConfig) vor T023–T026 (Auth-Endpoints)** — Auth-Logik braucht Security-Kontext
3. **T029 (V2-Migration) vor T030 (BugDao)** — DAO braucht Tabelle
4. **T039–T041 (Frontend-Setup) vor allem Frontend-Code** — trivial
5. **T056–T058 (Activity-Backend) vor T059 (Timeline-UI)** — UI braucht Daten

---

## Ausgangslage (05.05.2026)

| Epic | Status | Offene Tasks |
|------|--------|--------------|
| E1 Spezifikation & Setup | 90% ✅ | **T004 OpenAPI** |
| E2 DevOps & CI | 70% ✅ | T012 Dockerfile · T014 CI Frontend · T017 CONTRIBUTING |
| E3 Auth-System | 0% | T018–T028 |
| E4 Bug-Verwaltung | 0% | T029–T038b |
| E5 Frontend Setup & Pages | 0% | T039–T055 |
| E6 Erweiterte Features | 0% | T056–T062 |
| E7 Tests & Quality | 0% | T063–T071 |
| E8 Deployment | 0% | T072–T078 |
| E9 Präsentation & Bericht | 0% | T079–T083 |

**Pflichtenheft-Releases** (Deadlines hart):
- **MVP v0.1** → 09.05.2026 (lauffähiges Auth-System mit DB)
- **Beta v0.5** → 16.05.2026 (Bug-CRUD + Frontend-Pages)
- **Release v1.0** → 22.05.2026 (Activities, Suche, Deployment)
- **Live-Demo & Bericht** → 22.05.2026

Effektive Arbeitszeit bis Demo: **17 Tage** (inkl. Wochenenden).

---

## Strategie-Überblick

### Warum drei parallele Tracks?

5 Personen sequentiell auf einem Track → 4 warten, 1 arbeitet. **Verboten.**
Drei Tracks → alle ausgelastet, weniger Mergekonflikte (verschiedene Code-Bereiche).

### Warum Frontend mit Mocks vorziehen?

Wenn Frontend auf Backend wartet, fängt es erst nach E3 (~5 Tage) an. Mit MSW (Mock Service Worker) gegen die OpenAPI-Spec kann das Frontend **sofort nach T004** starten und ist bei E3-Abschluss schon weit.

→ **OpenAPI-Spec ist der wichtigste Hebel im ganzen Plan.** Deshalb wird T004 vor alle anderen offenen Tasks gezogen.

### Warum E2 zuerst sauber abschließen?

T012 (Dockerfile) und T017 (CONTRIBUTING) blocken niemanden technisch, aber:
- **T017** legt PR-Template + Branching fest → spart 5× Diskussion in der Woche
- **T012** wird für E8 Deployment gebraucht — billiger jetzt machen als später unter Zeitdruck

### Warum nicht alles auf einmal starten?

Mergekonflikte. Wenn Person A am `BugController` und Person B am `BugService` arbeitet, kollidieren sie nicht. Wenn beide am `BugController` sind → Stress. Daher saubere Aufgaben-Trennung pro Track.

---

## Phasenplan mit Begründungen

### Phase 0 — Heute (05.05) Abend

| Task | Wer | Warum jetzt |
|---|---|---|
| **T017 CONTRIBUTING.md** | irgendwer (klein) | Spielregeln vor Team-Skalierung. Verhindert „macht jeder anders"-Chaos in den nächsten Branches. |
| **T004 OpenAPI-Spec** | Backend-Lead | Härtester Hebel: schaltet Frontend-Track frei. Muss VOR Phase 2 stehen, sonst rennt Frontend ins Leere. |

**Reihenfolge-Begründung:** Beide Tasks sind Vorarbeiten — T017 für Prozess, T004 für Architektur-Contract. Ohne sie wird's später schmerzhaft.

---

### Phase 1 — Tag 2 (06.05) — E2 abschließen + Setups starten

| Task | Wer | Warum jetzt |
|---|---|---|
| **T012 Dockerfile Backend** | DevOps | E2-Deadline ist heute. Außerdem braucht E8 (Deployment) das Image. |
| **T018 Spring Boot Projekt-Init** | Backend P1 | E3 startet — Package-Struktur ist Voraussetzung für alles weitere. |
| **T019 Flyway V1 Migration (User)** | Backend P1 | Tabelle muss vor DAO da sein. |
| **T039 React + Vite + TS Setup** | Frontend P3 | Reine Frontend-Foundation, keine Abhängigkeit. Sofort möglich. |
| **T040 Tailwind Setup** | Frontend P3 | Direkt nach T039. |

**Reihenfolge-Begründung Backend:** T018→T019 ist eine harte Kette (Init → Migration). T020 (DAO) folgt erst, wenn die Tabelle existiert.

**Reihenfolge-Begründung Frontend:** T039 + T040 sind 30-Min-Tasks und blocken alles andere im Frontend. Ein Mensch kann sie nacheinander machen.

---

### Phase 2 — Tag 3–4 (07.–08.05) — Auth-Sprint + Frontend-Foundation

#### Backend (Person 1, sequentiell)

| Reihenfolge | Task | Warum |
|---|---|---|
| 1 | **T020** User-Model + DAO | Datenzugriff vor Service-Logik |
| 2 | **T027** PasswordHasher (BCrypt) | Wird in T025 (Register) gebraucht. Klein, daher früh erledigen. |
| 3 | **T021** Spring Security Config | Konfiguriert Auth-Pipeline — muss stehen bevor Endpoints funktionieren |
| 4 | **T021b** @PreAuthorize | Annotationen werden direkt mit Endpoints verteilt — daher hier mit T021 |
| 5 | **T022** CORS-Config | Frontend wird in Phase 3 echte Calls machen — CORS muss stehen |
| 6 | **T028** GlobalExceptionHandler | Endpoints liefern strukturierte Fehler — wird sofort beim ersten Endpoint gebraucht |
| 7 | **T023–T026** Login/Logout/Register/Me | Reihenfolge egal, alle 4 zusammen — gemeinsame Test-Session |

**Begründung Reihenfolge:** Infrastruktur (Security, CORS, Exceptions) **vor** Endpoints. Wenn man Endpoints zuerst baut und dann Security drüberstülpt, sind die Tests doppelt aufwändig.

#### Frontend (Person 3 + 4, parallel)

| Reihenfolge | Task | Warum |
|---|---|---|
| 1 | **T041** React Router | Routes-Skelett mit Platzhalter-Pages — entkoppelt alle weiteren Pages |
| 2 | **T042** API-Client + Hooks (mit MSW) | Kann gegen OpenAPI-Spec gebaut werden, ohne Backend |
| 3 | **T052** Layout + Navigation | Reines UI, keine Daten — kann auch Person 3 parallel zu Router machen |
| 4 | **T054** Toast-System | Library einrichten — UI-Pattern |
| 5 | **T055** Loading/Error-Patterns | Skelett-Komponenten + Error-Boundary |

**Begründung Aufteilung:** P3 macht „Skelett-Tasks" (Router, Layout, Toasts), P4 macht „API-Tasks" (Client, Hooks). So kollidieren sie nicht in den selben Dateien.

#### DevOps (Person 5)

- **T014 CI Frontend** sobald T039 gemerged ist (eslint/typecheck/build)
- Falls Zeit: GitLab-CI parallel zur GitHub-Actions-Pipeline aufbauen (siehe T013-Risiko unten)

---

### Phase 3 — Tag 5 (09.05) — MVP v0.1

| Task | Wer | Warum |
|---|---|---|
| **T044** Login-UI mit echtem Backend | Frontend P4 | Backend Auth ist da — MSW kann gegen echte Calls getauscht werden |
| **T045** Register-UI mit echtem Backend | Frontend P4 | dito |
| **T043** Auth-Context (echte Session) | Frontend P3 | Braucht echtes /me, war vorher gemockt |
| **T066** Auth-Endpoint Integration-Tests | Backend P1 | Test-Suite sichert MVP-Stand |
| **MVP-Smoke-Test** | alle | Login → /me → Logout End-to-End |

**Reihenfolge-Begründung:** UI-Komponenten waren in Phase 2 mit MSW vorgebaut → Phase 3 ist nur „Mock gegen Real austauschen". Schneller als komplette Neuentwicklung jetzt.

**MVP-Definition:** lauffähiges Auth-System mit DB-Anbindung. **NICHT** in MVP: Bug-CRUD (kommt in Beta).

---

### Phase 4 — Tag 6–8 (10.–12.05) — Bug-Backend & Bug-Frontend parallel

#### Backend (Person 1 + 2 — jetzt zwei!)

P1 weiter auf Service-/Controller-Tiefe:

| Reihenfolge | Task | Warum |
|---|---|---|
| 1 | **T029** V2-Migration (bugs + tags) | Tabellen-Voraussetzung |
| 2 | **T030** Bug-Model + DAO | Datenzugriff |
| 3 | **T038** Bean-Validation | Wird in jedem POST/PUT gebraucht — früh |
| 4 | **T031** POST /api/bugs | Erstes CRUD — entsperrt Frontend |
| 5 | **T032** GET /api/bugs (mit Filter-Params) | Liste — entsperrt T046 |
| 6 | **T033** GET /api/bugs/{id} | Detail — entsperrt T047 |
| 7 | **T034** PUT /api/bugs/{id} | Edit |
| 8 | **T035** Soft-Delete + Restore | Archivierung |
| 9 | **T036** PATCH status (State-Machine) | komplex, eigener Validator |
| 10 | **T036b** PATCH priority | trivial nach T036 |
| 11 | **T037** PATCH assignee | trivial |

P2 parallel auf Tags + Admin:

| Reihenfolge | Task | Warum |
|---|---|---|
| 1 | **T038a** Tag-CRUD | unabhängiger Endpoint-Block, kollidiert nicht mit P1 |
| 2 | **T038b** Admin User-Mgmt | dito |

**Begründung Aufteilung:** P1 arbeitet im `bugs/`-Package, P2 in `tags/` + `users/`. Verschiedene Pakete = keine Mergekonflikte.

#### Frontend (Person 3 + 4 parallel)

P3 (Listen + Forms):

| Task | Warum hier |
|---|---|
| **T046** Bug-Liste | sobald T032 da ist |
| **T048** Bug-Erstellen-Form | sobald T031 da ist |
| **T049** Bug-Bearbeiten-Form | sobald T034 da ist |
| **T053** Filter-UI | nach T032 |
| **T051** Archivieren-Button | nach T035 |

P4 (Detail + Inline-Edits):

| Task | Warum hier |
|---|---|
| **T047** Bug-Detail | sobald T033 da ist |
| **T050** Inline-Edits | nach T036, T036b, T037 |
| **T053a** AdminTagsPage | nach T038a |
| **T053b** AdminUsersPage | nach T038b |

**Begründung Aufteilung:** P3 baut Listen-/Formulare-Komponenten, P4 baut Detail-/Inline-Edit. Kollidieren nicht.

---

### Phase 5 — Tag 9–10 (13.–14.05) — Tests + Erweiterte Features

| Task | Wer | Warum |
|---|---|---|
| **T065** BugService-Tests | Backend P1 | Unit-Tests für komplette State-Machine + CRUD |
| **T065b** TagService-Tests | Backend P2 | Unabhängig |
| **T065c** UserService Rollen-Tests | Backend P2 | Lock-out-Schutz testen |
| **T067** Bug-Endpoint Integration-Tests | Backend P1 | E2E via MockMvc |
| **T067b** Rollenkontrolle Integration-Tests | Backend P1 | FA-15 Compliance-Beweis |
| **T056** V3-Migration (Activities) | Backend P2 | Tabelle für Historie |
| **T057** ActivityDao + Tracking | Backend P2 | Hook in BugService — touched code von P1, daher Sequenz mit P1 abstimmen! |
| **T058** GET /api/bugs/{id}/activities | Backend P2 | Endpoint |

**Begründung Tests-Timing:** Tests werden **nach** Endpoint-Implementierung geschrieben (nicht TDD-streng), weil das Pflichtenheft 60% Coverage in Service-Schicht verlangt — nicht 100% TDD. Realistisch für Studi-Sprint.

**Begründung Activity-Timing:** Activities sind nicht im MVP/Beta-kritischen Pfad — können nach den Bug-Endpoints kommen, ohne Risiko.

---

### Phase 6 — Tag 11 (15.05) — Beta-Polish

| Task | Wer | Warum |
|---|---|---|
| **T059** Activity-Timeline-UI | Frontend P4 | nach T058 |
| **T060** Suchfeld (FE + BE) | Frontend P3 + Backend P2 | klein, FA-10 (Kann-Anforderung) |
| **T068** Frontend Unit-Tests | Frontend P3 | Vitest setup + 5 Tests = ~3 Stunden |
| **T069** JaCoCo Coverage-Report | Backend P1 | Maven-Plugin + Report-Check |
| **T071** SonarLint Cleanup | alle Backend | jeder cleant seinen eigenen Code |

---

### Phase 7 — Tag 12 (16.05) — Beta v0.5 + Smoke-Test

- Komplettes Beta-Smoke-Testing aller User-Stories
- Bug-Fix-Tag — keine neuen Features
- T070 Usability-Test mit 3 Probanden (kann auch früher starten, sobald Frontend-Pages stabil)

**Beta-Definition:** Alle Muss + Soll-Anforderungen funktionsfähig. Kann + Optional (Comments T061/T062) müssen nicht da sein.

---

### Phase 8 — Tag 13–15 (17.–19.05) — Release-Vorbereitung & Deployment

DevOps (P5) zieht durch:

| Reihenfolge | Task | Warum |
|---|---|---|
| 1 | **T072** Hosting-Plattform wählen | Entscheidung treffen — Railway/Render/MCI |
| 2 | **T073** Production-DB | DB muss vor App stehen |
| 3 | **T074** Env-Variablen | Konfiguration vor Deployment |
| 4 | **T075** Frontend Build + Static Hosting | parallel zu Backend-Deploy |
| 5 | **T076** Deployment-Workflow CI | Automatisierung erst wenn manuelles Deploy klappt |
| 6 | **T077** Health-Check | falls noch nicht — Provider braucht ihn |
| 7 | **T078** Smoke-Test auf Prod | abschließende Validierung |

Parallel:

- **T070b JMeter Performance-Test** (P5 oder Backend-Lead) — NFA-04 Beweis
- **T080** Demo-Daten-Skript — mind. 5 realistische Bugs in verschiedenen Stati
- **T079** Slides erste Draft

**Begründung Deployment-Reihenfolge:** **Manuelles Deployment vor Automatisierung.** Sonst debuggt man Provider-Konfiguration UND CI-Pipeline gleichzeitig — extrem zäh. Erst klappt's mit Hand, dann automatisieren.

---

### Phase 9 — Tag 16–17 (20.–21.05) — Präsentation polishen

| Task | Wer | Warum |
|---|---|---|
| **T081** Probelauf mit Team | alle | Schwächen finden — < 20 Min Live-Demo |
| **T079** Slides finalisieren | 1–2 Personen | basierend auf Probelauf-Feedback |
| **T082** Projektbericht (≥ 10 Seiten) | 2–3 Personen | parallel zu Slides |
| **T083** Demo-Skript + Backup-Video | 1 Person | Fallback falls Live-Demo crasht |

**Begründung Probelauf vor Slides-Finale:** Im Probelauf merkt man, welche Slides langweilen oder fehlen. Erst danach finale Polish.

---

### Phase 10 — Tag 17 (22.05) — Demo & Abgabe

- **Live-Demo** vormittags
- **Bericht** als PDF in Sakai bis 23:59

---

## Kritische Pfade

```
T004 OpenAPI ──┬─→ Frontend-Track komplett
               └─→ Backend-Endpoint-Contracts

T021 Security ──→ T023–T026 Auth ──→ T043 Auth-Context ──→ MVP

T029 V2-Migration ──→ T030 BugDao ──→ T031–T037 Bug-Endpoints ──→ Beta

T072 Hosting ──→ T073 DB ──→ T074 ENV ──→ T076 CI-Deploy ──→ Live-Demo
```

Wer einen kritischen Pfad blockiert, muss Hilfe anfordern. **Nicht-kritische Pfade (Tests, Comments, JMeter) sind Buffer.**

---

## Team-Aufteilung (Vorschlag)

| Person | Rolle | Hauptverantwortung |
|---|---|---|
| **P1** | Backend Lead | E3 Auth, BugController-Pfad, State-Machine, Service-Tests |
| **P2** | Backend (Bugs + Tags) | Tags-CRUD, Admin-User-Mgmt, Activities, V2/V3-Migrationen |
| **P3** | Frontend (Skelett + Listen) | Setup, Router, Layout, Bug-Liste, Forms, Filter, Tests |
| **P4** | Frontend (Daten + Detail) | API-Client, Auth-Context, Bug-Detail, Inline-Edits, Admin-Pages |
| **P5** | DevOps + Specs | T004 OpenAPI, Docker, CI Frontend, Deployment, Performance-Test |

**Wichtig:** Pair-Programming gegen Ende ist OK — wenn jemand fertig ist, hilft auf kritischem Pfad aus. Nicht starr halten.

---

## Risiken & Gegenmaßnahmen

| Risiko | Wahrscheinlichkeit | Gegenmaßnahme |
|---|---|---|
| **GitHub Actions vs. GitLab-Mismatch** (T013) | hoch | Team-Entscheidung: GitHub-Mirror einrichten ODER `.gitlab-ci.yml` parallel schreiben. Spätestens Phase 2 klären. |
| **OpenAPI-Spec ändert sich nachträglich** | mittel | Spec als Single-Source-of-Truth committen, Änderungen NUR via PR mit Frontend-Tester-Approval |
| **Mergekonflikte auf TASKS.md** | mittel | Status-Updates klein halten, gerne in Sammel-Commits am Phasen-Ende |
| **Frontend-Mocks weichen vom echten Backend ab** | mittel | Mocks aus OpenAPI-Spec generieren (msw-auto-mock) statt von Hand |
| **Jemand fällt aus / krank** | mittel | Pair-Wissensaustausch jeden 2. Tag, kritischer Pfad immer doppelt besetzbar |
| **Pflichtenheft-Anforderung übersehen** | gering | FA-Mapping-Tabelle in TASKS.md regelmäßig (T009b) prüfen, im PR die FA-ID nennen |
| **Live-Demo crasht** | mittel | T083 Backup-Video als Fallback; Demo-User pro Rolle vorbereitet (T080) |
| **Deployment-Setup zieht sich** | hoch | T072 (Plattform-Wahl) **früher** treffen — am besten in Phase 5, nicht erst Phase 8 |

---

## Quick-Reference (Wandposter-Version)

```
HEUTE      → T017, T004
06.05      → T012 + T018, T019, T039, T040
07-08.05   → Auth-Sprint + Frontend-Foundation
09.05 MVP  → Auth läuft, Frontend gegen echte API
10-12.05   → Bug-CRUD Backend + Frontend parallel
13-14.05   → Tests + Activities
15.05      → Beta-Polish
16.05 BETA → Smoke-Test
17-19.05   → Deployment + Slides-Draft
20-21.05   → Probelauf + Bericht + Slides-Final
22.05 DEMO → Live-Präsentation + Bericht-Abgabe
```

---

## Was nicht in diesem Plan steht

- **Tägliche Standups** (15 Min, am besten 09:00 oder 18:00) — koordiniert kurzfristig
- **Code-Review-Erwartungen** — kommt in T017 CONTRIBUTING.md
- **Konkrete Branching-Konventionen** — kommt ebenfalls in T017

---

**Plan-Owner:** Maksym (jetzt) — bei Änderungen Plan updaten und Team informieren.
**Nächste Review-Sitzung:** Spätestens vor Phase 4 (10.05.) prüfen, ob Tempo passt.
