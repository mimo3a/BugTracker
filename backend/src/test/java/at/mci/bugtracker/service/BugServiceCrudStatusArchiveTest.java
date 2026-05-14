package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.controller.dto.UpdateBugRequest;
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
class BugServiceCrudStatusArchiveTest {

    @Mock private BugDao bugDao;
    @Mock private UserDao userDao;
    @Mock private ActivityDao activityDao;
    @Mock private BugStatusStateMachine statusStateMachine;

    @InjectMocks private BugService bugService;

    private static final long BUG_ID = 42L;
    private static final long REPORTER_ID = 5L;
    private static final long DEVELOPER_ID = 7L;
    private static final long TESTER_ID = 9L;

    private User reporter;
    private User otherTester;
    private User developer;
    private Bug existingBug;

    @BeforeEach
    void setUp() {
        reporter = user(REPORTER_ID, "reporter", UserRole.TESTER);
        otherTester = user(TESTER_ID, "other-tester", UserRole.TESTER);
        developer = user(DEVELOPER_ID, "developer", UserRole.DEVELOPER);
        existingBug = bug(BUG_ID, "Login fails", "Steps", BugStatus.NEU, BugPriority.HOCH,
                REPORTER_ID, null, false, List.of(1L));
    }

    @Test
    void createBugDefaultsStatusAndPriority_persistsAndLogsActivity() {
        CreateBugRequest request = new CreateBugRequest("Login button broken", "Steps", null, List.of(1L, 2L));
        when(bugDao.save(any(Bug.class))).thenAnswer(inv -> {
            Bug b = inv.getArgument(0);
            return new Bug(
                    BUG_ID,
                    b.title(),
                    b.description(),
                    b.status(),
                    b.priority(),
                    b.reporterId(),
                    "reporter",
                    b.assigneeId(),
                    b.assigneeName(),
                    b.tagIds(),
                    List.of("Backend", "Frontend"),
                    b.archived(),
                    LocalDateTime.of(2026, 5, 14, 10, 0),
                    LocalDateTime.of(2026, 5, 14, 10, 0)
            );
        });

        Bug result = bugService.createBug(request, REPORTER_ID);

        assertThat(result.id()).isEqualTo(BUG_ID);
        assertThat(result.status()).isEqualTo(BugStatus.NEU);
        assertThat(result.priority()).isEqualTo(BugPriority.MITTEL);
        assertThat(result.reporterId()).isEqualTo(REPORTER_ID);
        assertThat(result.assigneeId()).isNull();
        assertThat(result.archived()).isFalse();
        assertThat(result.tagIds()).containsExactly(1L, 2L);

        ArgumentCaptor<Bug> bugCaptor = ArgumentCaptor.forClass(Bug.class);
        verify(bugDao).save(bugCaptor.capture());
        assertThat(bugCaptor.getValue().id()).isNull();
        assertThat(bugCaptor.getValue().status()).isEqualTo(BugStatus.NEU);
        assertThat(bugCaptor.getValue().priority()).isEqualTo(BugPriority.MITTEL);

        verify(activityDao).insert(BUG_ID, REPORTER_ID, "CREATED", null, null, "Login button broken");
    }

