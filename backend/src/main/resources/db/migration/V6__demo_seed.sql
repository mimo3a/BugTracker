-- Demo-Datensatz für Live-Vorführung + 1-Woche-Testbetrieb (22.05.2026).
-- Damit die App nicht leer aussieht und Filter/Status/Tags real testbar sind.
--
-- Robustheit über Umgebungen hinweg:
--   * User werden per USERNAME aufgelöst (IDs sind je Umgebung verschieden:
--     lokal admin=3, Neon admin=1) — admin/dev/tester kommen aus V4.
--   * Tags per NAME; neue Tags via ON CONFLICT (name) DO NOTHING idempotent
--     (Backend/Frontend/Bug/Feature existieren bereits aus V2).
--   * bug_tags wird per Titel↔Tagname-Join gesetzt (Seed-Titel sind eindeutig).
-- Status/Priorität halten exakt die CHECK-Constraints aus V2 ein.

-- ---------------------------------------------------------------------------
-- 1) Zusätzliche, für einen Bugtracker sinnvolle Tags
-- ---------------------------------------------------------------------------
INSERT INTO tags (name, color) VALUES
    ('Security',      '#dc2626'),
    ('Datenbank',     '#0891b2'),
    ('Performance',   '#ea580c'),
    ('UI/UX',         '#7c3aed'),
    ('Auth',          '#db2777'),
    ('Dokumentation', '#65a30d'),
    ('API',           '#0d9488'),
    ('DevOps',        '#475569')
ON CONFLICT (name) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 2) 25 Demo-Bugs. reporter/assignee per Username; age_days/upd_days = Tage
--    in der Vergangenheit (für realistische, gestreute Zeitstempel).
-- ---------------------------------------------------------------------------
INSERT INTO bugs (title, description, status, priority, reporter_id, assignee_id, archived, created_at, updated_at)
SELECT v.title, v.description, v.status, v.priority,
       r.id, a.id, v.archived,
       CURRENT_TIMESTAMP - (v.age_days  || ' days')::interval,
       CURRENT_TIMESTAMP - (v.upd_days  || ' days')::interval
