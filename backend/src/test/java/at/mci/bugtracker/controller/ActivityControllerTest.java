package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.model.Activity;
import at.mci.bugtracker.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService activityService;

    @MockBean
    private SessionStore sessionStore;

    @Test
    void getBugActivitiesReturnsAllResponseFields() throws Exception {
        Activity activity = new Activity(
                5L,
                42L,
                2L,
                "marie",
                "UPDATED",
                "status",
                "NEU",
                "IN_BEARBEITUNG",
                LocalDateTime.of(2026, 5, 10, 11, 0)
        );
        when(activityService.getBugActivities(42L)).thenReturn(List.of(activity));

        mockMvc.perform(get("/api/bugs/42/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].bugId").value(42))
                .andExpect(jsonPath("$[0].userId").value(2))
                .andExpect(jsonPath("$[0].userName").value("marie"))
                .andExpect(jsonPath("$[0].action").value("UPDATED"))
                .andExpect(jsonPath("$[0].field").value("status"))
                .andExpect(jsonPath("$[0].oldValue").value("NEU"))
                .andExpect(jsonPath("$[0].newValue").value("IN_BEARBEITUNG"))
                .andExpect(jsonPath("$[0].createdAt").value("2026-05-10T11:00:00"));

        verify(activityService).getBugActivities(42L);
    }

    @Test
    void getBugActivitiesReturnsNotFoundForMissingBug() throws Exception {
        when(activityService.getBugActivities(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden"));

        mockMvc.perform(get("/api/bugs/999/activities"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Bug nicht gefunden"));
    }
}
