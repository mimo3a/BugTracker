# BugTracker — Frontend

React 19 + TypeScript + Vite + Tailwind v3. Spricht das Spring-Boot-Backend
unter `/api` an (siehe `../docs/api/openapi.yaml`).

## Voraussetzungen

- Node.js ≥ 20
- npm ≥ 10
- Backend lokal auf `http://localhost:8080` (optional — siehe Mock-Fallback unten)

## Setup

```bash
npm install
```

## Scripts

| Befehl              | Zweck                                                    |
|---------------------|----------------------------------------------------------|
| `npm run dev`       | Vite dev-server auf `http://localhost:5173`              |
| `npm run build`     | Production-Build (`tsc -b && vite build`) nach `dist/`   |
| `npm run preview`   | Build lokal vorschauen                                   |
| `npm run typecheck` | Nur TypeScript prüfen, kein Output                       |
| `npm run lint`      | ESLint flat config                                       |
| `npm run format`    | Prettier `--write`                                       |
| `npm run format:check` | Prettier `--check` (CI)                               |

## API-Proxy

Vite proxiert `/api/*` auf `http://localhost:8080` (siehe `vite.config.ts`).
Im Browser sprichst du also einfach `/api/bugs` an — kein CORS-Konfig nötig.

## Mock-Fallback

Wenn das Backend nicht läuft, fällt `useBugs` auf `src/lib/mockData.ts` zurück.
Im Header steht dann „mock data · backend offline". Praktisch für
UI-Entwicklung ohne lokale DB.

## Verzeichnis-Struktur

```
src/
├── pages/        # Top-level Routen (BugListPage, später BugDetailPage, …)
├── components/   # Wiederverwendbare UI-Bausteine (BugTable, FilterBar, …)
├── hooks/        # React-Hooks (useBugFilters, useBugs, useFilterOptions)
├── lib/          # Utilities (api-Client, filterBugs, mockData)
└── types/        # TypeScript-Typen, gespiegelt aus openapi.yaml
```

## Theming

Tailwind ist mit `darkMode: 'class'` konfiguriert. Die Farb-Tokens stehen als
CSS-Variablen in `src/index.css` (Light & Dark). `<ThemeToggle>` schaltet die
`.dark`-Klasse auf `<html>` und persistiert in `localStorage`.

## Verwandte Tasks

- T039 (BTASIG3-48): Vite + React + TS Setup
- T040 (BTASIG3-49): Tailwind Setup + Token-System
- T041 (BTASIG3-50): React-Router + Auth-Routes (folgt)
- T053 (BTASIG3-62): Bug-Filter-UI mit URL-Sync