FROM (VALUES
    ('Login-Button reagiert nicht beim ersten Klick',
     'Nach dem Laden der Login-Seite muss man zweimal auf "Anmelden" klicken. Erster Klick wird verschluckt — vermutlich Fokus-/State-Race im Formular.',
     'IN_BEARBEITUNG', 'HOCH', 'tester', 'dev', false, 34, 3),
    ('Passwort-Reset-Mail kommt nicht an',
     'Reset-Anforderung meldet Erfolg, aber es trifft keine E-Mail ein. Betrifft alle getesteten Adressen.',
     'NEU', 'KRITISCH', 'tester', NULL, false, 33, 33),
    ('Bug-Liste lädt langsam bei mehr als 500 Einträgen',
     'Ab ~500 Bugs blockt die Liste den UI-Thread für ca. 2 Sekunden. Pagination greift erst danach.',
     'IN_BEARBEITUNG', 'MITTEL', 'dev', 'dev', false, 30, 2),
    ('Statuswechsel ARCHIVIERT lässt sich nicht rückgängig machen',
     'Ein archivierter Bug kann über die UI nicht reaktiviert werden, obwohl der /restore-Endpoint existiert. Button fehlt.',
     'IM_REVIEW', 'MITTEL', 'dev', 'admin', false, 28, 1),
    ('CSV-Export verliert Umlaute',
     'Beim Export erscheinen ä/ö/ü als Fragezeichen. Encoding vermutlich nicht UTF-8.',
     'ERLEDIGT', 'NIEDRIG', 'tester', 'dev', false, 26, 12),
    ('Filter-Dropdown verliert Auswahl nach Reload',
     'Gesetzte Status-Filter sind nach Seiten-Reload zurückgesetzt, obwohl die URL die Query-Parameter enthält.',
     'NEU', 'NIEDRIG', 'tester', NULL, false, 24, 24),
    ('Detailseite zeigt 404 nach dem Löschen eines Bugs',
     'Wird ein Bug aus der Liste gelöscht und danach die offene Detail-URL neu geladen, kommt ein harter 404 statt einer Meldung.',
     'NEU', 'MITTEL', 'tester', 'dev', false, 23, 23),
    ('Kommentar mit nur Leerzeichen wird angenommen',
     'Das Backend lehnt leere Kommentare ab, aber ein Kommentar aus nur Whitespace im Frontend wirkt zunächst abgeschickt.',
     'IN_BEARBEITUNG', 'NIEDRIG', 'dev', 'dev', false, 21, 1),
    ('Rollen-Badge im Header zeigt veraltete Rolle',
     'Nachdem ein Admin die eigene Ansicht wechselt, zeigt das Badge bis zum nächsten Reload die alte Rolle.',
     'NEU', 'NIEDRIG', 'admin', NULL, false, 20, 20),
    ('Prioritäts-Sortierung ist alphabetisch statt nach Schweregrad',
     'Sortierung ergibt HOCH, KRITISCH, MITTEL, NIEDRIG statt KRITISCH > HOCH > MITTEL > NIEDRIG.',
     'IM_REVIEW', 'MITTEL', 'dev', 'admin', false, 19, 2),
    ('Session läuft trotz Aktivität nach 30 Minuten ab',
     'Nutzer werden mitten in der Bearbeitung ausgeloggt. Session sollte bei Aktivität verlängert werden.',
     'IN_BEARBEITUNG', 'HOCH', 'tester', 'dev', false, 18, 1),
    ('Tag mit sehr langem Namen sprengt das Layout',
     'Ein Tag-Name über ~30 Zeichen schiebt die Action-Buttons in der Admin-Liste aus dem Viewport.',
     'NEU', 'NIEDRIG', 'admin', NULL, false, 17, 17),
    ('Bug-Anlegen ohne Beschreibung gibt unklare Fehlermeldung',
     'Fehlt die Beschreibung, kommt nur "400" statt einer feldbezogenen Meldung.',
     'ERLEDIGT', 'MITTEL', 'tester', 'dev', false, 16, 8),
    ('Suchfeld ignoriert Groß-/Kleinschreibung nicht konsistent',
     'Suche nach "login" findet "Login"-Bugs, "LOGIN" jedoch nicht zuverlässig.',
     'NEU', 'MITTEL', 'tester', NULL, false, 15, 15),
    ('Mehrfach-Klick auf Speichern legt Bug doppelt an',
     'Schnelles Doppelklicken auf "create" erzeugt zwei identische Bugs. Button sollte währenddessen sperren.',
     'IN_BEARBEITUNG', 'HOCH', 'dev', 'dev', false, 14, 1),
    ('Archivierte Bugs erscheinen im Standard-Filter',
     'Ohne aktiven Archiv-Filter sollten archivierte Bugs ausgeblendet sein — vereinzelt tauchen sie doch auf.',
     'IM_REVIEW', 'MITTEL', 'dev', 'admin', false, 13, 2),
    ('Dark-Mode-Einstellung wird nicht gespeichert',
     'Nach erneutem Login ist wieder der Light-Mode aktiv. Präferenz wird nicht persistiert.',
     'NEU', 'NIEDRIG', 'tester', NULL, false, 12, 12),
    ('API-Dokumentation fehlt für /api/bugs/{id}/comments',
     'Der Kommentar-Endpoint ist nicht in der Swagger-Übersicht aufgeführt.',
     'NEU', 'NIEDRIG', 'admin', 'dev', false, 11, 11),
    ('Zuweisung an deaktivierten Benutzer möglich',
     'Ein Bug lässt sich einem User zuweisen, dessen Konto deaktiviert ist (active=false).',
     'NEU', 'HOCH', 'admin', 'admin', false, 10, 10),
    ('Tabellen-Header bleibt beim Scrollen nicht sichtbar',
     'Bei langer Bug-Liste verschwindet die Kopfzeile beim Scrollen — sticky-Header fehlt.',
     'NEU', 'NIEDRIG', 'tester', NULL, false, 9, 9),
    ('Validierungsfehler scrollt nicht ins sichtbare Feld',
     'Bei mehreren Fehlern im Formular muss man manuell zum ersten fehlerhaften Feld scrollen.',
     'IN_BEARBEITUNG', 'NIEDRIG', 'dev', 'dev', false, 8, 1),
    ('Ungültiges Datumsformat in der Aktivitäts-Historie',
     'Zeitstempel werden teils als ISO-Roh-String statt lokalisiert angezeigt.',
     'ERLEDIGT', 'NIEDRIG', 'tester', 'dev', false, 7, 4),
    ('Rechtschreibfehler auf der Registrierungsseite',
     '"Passwort bestätign" statt "bestätigen" im Label der Registrierung.',
     'ERLEDIGT', 'NIEDRIG', 'tester', 'admin', false, 6, 5),
    ('Feature-Wunsch: Bulk-Statuswechsel für mehrere Bugs',
     'Mehrere Bugs in der Liste auswählen und gemeinsam den Status setzen können.',
     'ABGELEHNT', 'NIEDRIG', 'dev', NULL, false, 40, 36),
    ('Alter Prototyp-Endpoint /api/notifications dokumentiert aber tot',
     'Verbleibsel aus einem frühen Prototyp. Endpoint existiert nicht mehr, Doku-Eintrag irreführend.',
     'ARCHIVIERT', 'NIEDRIG', 'admin', 'admin', true, 45, 38)
) AS v(title, description, status, priority, reporter, assignee, archived, age_days, upd_days)
JOIN users r ON r.username = v.reporter
LEFT JOIN users a ON a.username = v.assignee;

