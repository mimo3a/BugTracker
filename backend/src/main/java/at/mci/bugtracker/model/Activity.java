package at.mci.bugtracker.model;

import java.time.OffsetDateTime;

public record Activity(
        Long id,
        Long bugId,
        Long userId,
        String userName,
        String action,
        String field,
        String oldValue,
        String newValue,
        OffsetDateTime createdAt
) {}
