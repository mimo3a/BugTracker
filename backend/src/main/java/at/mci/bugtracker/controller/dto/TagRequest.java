package at.mci.bugtracker.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Request-Body für POST/PUT/PATCH /api/tags. Frontend (api.ts TagInput)
// sendet { name, color? } — color ist optional/nullable.
//
// @Pattern lässt null bewusst durch (Bean-Validation-Semantik) — eine
// fehlende Farbe ist erlaubt, nur ein gesetzter Wert muss ein gültiger
// Hex-Code sein (#RGB oder #RRGGBB, case-insensitive).
public record TagRequest(
        @NotBlank(message = "Name darf nicht leer sein")
        @Size(max = 50, message = "Name darf maximal 50 Zeichen lang sein")
        String name,

        @Pattern(
                regexp = "^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$",
                message = "Farbe muss ein Hex-Code sein (z.B. #3B82F6)")
        String color
) {}
