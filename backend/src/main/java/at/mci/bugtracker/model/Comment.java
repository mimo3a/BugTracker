package at.mci.bugtracker.model;

import java.time.OffsetDateTime;

// Kommentar zu einem Bug (FA-11). Shape exakt am Frontend-Type
// (frontend/src/types/bug.ts Comment) ausgerichtet: userName wird per
// JOIN über users aufgelöst, damit das UI keinen zweiten Request für
// Autor-Namen braucht. OffsetDateTime wie Activity (TIMESTAMP WITH TIME
// ZONE -> ISO-String im JSON, vom Frontend per formatLocalDateTime geparst).
public record Comment(
        Long id,
        long bugId,
        long userId,
        String userName,
        String content,
        OffsetDateTime createdAt
) {}
