package at.mci.bugtracker.service;

import at.mci.bugtracker.dao.ActivityDao;
import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.dao.UserDao;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import at.mci.bugtracker.model.User;
import at.mci.bugtracker.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BugServicePriorityTest {

    @Mock private BugDao bugDao;
    @Mock private UserDao userDao;
    @Mock private ActivityDao activityDao;
    @Mock private BugStatusStateMachine statusStateMachine;

    @InjectMocks private BugService bugService;

    private static final long ACTOR_ID = 10L;
    private static final long BUG_ID = 42L;

    private Bug existingBug;
    private User developer;
    private User tester;

    @BeforeEach
    void setUp() {
        existingBug = bug(BUG_ID, BugPriority.MITTEL, false);
        developer = new User(ACTOR_ID, "dev", "dev@example.com", "hash",
                UserRole.DEVELOPER, true, OffsetDateTime.now());
        tester = new User(ACTOR_ID, "tester", "tester@example.com", "hash",
                UserRole.TESTER, true, OffsetDateTime.now());
    }

    @Test
    void developerChangesPriority_persistsAndLogsActivity() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));
        when(userDao.findById(ACTOR_ID)).thenReturn(developer);
        when(bugDao.update(any(Bug.class)))
                .thenAnswer(inv -> Optional.of(inv.<Bug>getArgument(0)));

        Bug result = bugService.updatePriority(BUG_ID, BugPriority.KRITISCH, ACTOR_ID);

        assertThat(result.priority()).isEqualTo(BugPriority.KRITISCH);

        ArgumentCaptor<Bug> bugCaptor = ArgumentCaptor.forClass(Bug.class);
        verify(bugDao).update(bugCaptor.capture());
        assertThat(bugCaptor.getValue().priority()).isEqualTo(BugPriority.KRITISCH);
        assertThat(bugCaptor.getValue().title()).isEqualTo(existingBug.title());

        verify(activityDao).insert(
                BUG_ID, ACTOR_ID, "UPDATED", "priority", "MITTEL", "KRITISCH"
        );
    }

    @Test
    void testerCannotChangePriority_returns403() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));
        when(userDao.findById(ACTOR_ID)).thenReturn(tester);

        assertThatThrownBy(() -> bugService.updatePriority(BUG_ID, BugPriority.HOCH, ACTOR_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(bugDao, never()).update(any());
        verifyNoInteractions(activityDao);
    }

    @Test
    void archivedBug_returns409() {
        Bug archivedBug = bug(BUG_ID, BugPriority.MITTEL, true);
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(archivedBug));

        assertThatThrownBy(() -> bugService.updatePriority(BUG_ID, BugPriority.HOCH, ACTOR_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bugDao, never()).update(any());
        verifyNoInteractions(activityDao);
    }

    @Test
    void unknownBug_returns404() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bugService.updatePriority(BUG_ID, BugPriority.HOCH, ACTOR_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(bugDao, never()).update(any());
        verifyNoInteractions(activityDao);
    }

    @Test
    void samePriority_doesNotLogActivity() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));
        when(userDao.findById(ACTOR_ID)).thenReturn(developer);
        when(bugDao.update(any(Bug.class)))
                .thenAnswer(inv -> Optional.of(inv.<Bug>getArgument(0)));

        bugService.updatePriority(BUG_ID, BugPriority.MITTEL, ACTOR_ID);

        verifyNoInteractions(activityDao);
    }

    private static Bug bug(long id, BugPriority priority, boolean archived) {
        return new Bug(
                id,
                "Login fails",
                "Steps to reproduce",
                BugStatus.NEU,
                priority,
                1L,
                "reporter",
                null,
                null,
                List.of(),
                List.of(),
                archived,
                LocalDateTime.of(2026, 5, 13, 10, 0),
                LocalDateTime.of(2026, 5, 13, 10, 0)
        );
    }
}