-- ---------------------------------------------------------------------------
-- 3) Tag-Zuordnungen (M:N) — per Titel↔Tagname-Join, idempotent gegen Re-Runs.
-- ---------------------------------------------------------------------------
INSERT INTO bug_tags (bug_id, tag_id)
SELECT b.id, t.id
FROM (VALUES
    ('Login-Button reagiert nicht beim ersten Klick',                 'Frontend'),
    ('Login-Button reagiert nicht beim ersten Klick',                 'Auth'),
    ('Passwort-Reset-Mail kommt nicht an',                            'Auth'),
    ('Passwort-Reset-Mail kommt nicht an',                            'Backend'),
    ('Bug-Liste lädt langsam bei mehr als 500 Einträgen',             'Performance'),
    ('Bug-Liste lädt langsam bei mehr als 500 Einträgen',             'Frontend'),
    ('Statuswechsel ARCHIVIERT lässt sich nicht rückgängig machen',   'Bug'),
    ('CSV-Export verliert Umlaute',                                   'Backend'),
    ('Filter-Dropdown verliert Auswahl nach Reload',                  'Frontend'),
    ('Detailseite zeigt 404 nach dem Löschen eines Bugs',             'Frontend'),
    ('Kommentar mit nur Leerzeichen wird angenommen',                 'API'),
    ('Rollen-Badge im Header zeigt veraltete Rolle',                  'UI/UX'),
    ('Prioritäts-Sortierung ist alphabetisch statt nach Schweregrad', 'Backend'),
    ('Session läuft trotz Aktivität nach 30 Minuten ab',              'Security'),
    ('Session läuft trotz Aktivität nach 30 Minuten ab',              'Auth'),
    ('Tag mit sehr langem Namen sprengt das Layout',                  'UI/UX'),
    ('Bug-Anlegen ohne Beschreibung gibt unklare Fehlermeldung',      'API'),
    ('Suchfeld ignoriert Groß-/Kleinschreibung nicht konsistent',     'Backend'),
    ('Mehrfach-Klick auf Speichern legt Bug doppelt an',              'Frontend'),
    ('Archivierte Bugs erscheinen im Standard-Filter',                'Datenbank'),
    ('Dark-Mode-Einstellung wird nicht gespeichert',                  'UI/UX'),
    ('API-Dokumentation fehlt für /api/bugs/{id}/comments',           'Dokumentation'),
    ('Zuweisung an deaktivierten Benutzer möglich',                   'Security'),
    ('Validierungsfehler scrollt nicht ins sichtbare Feld',           'UI/UX'),
    ('Ungültiges Datumsformat in der Aktivitäts-Historie',            'Frontend'),
    ('Feature-Wunsch: Bulk-Statuswechsel für mehrere Bugs',           'Feature'),
    ('Alter Prototyp-Endpoint /api/notifications dokumentiert aber tot','DevOps')
) AS m(title, tagname)
JOIN bugs b ON b.title = m.title
JOIN tags t ON t.name = m.tagname;
