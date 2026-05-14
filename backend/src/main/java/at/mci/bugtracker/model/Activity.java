package at.mci.bugtracker.model;

import java.time.OffsetDateTime;

// Historie-Eintrag pro Bug-Änderung. Polymorph aufgebaut: 'action' beschreibt
// die Operation ("CREATED", "UPDATED", "ARCHIVED", "RESTORED"), 'field' nennt
// das geänderte Feld bei UPDATED (z.B. "status"), old/new halten die Werte
// als String. Schema siehe V3__activities.sql aus T056.
public record Activity(
        Long id,
        long bugId,
        long userId,
        String userName,
        String action,
        String field,
        String oldValue,
        String newValue,
        OffsetDateTime createdAt
) {}
