package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.dao.ActivityDao;
import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.exception.InvalidStatusTransitionException;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BugServiceTest {
    private final BugDao bugDao = mock(BugDao.class);
    private final ActivityDao activityDao = mock(ActivityDao.class);
    private final BugService bugService = new BugService(
            bugDao,
            activityDao,
            new BugStatusStateMachine()
    );

    @Test
    void createBugUsesNewStatusByDefault() {
        CreateBugRequest request = new CreateBugRequest("Title", "Description", null, List.of());
        Bug saved = bug(1L, BugStatus.NEU);
        when(bugDao.save(org.mockito.ArgumentMatchers.any(Bug.class))).thenReturn(saved);

        Bug result = bugService.createBug(request, 7L);

        assertThat(result.status()).isEqualTo(BugStatus.NEU);
        verify(bugDao).save(org.mockito.ArgumentMatchers.argThat(b ->
                b.status() == BugStatus.NEU && b.reporterId() == 7L));
    }

    @Test
    void updateStatusAllowsTransitionAndCreatesActivity() {
        Bug current = bug(42L, BugStatus.NEU);
        Bug updated = bug(42L, BugStatus.IN_BEARBEITUNG);
        when(bugDao.findById(42L)).thenReturn(Optional.of(current));
        when(bugDao.updateStatus(42L, BugStatus.IN_BEARBEITUNG)).thenReturn(Optional.of(updated));

        Bug result = bugService.updateStatus(42L, BugStatus.IN_BEARBEITUNG, 2L);

        assertThat(result.status()).isEqualTo(BugStatus.IN_BEARBEITUNG);
        verify(activityDao).insertStatusChanged(42L, 2L, "NEU", "IN_BEARBEITUNG");
    }

    @Test
    void updateStatusRejectsInvalidTransitionWithoutActivity() {
        when(bugDao.findById(42L)).thenReturn(Optional.of(bug(42L, BugStatus.ERLEDIGT)));

        assertThatThrownBy(() -> bugService.updateStatus(42L, BugStatus.NEU, 2L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessage("Ungültiger Statuswechsel");

        verify(bugDao, never()).updateStatus(42L, BugStatus.NEU);
        verify(activityDao, never()).insertStatusChanged(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    private static Bug bug(Long id, BugStatus status) {
        return new Bug(
                id,
                "Title",
                "Description",
                status,
                BugPriority.MITTEL,
                1L,
                "marie",
                null,
                null,
                List.of(),
                List.of(),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
