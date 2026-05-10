package at.mci.bugtracker.model;

import java.time.OffsetDateTime;

public record UserWithoutHash(
        long id,
        String username,
        String email,
        UserRole role,
        boolean active,
        OffsetDateTime createdAt
) {}
