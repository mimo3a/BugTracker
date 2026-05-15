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
            @Size(max = 255, message = "E-Mail darf maximal 255 Zeichen lang sein")
            @Email(message = "Ungueltiges E-Mail-Format")
            String email,

            @NotBlank(message = "Passwort darf nicht leer sein")
            @Size(min = 8, max = 255, message = "Passwort muss zwischen 8 und 255 Zeichen lang sein")
            String password,

            @NotBlank(message = "Passwort-Bestaetigung darf nicht leer sein")
            @Size(min = 8, max = 255, message = "Passwort-Bestaetigung muss zwischen 8 und 255 Zeichen lang sein")
            String passwordConfirm
    ) {}

    public record Login(
            @NotBlank(message = "Username darf nicht leer sein")
            @Size(max = 50, message = "Username darf maximal 50 Zeichen lang sein")
            String username,

            @NotBlank(message = "Passwort darf nicht leer sein")
            @Size(max = 255, message = "Passwort darf maximal 255 Zeichen lang sein")
            String password
    ) {}

    public record ChangePassword(
            @NotBlank(message = "Aktuelles Passwort darf nicht leer sein")
            @Size(max = 255, message = "Aktuelles Passwort darf maximal 255 Zeichen lang sein")
            String currentPassword,

            @NotBlank(message = "Neues Passwort darf nicht leer sein")
            @Size(min = 6, max = 255, message = "Neues Passwort muss zwischen 6 und 255 Zeichen lang sein")
            String newPassword
    ) {}

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
