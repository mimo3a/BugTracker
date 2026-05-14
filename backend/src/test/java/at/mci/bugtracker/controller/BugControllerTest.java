package at.mci.bugtracker.controller;

import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugFilter;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import at.mci.bugtracker.model.UserRole;
import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.service.BugPage;
import at.mci.bugtracker.service.BugService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BugController.class)
@AutoConfigureMockMvc(addFilters = false)
class BugControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BugService bugService;

    @MockBean
    private SessionStore sessionStore;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listBugsBuildsFilterFromQueryParamsAndReturnsPage() throws Exception {
        Bug bug = new Bug(
                42L,
                "Login fails",
                "Steps",
                BugStatus.NEU,
                BugPriority.HOCH,
                1L,
                "tom",
                2L,
                "marie",
                List.of(1L),
                List.of("Backend"),
                false,
                LocalDateTime.of(2026, 5, 10, 12, 0),
                LocalDateTime.of(2026, 5, 10, 12, 5)
        );
        when(bugService.listBugs(any(BugFilter.class), eq(2)))
                .thenReturn(new BugPage(List.of(bug), 73, 2, 50));

        mockMvc.perform(get("/api/bugs")
                        .param("status", "NEU")
                        .param("status", "IN_BEARBEITUNG")
                        .param("priority", "HOCH")
                        .param("assigneeId", "2")
                        .param("tagIds", "1")
                        .param("tagIds", "3")
                        .param("search", " login ")
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(73))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.pageSize").value(50))
                .andExpect(jsonPath("$.bugs[0].id").value(42))
                .andExpect(jsonPath("$.bugs[0].title").value("Login fails"))
                .andExpect(jsonPath("$.bugs[0].tagIds[0]").value(1));

        ArgumentCaptor<BugFilter> filterCaptor = ArgumentCaptor.forClass(BugFilter.class);
        verify(bugService).listBugs(filterCaptor.capture(), eq(2));

        BugFilter filter = filterCaptor.getValue();
        assertThat(filter.statuses()).containsExactly(BugStatus.NEU, BugStatus.IN_BEARBEITUNG);
        assertThat(filter.priority()).isEqualTo(BugPriority.HOCH);
        assertThat(filter.assigneeId()).isEqualTo(2L);
        assertThat(filter.tagIds()).containsExactly(1L, 3L);
        assertThat(filter.search()).isEqualTo("login");
        assertThat(filter.includeArchived()).isFalse();
        assertThat(filter.pageSize()).isEqualTo(50);
    }

    @Test
    void listBugsClampsNegativePageAndDefaultsEmptyFilters() throws Exception {
        when(bugService.listBugs(any(BugFilter.class), eq(0)))
                .thenReturn(new BugPage(List.of(), 0, 0, 50));

        mockMvc.perform(get("/api/bugs")
                        .param("assigneeId", "3")
                        .param("page", "-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.page").value(0));

        ArgumentCaptor<BugFilter> filterCaptor = ArgumentCaptor.forClass(BugFilter.class);
        verify(bugService).listBugs(filterCaptor.capture(), eq(0));
        BugFilter filter = filterCaptor.getValue();
        assertThat(filter.assigneeId()).isEqualTo(3L);
        assertThat(filter.tagIds()).isEmpty();
    }

    @Test
    void updatePriorityReturnsUpdatedBug() throws Exception {
        SessionStore.Session session = new SessionStore.Session(10L, "dev", UserRole.DEVELOPER);
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(session, null);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Bug bug = new Bug(
                42L,
                "Login fails",
                "Steps",
                BugStatus.NEU,
                BugPriority.KRITISCH,
                1L,
                "tom",
                null,
                null,
                List.of(),
                List.of(),
                false,
                LocalDateTime.of(2026, 5, 10, 12, 0),
                LocalDateTime.of(2026, 5, 10, 12, 5)
        );
        when(bugService.updatePriority(42L, BugPriority.KRITISCH, 10L)).thenReturn(bug);

        mockMvc.perform(patch("/api/bugs/42/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priority\":\"KRITISCH\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.priority").value("KRITISCH"));

        verify(bugService).updatePriority(42L, BugPriority.KRITISCH, 10L);
    }

    @Test
    void updatePriorityWithInvalidValueReturns400() throws Exception {
        mockMvc.perform(patch("/api/bugs/42/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"priority\":\"DRINGEND\"}"))
                .andExpect(status().isBadRequest());
    }
}
