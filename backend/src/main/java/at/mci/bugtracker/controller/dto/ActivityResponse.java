package at.mci.bugtracker.controller.dto;

import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        Long bugId,
        Long userId,
        String userName,
        String action,
        String field,
        String oldValue,
        String newValue,
        LocalDateTime createdAt
) {}
