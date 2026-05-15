package at.mci.bugtracker.model;

import java.time.OffsetDateTime;

// Tag-Stammdaten (FA-16). name ist DB-seitig UNIQUE, color ist optional
// (NULL erlaubt) und — wenn gesetzt — ein Hex-Code im Format #RGB oder
// #RRGGBB. createdAt wird DB-seitig per DEFAULT CURRENT_TIMESTAMP gesetzt.
// OffsetDateTime statt LocalDateTime, weil die Spalte TIMESTAMP WITH TIME
// ZONE ist und der Postgres-Treiber sonst eine PSQLException wirft (gleiche
// Lehre wie BugRowMapper / ActivityDao).
public record Tag(
        Long id,
        String name,
        String color,
        OffsetDateTime createdAt
) {}
