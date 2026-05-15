package at.mci.bugtracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request-Body für POST /api/bugs/{id}/comments. Frontend (api.ts
// CreateCommentInput) sendet { content }. HTML-Tags werden bewusst NICHT
// serverseitig escaped — das Frontend rendert content als plain text
// (whitespace-pre-wrap), XSS-sicher per React-Default-Escaping.
public record CreateCommentRequest(
        @NotBlank(message = "Kommentar darf nicht leer sein")
        @Size(max = 5000, message = "Kommentar darf maximal 5000 Zeichen lang sein")
        String content
) {}
