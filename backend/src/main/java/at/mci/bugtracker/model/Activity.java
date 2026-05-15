package at.mci.bugtracker.model;

import java.time.OffsetDateTime;

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
