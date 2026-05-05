# Beitragen zum BugTracker — CONTRIBUTING.md

Willkommen im Team!
Dieses Dokument beschreibt **wie** wir an diesem Projekt arbeiten — Branching, Commits, Pull Requests und Code-Reviews. Lies es einmal durch, bevor du deinen ersten PR aufmachst.

> **TL;DR**
> 1. Branch von `develop` ziehen: `feature/T0XX-kurzbeschreibung`
> 2. Conventional Commits: `type(scope): kurze beschreibung`
> 3. PR gegen `develop`, mit FA-/Task-ID im Titel
> 4. Mind. 1 Approval + grüner CI-Lauf vor Merge
> 5. Squash & Merge → Branch löschen

---

## Inhaltsverzeichnis

- [Setup](#setup)
- [Branching-Strategie](#branching-strategie)
- [Conventional Commits](#conventional-commits)
- [Pull Requests](#pull-requests)
- [Code-Review-Erwartungen](#code-review-erwartungen)
- [CI & Branch-Protection](#ci--branch-protection)
- [Häufige Probleme](#häufige-probleme)

---

## Setup

Lokales Setup steht in der [README.md](./README.md#lokales-setup). Wenn du das in unter 15 Minuten lokal nicht zum Laufen bekommst, frag — wir verbessern dann die README, statt dass du allein leidest.

---

## Branching-Strategie

### Branch-Hierarchie

```
main           ← nur Releases (v0.1, v0.5, v1.0), geschützt
└── develop    ← laufende Integration, geschützt, nur via PR
    ├── feature/T031-bug-create-endpoint
    ├── feature/T044-login-page
    ├── docs/umsetzungsplan
    └── fix/login-validation-edge-case
```

### Welcher Branch wofür?

| Branch | Zweck | Wer pusht? | Wie kommt Code rein? |
|--------|-------|------------|----------------------|
| `main` | Released Releases (Tag: `v0.1`, `v0.5`, `v1.0`) | niemand direkt | nur via PR von `develop`, am Ende eines Sprints |
| `develop` | Aktueller Integrations-Stand | niemand direkt | nur via PR von Feature-/Fix-/Docs-Branches |
| `feature/T0XX-...` | Eine Task aus `TASKS.md` umsetzen | jeder | per PR auf `develop` mergen, dann löschen |
| `fix/...` | Bugfix außerhalb einer Task (z.B. Hotfix) | jeder | per PR auf `develop` |
| `docs/...` | Reine Doku-Änderungen ohne Task-Bezug | jeder | per PR auf `develop` |

### Branch-Naming-Konvention

```
feature/T013-ci-backend
feature/T044-login-page
fix/cors-preflight-headers
docs/umsetzungsplan
```

**Regeln:**
- **Klein-Buchstaben** + Bindestriche, keine Leerzeichen, keine Umlaute
- **Mit Task-ID starten** wenn aus TASKS.md (`feature/T031-...`)
- **Kurz und beschreibend** — Branch-Name darf den Inhalt vermitteln, ohne dass man den PR öffnen muss
- **Nicht länger als ~50 Zeichen** — sonst werden Konsolen-Outputs unleserlich

### Branch erstellen — Standard-Workflow

```bash
# Auf develop wechseln und neuesten Stand holen
git switch develop
git pull --prune

# Neuen Feature-Branch ziehen
git switch -c feature/T0XX-kurzbeschreibung

# Code schreiben, committen
git add <dateien>
git commit -m "feat(scope): beschreibung (T0XX)"

# Pushen + PR aufmachen
git push -u origin feature/T0XX-kurzbeschreibung
```

### Branch aktuell halten

Wenn dein Branch älter wird (mehrere Tage), könnte sich `develop` weiterbewegt haben. Vor dem PR-Push:

```bash
git switch develop
git pull --prune
git switch feature/T0XX-...
git rebase develop
# Bei Konflikten: lösen, git add <datei>, git rebase --continue
git push --force-with-lease     # nach Rebase nötig
```

> **Warum `--force-with-lease` und nicht `--force`?**
> `--force-with-lease` lehnt den Push ab, falls jemand anders zwischenzeitlich auf deinen Branch gepusht hat. Sicherheitsnetz gegen versehentliches Überschreiben fremder Arbeit.

---

## Conventional Commits

Alle Commit-Messages folgen [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): kurze beschreibung im imperativ

optional: längere beschreibung in punkten
- punkt 1
- punkt 2

Refs: T0XX, FA-YY
```

### Erlaubte Types

| Type | Wann verwenden | Beispiel |
|------|----------------|----------|
| `feat` | Neue Funktionalität | `feat(auth): add login endpoint (T023)` |
| `fix` | Bugfix | `fix(bugs): handle null assignee on update` |
| `refactor` | Code-Umbau ohne Verhaltensänderung | `refactor(service): extract status validator` |
| `docs` | Nur Doku-Änderungen | `docs: update README setup guide (T015)` |
| `test` | Tests hinzufügen / anpassen | `test(bug): add state-machine cases (T065)` |
| `chore` | Wartung, Dependencies, Configs | `chore: bump Spring Boot to 3.3.5` |
| `ci` | CI-/Workflow-Änderungen | `ci(backend): add GitHub Actions pipeline (T013)` |
| `style` | Formatierung, Whitespace, kein Code-Effekt | `style: fix indentation in BugController` |
| `perf` | Performance-Verbesserung | `perf(bugs): cache assignee lookup` |

### Scope (in Klammern)

Optional, aber sehr empfohlen. Macht klar, **welcher Bereich** betroffen ist:

| Scope | Was |
|-------|-----|
| `auth` | Login, Logout, Register, Sessions |
| `bugs` | Bug-CRUD-Logik |
| `tags` | Tag-Verwaltung |
| `users` | User-Verwaltung, Rollen |
| `activities` | Bug-Historie |
| `comments` | Kommentar-Funktion |
| `backend` | Allgemein Backend, kein spezifischer Bereich |
| `frontend` | Allgemein Frontend |
| `ci` | GitHub-Actions / Build-Pipeline |
| `docs` | Dokumentation |

### Title-Regeln

- **Imperativ:** „add login" nicht „added login" oder „adds login"
- **Klein anfangen:** `feat: add login` nicht `feat: Add login`
- **Kein Punkt am Ende:** ✗ `feat: add login.` → ✓ `feat: add login`
- **Max. 72 Zeichen** — sonst wird auf GitLab/GitHub abgeschnitten
- **Task-ID am Ende:** `(T023)` — macht Bewertung & Tracing einfacher

### Body (optional, aber willkommen bei größeren Änderungen)

Wenn der Title nicht ausreicht, schreibe einen Body. Trenne mit Leerzeile:

```
feat(bugs): add state-machine validator (T036)

- Implement StatusTransitionValidator service
- Allowed transitions defined in TASKS.md
- Throws InvalidStatusTransitionException on illegal transitions
- Wired into PATCH /api/bugs/{id}/status

Refs: T036, FA-06
```

### Beispiele aus diesem Projekt

```
✓ feat(auth): add register endpoint with validation (T025)
✓ fix(bugs): allow null assignee on creation
✓ docs: comprehensive README setup guide (T015)
✓ ci(backend): add GitHub Actions CI pipeline (T013)
✓ refactor(service): extract password hashing helper (T027)
✓ test(auth): add login integration tests (T066)

✗ "Updated stuff"                     — kein Type, vage
✗ "fix bug"                           — vage, kein Scope
✗ "Feat: Added Login Endpoint."       — Großschreibung + Punkt
✗ "feature/T023-login complete"       — Branch-Name als Message
```

---

## Pull Requests

### Wann PR aufmachen?

- **Sobald** der Feature-Branch einen lauffähigen Stand hat — auch wenn noch Polish fehlt: dann als „Draft" / WIP markieren
- Lieber **früher** PR aufmachen und Feedback einholen als drei Tage allein bauen

### PR-Titel

Gleich wie Commit-Title (Conventional Commits Format):

```
feat(auth): add login endpoint (T023)
```

### PR-Beschreibung — Template

Bitte folgende Struktur in der PR-Beschreibung verwenden (kopiere und fülle aus):

```markdown
## Was ändert sich?

<!-- 1-3 Sätze: was macht dieser PR? -->

## Bezug zu TASKS.md / Pflichtenheft

- Task: T0XX (siehe TASKS.md)
- FA-IDs: FA-XX, FA-YY (falls relevant)
- Implementiert: US-XX AC1, AC2 (User-Story-Akzeptanzkriterien)

## Was wurde gemacht?

<!-- Bullet-Liste der wichtigsten Änderungen -->
- ...
- ...

## Tests

<!-- Was wurde getestet? Welche Test-Klassen wurden hinzugefügt? -->
- [ ] Unit-Tests für ...
- [ ] Integration-Tests für ...
- [ ] Manuell verifiziert: <Schritte>

## Screenshots / Demo

<!-- Bei Frontend-Änderungen: Vorher/Nachher-Screenshots oder kurzes GIF -->

## Reviewer-Hinweise

<!-- Was sollte der Reviewer besonders prüfen? Wo bist du unsicher? -->
- ...

## Checkliste

- [ ] Branch ist auf neuestem `develop` rebased
- [ ] Conventional Commit Messages
- [ ] Tests grün lokal (`mvn verify` / `npm test`)
- [ ] CI grün
- [ ] TASKS.md aktualisiert (✅ markiert) — falls Task abgeschlossen
- [ ] Erklärungs-Datei in `.erklaerungen/` aktualisiert (falls relevant)
```

### PR-Größe

**Faustregel:** ein PR = eine Task aus TASKS.md.

- **Klein bevorzugt** — kleinere PRs werden schneller reviewt und gemerged
- **Über 500 Zeilen Diff** → fragen, ob man splitten kann
- **Zwei unabhängige Themen** → zwei PRs

### Merge-Strategie

Auf Gitlab beim Merge **„Zusammenfassen und mergen"** (Squash and Merge) wählen.

| Strategie | Wann |
|-----------|------|
| Squash & Merge (`Zusammenfassen und mergen`) | **Standard für Feature-PRs auf develop** |
| Plain Merge (`PR zusammenführen`) | Beim Release-Merge `develop → main` (1× pro Release) |
| Rebase & Merge | Nicht verwenden — splittet Tasks in mehrere develop-Commits |

**Begründung Squash:** Pro Task = 1 Commit auf develop. Saubere History für Bewertung & Bericht.

### Nach dem Merge

```bash
git switch develop
git pull --prune
git branch -d feature/T0XX-...        # lokale Branch löschen
# falls -d nicht klappt (Squash-Merge ändert die Hashes):
git branch -D feature/T0XX-...
```

Der Remote-Branch wird von Gitlab automatisch gelöscht (Häkchen im Merge-Dialog).

---

## Code-Review-Erwartungen

### Pflicht-Reviews

- **Mindestens 1 Approval** vor Merge
- Für **größere/risikoreiche** Änderungen (Security, State-Machine, Schema-Migration) **2 Approvals** anstreben
- Reviewer **nicht** = PR-Autor

### Was prüft ein Reviewer?

In dieser Reihenfolge:

1. **Funktioniert es?** Erfüllt der Code die DoD aus TASKS.md? Sind die Akzeptanzkriterien der User Story abgedeckt?
2. **Ist es getestet?** Mindestens Happy-Path + 1–2 Edge-Cases. Bei Service-Schicht: ≥60% Coverage anstreben (NFA-05)
3. **Ist es lesbar?** Klare Methoden-/Variablennamen, keine Magic Numbers, sinnvolle Struktur
4. **Folgt es CONTEXT.md?** Tech-Stack-Vorgaben (z.B. JdbcTemplate statt JPA), Package-Struktur, Naming
5. **Sicherheit:** keine Klartext-Passwörter, keine SQL-Injection, korrekte `@PreAuthorize`-Annotation, sensible Daten nicht im Log
6. **Performance:** keine N+1-Queries in Listen-Endpoints, keine Endlos-Schleifen

### Wie reviewt man?

- **Auf GitLab:** Tab „Changes" → „Files" → an einzelnen Zeilen kommentieren
- **Konstruktiv:** Begründe das Warum, nicht nur das Was. „Hier könnten wir X benutzen, weil Y" statt „Mach X"
- **Frage statt Befehl** wenn unklar: „Warum hier nicht ein Stream?"
- **Kein Bikeshedding:** keine Diskussionen über Stilfragen, die `prettier`/`SonarLint` automatisch lösen können

### Wie reagiert man auf Reviews?

- **Antworten** auf jeden Kommentar — entweder mit Code-Änderung oder mit Begründung „warum so"
- **Nicht persönlich nehmen** — Review betrifft den Code, nicht den Autor
- **Bei Disagreement:** kurzer Austausch im PR, wenn nicht aufzulösen → gemeinsam in der Standup besprechen
- **„Resolve"** klicken, wenn Kommentar abgehandelt — nicht warten bis Reviewer das macht

### SLA für Reviews

- **Innerhalb 24 h** nach PR-Open zumindest ein erstes Feedback (auch nur „komme heute Abend dran" ist OK)
- **Innerhalb 48 h** vollständig reviewt — sonst gerät der Plan in Verzug
- **In der heißen Phase** (vor MVP/Beta-Deadlines) → Reviews priorisieren über eigene Tasks

---

## CI & Branch-Protection

### Was läuft auf jedem PR?

- **Backend-CI** (`.github/workflows/ci-backend.yml`): `mvn -B clean verify` mit PostgreSQL-Service-Container — siehe T013
- **Frontend-CI** (`.github/workflows/ci-frontend.yml`, geplant T014): ESLint + TypeScript-Check + Vite-Build

### Was muss grün sein vor dem Merge?

- **Build erfolgreich**
- **Alle Tests bestanden**
- **Mindestens 1 Approval**

> **⚠️ Bekannter offener Punkt (Stand 05.05.2026):**
> Die CI-Pipeline liegt als GitHub-Actions-Workflow im Repo, der `origin` ist aber MCI-Gitea. GitHub-Actions führt nur auf GitHub aus — d.h. solange kein GitHub-Mirror eingerichtet ist, läuft die CI nicht. Branch-Protection mit „CI grün" als Bedingung greift entsprechend nicht. Lösung steht in `TASKS.md` / `docs/Umsetzungsplan.md` → Risiko-Sektion.

### Wenn die CI rot ist

1. **Logs lesen** auf der Actions-Seite (oder Gitea-CI-View, sobald aktiv)
2. **Surefire-Report** als Artefakt herunterladen — dort steht, welcher Test wieso failed
3. **Lokal reproduzieren:** `cd backend && mvn clean verify`
4. **Fixen + neu pushen** → CI läuft automatisch neu

---

## Häufige Probleme

### Branch lässt sich nicht mergen — Konflikte

```bash
git switch feature/...
git pull origin develop --rebase
# Konflikte werden in den betroffenen Dateien markiert (<<<<<<<, =======, >>>>>>>)
# Konflikte lösen, dann:
git add <konfliktdatei>
git rebase --continue
git push --force-with-lease
```

### Versehentlich auf `main` oder `develop` committet

```bash
# 1. Commit-Hash merken
git log -1 --oneline

# 2. Branch zurücksetzen
git reset --hard origin/develop          # oder origin/main

# 3. Cherry-pick auf neuen Branch
git switch -c feature/Txx-was-auch-immer
git cherry-pick <commit-hash>

# 4. Pushen
git push -u origin feature/Txx-was-auch-immer
```

### Force-push hat fremde Commits überschrieben

Wenn du `--force` (ohne `-with-lease`) genutzt hast — sofort melden! `git reflog` zeigt verlorene Commits, die wir wiederherstellen können. Nicht weiterarbeiten, bis das geklärt ist.

### Stash-Inhalte sind weg

```bash
git stash list           # alle Stashes anzeigen
git stash show -p stash@{N}    # Inhalt eines Stashes anzeigen
git stash pop stash@{N}        # zurückholen
```

### CI auf Push schlägt fehl, lokal grün

- **Java-Version:** lokal vielleicht 17, CI nutzt 21 — Fehler werden nur in 21 sichtbar
- **DB-State:** lokale DB hat alte Daten, CI startet sie frisch — Migrations-Reihenfolge prüfen
- **Locale/Timezone:** CI läuft UTC, lokal vielleicht CET — Tests mit Zeit-/Datum-Logik anfällig

---

## Weitere Doku

- **[README.md](./README.md)** — Setup-Anleitung & Projektübersicht
- **[TASKS.md](./TASKS.md)** — vollständige Aufgabenliste (92 Tasks)
- **[docs/Umsetzungsplan.md](./docs/Umsetzungsplan.md)** — Tag-für-Tag-Plan & Reihenfolge-Begründungen
- **CONTEXT.md** — Tech-Stack-Vereinbarungen (verbindlich)

---

**Fragen?** Standup-Diskussion oder im Team-Discord. Lieber nachfragen als raten.
