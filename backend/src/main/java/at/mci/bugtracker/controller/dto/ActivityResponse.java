package at.mci.bugtracker.controller.dto;

import java.time.OffsetDateTime;

public record ActivityResponse(
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
