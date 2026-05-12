package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.UpdateBugRequest;
import at.mci.bugtracker.dao.ActivityDao;
import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.dao.UserDao;
import at.mci.bugtracker.model.Activity;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import at.mci.bugtracker.model.User;
import at.mci.bugtracker.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BugServiceTest {
    private static final long BUG_ID = 42L;
    private static final long ACTOR_ID = 2L;

    @Mock
    private BugDao bugDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ActivityDao activityDao;

    private BugService bugService;

    @BeforeEach
    void setUp() {
        bugService = new BugService(bugDao, userDao, activityDao);
        when(userDao.findById(ACTOR_ID)).thenReturn(user(ACTOR_ID, UserRole.DEVELOPER));
    }

    @Test
    void updateBugCreatesActivityForChangedTitle() {
        Bug existing = bug("Old title", BugStatus.NEU, BugPriority.MITTEL, null, List.of(1L));
        Bug saved = bug("New title", BugStatus.NEU, BugPriority.MITTEL, null, List.of(1L));
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existing));
        when(bugDao.update(any(Bug.class))).thenReturn(Optional.of(saved));

        bugService.updateBug(BUG_ID, new UpdateBugRequest("New title", existing.description(), null), ACTOR_ID);

        Activity activity = capturedActivity();
        assertThat(activity.bugId()).isEqualTo(BUG_ID);
        assertThat(activity.userId()).isEqualTo(ACTOR_ID);
        assertThat(activity.action()).isEqualTo("TITLE_CHANGED");
        assertThat(activity.field()).isEqualTo("title");
        assertThat(activity.oldValue()).isEqualTo("Old title");
        assertThat(activity.newValue()).isEqualTo("New title");
    }

    @Test
    void updateStatusCreatesActivityForStatusChange() {
        Bug existing = bug("Title", BugStatus.NEU, BugPriority.MITTEL, null, List.of());
        Bug saved = bug("Title", BugStatus.IN_BEARBEITUNG, BugPriority.MITTEL, null, List.of());
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existing));
        when(bugDao.update(any(Bug.class))).thenReturn(Optional.of(saved));

        bugService.updateStatus(BUG_ID, BugStatus.IN_BEARBEITUNG, ACTOR_ID);

        Activity activity = capturedActivity();
        assertThat(activity.action()).isEqualTo("STATUS_CHANGED");
        assertThat(activity.field()).isEqualTo("status");
        assertThat(activity.oldValue()).isEqualTo("NEU");
        assertThat(activity.newValue()).isEqualTo("IN_BEARBEITUNG");
    }

    @Test
    void updatePriorityCreatesActivityForPriorityChange() {
        Bug existing = bug("Title", BugStatus.NEU, BugPriority.MITTEL, null, List.of());
        Bug saved = bug("Title", BugStatus.NEU, BugPriority.KRITISCH, null, List.of());
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existing));
        when(bugDao.update(any(Bug.class))).thenReturn(Optional.of(saved));

        bugService.updatePriority(BUG_ID, BugPriority.KRITISCH, ACTOR_ID);

        Activity activity = capturedActivity();
        assertThat(activity.action()).isEqualTo("PRIORITY_CHANGED");
        assertThat(activity.field()).isEqualTo("priority");
        assertThat(activity.oldValue()).isEqualTo("MITTEL");
        assertThat(activity.newValue()).isEqualTo("KRITISCH");
    }

    @Test
    void updateAssigneeCreatesActivityForAssigneeChange() {
        Bug existing = bug("Title", BugStatus.NEU, BugPriority.MITTEL, null, List.of());
        Bug saved = bug("Title", BugStatus.NEU, BugPriority.MITTEL, 3L, List.of());
        when(userDao.findById(3L)).thenReturn(user(3L, UserRole.DEVELOPER));
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existing));
        when(bugDao.update(any(Bug.class))).thenReturn(Optional.of(saved));

        bugService.updateAssignee(BUG_ID, 3L, ACTOR_ID);

        Activity activity = capturedActivity();
        assertThat(activity.action()).isEqualTo("ASSIGNEE_CHANGED");
        assertThat(activity.field()).isEqualTo("assignee");
        assertThat(activity.oldValue()).isNull();
        assertThat(activity.newValue()).isEqualTo("3");
    }

    @Test
    void updateBugDoesNotCreateActivityWhenTrackedFieldsAreUnchanged() {
        Bug existing = bug("Title", BugStatus.NEU, BugPriority.MITTEL, null, List.of(1L));
        when(bugDao.findById(BUG_ID)).thenReturn(Optional.of(existing));
        when(bugDao.update(any(Bug.class))).thenReturn(Optional.of(existing));

        bugService.updateBug(BUG_ID, new UpdateBugRequest("Title", existing.description(), List.of(1L)), ACTOR_ID);

        verify(activityDao, never()).save(any(Activity.class));
    }

    private Activity capturedActivity() {
        ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(activityDao).save(captor.capture());
        return captor.getValue();
    }

    private static Bug bug(String title, BugStatus status, BugPriority priority, Long assigneeId, List<Long> tagIds) {
        return new Bug(
                BUG_ID,
                title,
                "Steps to reproduce",
                status,
                priority,
                1L,
                "tom",
                assigneeId,
                null,
                tagIds,
                List.of(),
                false,
                null,
                null
        );
    }

    private static User user(long id, UserRole role) {
        return new User(id, "user" + id, "user" + id + "@example.com", "hash", role, true, OffsetDateTime.now());
    }
}