    @Test
    void updateBugByReporter_updatesContentAndTagsButKeepsWorkflowFields() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));
        when(userDao.findById(REPORTER_ID)).thenReturn(reporter);
        when(bugDao.update(any(Bug.class))).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        Bug result = bugService.updateBug(
                BUG_ID,
                new UpdateBugRequest("Login fixed title", "Updated steps", List.of(2L, 3L)),
                REPORTER_ID
        );

        assertThat(result.title()).isEqualTo("Login fixed title");
        assertThat(result.description()).isEqualTo("Updated steps");
        assertThat(result.tagIds()).containsExactly(2L, 3L);
        assertThat(result.status()).isEqualTo(existingBug.status());
        assertThat(result.priority()).isEqualTo(existingBug.priority());
        assertThat(result.assigneeId()).isEqualTo(existingBug.assigneeId());

        verify(activityDao).insert(BUG_ID, REPORTER_ID, "UPDATED", "title", "Login fails", "Login fixed title");
        verify(activityDao).insert(BUG_ID, REPORTER_ID, "UPDATED", "description", "Steps", "Updated steps");
        verify(activityDao).insert(BUG_ID, REPORTER_ID, "UPDATED", "tagIds", "[1]", "[2, 3]");
    }

    @Test
    void updateBugByOtherTester_returns403() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));
        when(userDao.findById(TESTER_ID)).thenReturn(otherTester);

        assertThatThrownBy(() -> bugService.updateBug(
                BUG_ID,
                new UpdateBugRequest("Nope", "Nope", List.of()),
                TESTER_ID
        ))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(bugDao, never()).update(any());
        verifyNoInteractions(activityDao);
    }

    @Test
    void updateBugOnArchivedBug_returns409() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(archivedBug()));

        assertThatThrownBy(() -> bugService.updateBug(
                BUG_ID,
                new UpdateBugRequest("Nope", "Nope", List.of()),
                DEVELOPER_ID
        ))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bugDao, never()).update(any());
        verifyNoInteractions(activityDao);
    }

    @Test
    void updateStatusAllowedTransition_persistsAndLogsActivity() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));
        when(userDao.findById(DEVELOPER_ID)).thenReturn(developer);
        when(statusStateMachine.canTransition(BugStatus.NEU, BugStatus.IN_BEARBEITUNG)).thenReturn(true);
        when(bugDao.update(any(Bug.class))).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        Bug result = bugService.updateStatus(BUG_ID, BugStatus.IN_BEARBEITUNG, DEVELOPER_ID);

        assertThat(result.status()).isEqualTo(BugStatus.IN_BEARBEITUNG);

        ArgumentCaptor<Bug> bugCaptor = ArgumentCaptor.forClass(Bug.class);
        verify(bugDao).update(bugCaptor.capture());
        assertThat(bugCaptor.getValue().title()).isEqualTo(existingBug.title());
        assertThat(bugCaptor.getValue().priority()).isEqualTo(existingBug.priority());
        assertThat(bugCaptor.getValue().tagIds()).containsExactlyElementsOf(existingBug.tagIds());

        verify(activityDao).insert(BUG_ID, DEVELOPER_ID, "UPDATED", "status", "NEU", "IN_BEARBEITUNG");
    }

    @Test
    void updateStatusForbiddenTransition_returns409AndDoesNotPersist() {
        Bug doneBug = bug(BUG_ID, "Done", "Steps", BugStatus.ERLEDIGT, BugPriority.MITTEL,
                REPORTER_ID, null, false, List.of());
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(doneBug));
        when(userDao.findById(DEVELOPER_ID)).thenReturn(developer);
        when(statusStateMachine.canTransition(BugStatus.ERLEDIGT, BugStatus.NEU)).thenReturn(false);

        assertThatThrownBy(() -> bugService.updateStatus(BUG_ID, BugStatus.NEU, DEVELOPER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bugDao, never()).update(any());
        verifyNoInteractions(activityDao);
    }

    @Test
    void updateStatusByTester_returns403() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));
        when(userDao.findById(TESTER_ID)).thenReturn(otherTester);

        assertThatThrownBy(() -> bugService.updateStatus(BUG_ID, BugStatus.IN_BEARBEITUNG, TESTER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verifyNoInteractions(statusStateMachine);
        verify(bugDao, never()).update(any());
        verifyNoInteractions(activityDao);
    }

    @Test
    void archiveBugSoftDeletesAndLogsActivity() {
        Bug archived = archivedBug();
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug), Optional.of(archived));
        when(userDao.findById(DEVELOPER_ID)).thenReturn(developer);

        Bug result = bugService.archiveBug(BUG_ID, DEVELOPER_ID);

        assertThat(result.archived()).isTrue();
        assertThat(result.status()).isEqualTo(BugStatus.ARCHIVIERT);
        verify(bugDao).archive(BUG_ID);
        verify(activityDao).insert(BUG_ID, DEVELOPER_ID, "UPDATED", "archived", "false", "true");
    }

    @Test
    void archiveAlreadyArchivedBug_returns409() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(archivedBug()));

        assertThatThrownBy(() -> bugService.archiveBug(BUG_ID, DEVELOPER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bugDao, never()).archive(BUG_ID);
        verifyNoInteractions(activityDao);
    }

    @Test
    void restoreBugReactivatesToNeuAndLogsActivity() {
        Bug archived = archivedBug();
        Bug restored = bug(BUG_ID, "Archived", "Steps", BugStatus.NEU, BugPriority.MITTEL,
                REPORTER_ID, null, false, List.of());
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(archived), Optional.of(restored));
        when(userDao.findById(DEVELOPER_ID)).thenReturn(developer);

        Bug result = bugService.restoreBug(BUG_ID, DEVELOPER_ID);

        assertThat(result.archived()).isFalse();
        assertThat(result.status()).isEqualTo(BugStatus.NEU);
        verify(bugDao).restore(BUG_ID);
        verify(activityDao).insert(BUG_ID, DEVELOPER_ID, "UPDATED", "archived", "true", "false");
    }

    @Test
    void restoreActiveBug_returns409() {
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existingBug));

        assertThatThrownBy(() -> bugService.restoreBug(BUG_ID, DEVELOPER_ID))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bugDao, never()).restore(BUG_ID);
        verifyNoInteractions(activityDao);
    }

    private Bug archivedBug() {
        return bug(BUG_ID, "Archived", "Steps", BugStatus.ARCHIVIERT, BugPriority.MITTEL,
                REPORTER_ID, null, true, List.of());
    }

    private static User user(long id, String username, UserRole role) {
        return new User(id, username, username + "@example.com", "hash", role, true, OffsetDateTime.now());
    }

    private static Bug bug(
            Long id,
            String title,
            String description,
            BugStatus status,
            BugPriority priority,
            long reporterId,
            Long assigneeId,
            boolean archived,
            List<Long> tagIds
    ) {
        return new Bug(
                id,
                title,
                description,
                status,
                priority,
                reporterId,
                "reporter",
                assigneeId,
                assigneeId == null ? null : "assignee",
                tagIds,
                List.of(),
                archived,
                LocalDateTime.of(2026, 5, 14, 9, 0),
                LocalDateTime.of(2026, 5, 14, 9, 0)
        );
    }
}
