package at.mci.bugtracker.model;

import java.time.OffsetDateTime;

public record User(
        long id,
        String username,
        String email,
        String passwordHash,
        UserRole role,
        boolean active,
        OffsetDateTime createdAt
) {}