package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.controller.dto.TagRequest;
import at.mci.bugtracker.model.Tag;
import at.mci.bugtracker.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// FA-16 Tag-Verwaltung (T038a). Lesen darf jeder eingeloggte User (für das
// Tag-Dropdown beim Bug-Anlegen); Schreiben ist ADMIN-only — durchgesetzt
// via @PreAuthorize (T021b, Plumbing in SessionAuthFilter + @EnableMethodSecurity).
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<Tag> listTags() {
        CurrentSession.require();
        return tagService.listTags();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Tag createTag(@Valid @RequestBody TagRequest request) {
        return tagService.createTag(request.name(), request.color());
    }

    // Frontend (api.ts) nutzt PATCH; TASKS.md T038a spezifiziert PUT. Beide
    // Verben sind auf denselben Handler gemappt, damit der deployte Frontend-
    // Contract UND der Pflichtenheft-Wortlaut erfüllt sind. Semantik ist ein
    // Full-Replace von name+color (das Frontend sendet immer beide Felder).
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @PreAuthorize("hasRole('ADMIN')")
    public Tag updateTag(@PathVariable long id, @Valid @RequestBody TagRequest request) {
        return tagService.updateTag(id, request.name(), request.color());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
