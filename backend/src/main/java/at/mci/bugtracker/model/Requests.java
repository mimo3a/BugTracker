package at.mci.bugtracker.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public final class Requests {
    private Requests() {}

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record Register(
            @NotBlank(message = "Username darf nicht leer sein")
            @Size(min = 3, max = 50, message = "Username muss zwischen 3 und 50 Zeichen lang sein")
            String username,

            @NotBlank(message = "E-Mail darf nicht leer sein")
            @Email(message = "Ungültiges E-Mail-Format")
            String email,

            @NotBlank(message = "Passwort darf nicht leer sein")
            @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein")
            String password,

            @NotBlank(message = "Passwort-Bestätigung darf nicht leer sein")
            String passwordConfirm
    ) {}

    public record Login(String username, String password) {}

    public record ChangePassword(String currentPassword, String newPassword) {}

    public record CreateProject(String name, String description) {}

    public record CreateBug(
            String title,
            String description,
            String status,
            String priority,
            String severity,
            Integer projectId,
            Integer assigneeId
    ) {}

    public record UpdateBug(
            String title,
            String description,
            String status,
            String priority,
            String severity,
            Integer projectId,
            Integer assigneeId
    ) {}

    public record UpdateBugStatus(String status) {}

    public record BulkUpdateBugs(
            List<Integer> ids,
            String status,
            Integer assigneeId,
            String priority
    ) {}

    public record BulkDeleteBugs(List<Integer> ids) {}

    public record CreateComment(String content) {}

    public record CreateTag(String name, String color) {}

    public record SetBugTags(List<Integer> tagIds) {}
}

