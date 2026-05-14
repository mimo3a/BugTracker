package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.UpdateUserRequest;
import at.mci.bugtracker.dao.UserDao;
import at.mci.bugtracker.model.User;
import at.mci.bugtracker.model.UserRole;
import at.mci.bugtracker.model.UserWithoutHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserDao userDao;

    @InjectMocks private UserService userService;

    private static final long ADMIN_ID = 1L;
    private static final long TARGET_ID = 2L;
    private static final long DEVELOPER_ID = 3L;

    private User admin;
    private User developer;
    private User target;

    @BeforeEach
    void setUp() {
        admin = user(ADMIN_ID, "admin", UserRole.ADMIN, true);
        developer = user(DEVELOPER_ID, "dev", UserRole.DEVELOPER, true);
        target = user(TARGET_ID, "tester", UserRole.TESTER, true);
    }

    @Test
    void listUsers_returnsUsersWithoutPasswordHash() {
        when(userDao.findAll()).thenReturn(List.of(admin, developer, target));

        List<UserWithoutHash> result = userService.listUsers();

        assertThat(result).hasSize(3);
        assertThat(result.get(0).username()).isEqualTo("admin");
        assertThat(result.get(2).role()).isEqualTo(UserRole.TESTER);
    }

    @Test
    void adminChangesOtherUsersRole_persistsAndReturnsUpdated() {
        when(userDao.findById(ADMIN_ID)).thenReturn(admin);
        when(userDao.findById(TARGET_ID))
                .thenReturn(target, user(TARGET_ID, "tester", UserRole.DEVELOPER, true));

        UserWithoutHash result = userService.updateUser(
                TARGET_ID, new UpdateUserRequest(UserRole.DEVELOPER, null), ADMIN_ID);

        assertThat(result.role()).isEqualTo(UserRole.DEVELOPER);
        verify(userDao).updateRole(TARGET_ID, UserRole.DEVELOPER);
        verify(userDao, never()).updateActive(eq(TARGET_ID), org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    void adminDeactivatesOtherUser_persistsAndReturnsUpdated() {
        when(userDao.findById(ADMIN_ID)).thenReturn(admin);
        when(userDao.findById(TARGET_ID))
                .thenReturn(target, user(TARGET_ID, "tester", UserRole.TESTER, false));

        UserWithoutHash result = userService.updateUser(
                TARGET_ID, new UpdateUserRequest(null, false), ADMIN_ID);

        assertThat(result.active()).isFalse();
        verify(userDao).updateActive(TARGET_ID, false);
        verify(userDao, never()).updateRole(eq(TARGET_ID), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void adminChangesBothRoleAndActive_appliesBoth() {
        when(userDao.findById(ADMIN_ID)).thenReturn(admin);
        when(userDao.findById(TARGET_ID))
                .thenReturn(target, user(TARGET_ID, "tester", UserRole.DEVELOPER, false));

        UserWithoutHash result = userService.updateUser(
                TARGET_ID, new UpdateUserRequest(UserRole.DEVELOPER, false), ADMIN_ID);

        assertThat(result.role()).isEqualTo(UserRole.DEVELOPER);
        assertThat(result.active()).isFalse();
        verify(userDao).updateRole(TARGET_ID, UserRole.DEVELOPER);
        verify(userDao).updateActive(TARGET_ID, false);
    }

    @Test
    void developerTriesToUpdateUser_returns403() {
        when(userDao.findById(DEVELOPER_ID)).thenReturn(developer);

        assertThatThrownBy(() -> userService.updateUser(
                TARGET_ID, new UpdateUserRequest(UserRole.ADMIN, null), DEVELOPER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userDao, never()).updateRole(eq(TARGET_ID), org.mockito.ArgumentMatchers.any());
        verify(userDao, never()).updateActive(eq(TARGET_ID), org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    void updateUnknownUser_returns404() {
        when(userDao.findById(ADMIN_ID)).thenReturn(admin);
        when(userDao.findById(999L)).thenReturn(null);

        assertThatThrownBy(() -> userService.updateUser(
                999L, new UpdateUserRequest(UserRole.DEVELOPER, null), ADMIN_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userDao, never()).updateRole(eq(999L), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void adminChangesOwnRole_returns400() {
        when(userDao.findById(ADMIN_ID)).thenReturn(admin);

        assertThatThrownBy(() -> userService.updateUser(
                ADMIN_ID, new UpdateUserRequest(UserRole.DEVELOPER, null), ADMIN_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userDao, never()).updateRole(eq(ADMIN_ID), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void adminDeactivatesSelf_returns400() {
        when(userDao.findById(ADMIN_ID)).thenReturn(admin);

        assertThatThrownBy(() -> userService.updateUser(
                ADMIN_ID, new UpdateUserRequest(null, false), ADMIN_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userDao, never()).updateActive(eq(ADMIN_ID), org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    void adminSetsSameRoleOnSelf_isAllowed() {
        // ADMIN setzt seine eigene Rolle erneut auf ADMIN — no-op, kein Lock-out
        when(userDao.findById(ADMIN_ID)).thenReturn(admin);

        userService.updateUser(ADMIN_ID, new UpdateUserRequest(UserRole.ADMIN, null), ADMIN_ID);

        verify(userDao).updateRole(ADMIN_ID, UserRole.ADMIN);
    }

    @Test
    void emptyBody_returns400() {
        assertThatThrownBy(() -> userService.updateUser(
                TARGET_ID, new UpdateUserRequest(null, null), ADMIN_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private static User user(long id, String username, UserRole role, boolean active) {
        return new User(id, username, username + "@example.com", "hash",
                role, active, OffsetDateTime.now());
    }
}
