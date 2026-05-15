package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.model.Tag;
import at.mci.bugtracker.model.UserRole;
import at.mci.bugtracker.service.TagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Slice-Test: @WebMvcTest lädt @EnableMethodSecurity NICHT, daher wird
// @PreAuthorize hier bewusst NICHT geprüft (das passiert im
// RoleAccessIntegrationTest mit vollem Context). Hier nur MVC-Mapping,
// Bean-Validation und Service-Fehler-Propagation.
@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    // @WebMvcTest lädt die Security-Filterchain mit; SessionAuthFilter braucht
    // eine SessionStore-Bean, auch wenn addFilters=false die Filter abschaltet.
    @MockBean
    private SessionStore sessionStore;

    @BeforeEach
    void authenticate() {
        // listTags ruft CurrentSession.require() — Principal muss eine
        // SessionStore.Session sein, sonst 401.
        SessionStore.Session session = new SessionStore.Session(1L, "admin", UserRole.ADMIN);
        var auth = new TestingAuthenticationToken(session, null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listTagsReturnsAllTags() throws Exception {
        when(tagService.listTags()).thenReturn(List.of(
                new Tag(1L, "Backend", "#3B82F6", OffsetDateTime.now())));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Backend"))
                .andExpect(jsonPath("$[0].color").value("#3B82F6"));
    }

    @Test
    void createTagReturns201() throws Exception {
        when(tagService.createTag("Security", "#EF4444"))
                .thenReturn(new Tag(9L, "Security", "#EF4444", OffsetDateTime.now()));

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Security\",\"color\":\"#EF4444\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.name").value("Security"));

        verify(tagService).createTag("Security", "#EF4444");
    }

    @Test
    void createTagRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \",\"color\":\"#EF4444\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name darf nicht leer sein"));
    }

    @Test
    void createTagRejectsInvalidHexColor() throws Exception {
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Valid\",\"color\":\"red\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.color").exists());
    }

    @Test
    void createTagAllowsNullColor() throws Exception {
        when(tagService.createTag(eq("NoColor"), eq(null)))
                .thenReturn(new Tag(7L, "NoColor", null, OffsetDateTime.now()));

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NoColor\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.color").doesNotExist());
    }

    @Test
    void updateTagAcceptsBothPatchAndPut() throws Exception {
        when(tagService.updateTag(eq(2L), eq("UI"), eq("#000000")))
                .thenReturn(new Tag(2L, "UI", "#000000", OffsetDateTime.now()));

        mockMvc.perform(patch("/api/tags/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UI\",\"color\":\"#000000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UI"));

        mockMvc.perform(put("/api/tags/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UI\",\"color\":\"#000000\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateTagPropagates404FromService() throws Exception {
        when(tagService.updateTag(eq(99L), eq("Ghost"), eq("#FFFFFF")))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag nicht gefunden"));

        mockMvc.perform(patch("/api/tags/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ghost\",\"color\":\"#FFFFFF\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Tag nicht gefunden"));
    }

    @Test
    void createTagPropagates409FromService() throws Exception {
        when(tagService.createTag(eq("Backend"), eq("#3B82F6")))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Tag existiert bereits"));

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Backend\",\"color\":\"#3B82F6\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Tag existiert bereits"));
    }

    @Test
    void deleteTagReturns204() throws Exception {
        mockMvc.perform(delete("/api/tags/3"))
                .andExpect(status().isNoContent());

        verify(tagService).deleteTag(3L);
    }
}
