package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.model.UserRole;
import at.mci.bugtracker.model.UserWithoutHash;
import at.mci.bugtracker.service.UserService;
import org.junit.jupiter.api.AfterEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private UserService userService;

    @MockBean private SessionStore sessionStore;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(long userId, String username, UserRole role) {
        SessionStore.Session session = new SessionStore.Session(userId, username, role);
        TestingAuthenticationToken token = new TestingAuthenticationToken(session, null);
        token.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void listUsersReturnsAllUsersAsJson() throws Exception {
        authenticate(1L, "admin", UserRole.ADMIN);
        when(userService.listUsers()).thenReturn(List.of(
                new UserWithoutHash(1L, "admin", "admin@example.com",
                        UserRole.ADMIN, true, OffsetDateTime.parse("2026-05-01T10:00:00Z")),
                new UserWithoutHash(2L, "tester", "tester@example.com",
                        UserRole.TESTER, true, OffsetDateTime.parse("2026-05-02T10:00:00Z"))
        ));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].username").value("tester"))
                .andExpect(jsonPath("$[1].active").value(true));
    }

    @Test
    void patchUserRoleDelegatesToService() throws Exception {
        authenticate(1L, "admin", UserRole.ADMIN);
        when(userService.updateUser(eq(2L), any(), eq(1L))).thenReturn(
                new UserWithoutHash(2L, "tester", "tester@example.com",
                        UserRole.DEVELOPER, true, OffsetDateTime.parse("2026-05-02T10:00:00Z"))
        );

        mockMvc.perform(patch("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"DEVELOPER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.role").value("DEVELOPER"));

        verify(userService).updateUser(eq(2L), any(), eq(1L));
    }

    @Test
    void patchUserActiveDelegatesToService() throws Exception {
        authenticate(1L, "admin", UserRole.ADMIN);
        when(userService.updateUser(eq(2L), any(), eq(1L))).thenReturn(
                new UserWithoutHash(2L, "tester", "tester@example.com",
                        UserRole.TESTER, false, OffsetDateTime.parse("2026-05-02T10:00:00Z"))
        );

        mockMvc.perform(patch("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void patchUserPropagates403WhenServiceForbids() throws Exception {
        authenticate(3L, "dev", UserRole.DEVELOPER);
        when(userService.updateUser(eq(2L), any(), eq(3L)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Nur ADMIN darf User verwalten"));

        mockMvc.perform(patch("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ADMIN\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchUserPropagates400WhenServiceRejects() throws Exception {
        authenticate(1L, "admin", UserRole.ADMIN);
        when(userService.updateUser(eq(1L), any(), eq(1L)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "ADMIN darf eigene Rolle nicht ändern"));

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"DEVELOPER\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchUserWithInvalidRoleReturns400() throws Exception {
        authenticate(1L, "admin", UserRole.ADMIN);

        mockMvc.perform(patch("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"SUPERUSER\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
