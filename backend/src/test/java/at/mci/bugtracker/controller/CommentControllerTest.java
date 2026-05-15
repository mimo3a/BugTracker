package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.model.Comment;
import at.mci.bugtracker.model.UserRole;
import at.mci.bugtracker.service.CommentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    // @WebMvcTest lädt die Security-Filterchain mit; SessionAuthFilter braucht
    // eine SessionStore-Bean, auch wenn addFilters=false die Filter abschaltet.
    @MockBean
    private SessionStore sessionStore;

    @BeforeEach
    void authenticate() {
        SessionStore.Session session = new SessionStore.Session(2L, "tester", UserRole.TESTER);
        var auth = new TestingAuthenticationToken(session, null);
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listCommentsReturnsComments() throws Exception {
        when(commentService.getComments(42L)).thenReturn(List.of(
                new Comment(1L, 42L, 2L, "tester", "Reproduzierbar", OffsetDateTime.now())));

        mockMvc.perform(get("/api/bugs/42/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].bugId").value(42))
                .andExpect(jsonPath("$[0].userName").value("tester"))
                .andExpect(jsonPath("$[0].content").value("Reproduzierbar"));
    }

    @Test
    void addCommentReturns201AndUsesSessionUserId() throws Exception {
        when(commentService.addComment(eq(42L), eq(2L), eq("Mein Kommentar")))
                .thenReturn(new Comment(5L, 42L, 2L, "tester", "Mein Kommentar", OffsetDateTime.now()));

        mockMvc.perform(post("/api/bugs/42/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Mein Kommentar\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.content").value("Mein Kommentar"));

        verify(commentService).addComment(42L, 2L, "Mein Kommentar");
    }

    @Test
    void addCommentRejectsBlankContent() throws Exception {
        mockMvc.perform(post("/api/bugs/42/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Kommentar darf nicht leer sein"));
    }

    @Test
    void addCommentPropagates404WhenBugMissing() throws Exception {
        when(commentService.addComment(eq(999L), eq(2L), eq("Hi")))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));

        mockMvc.perform(post("/api/bugs/999/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Hi\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Bug nicht gefunden"));
    }
}
