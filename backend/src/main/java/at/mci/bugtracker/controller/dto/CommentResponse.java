package at.mci.bugtracker.controller.dto;

import java.time.OffsetDateTime;

public record CommentResponse(
        Long id,
        long bugId,
        long userId,
        String userName,
        String content,
        OffsetDateTime createdAt
) {}
