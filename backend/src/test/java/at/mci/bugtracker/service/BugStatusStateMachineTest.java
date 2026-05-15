package at.mci.bugtracker.service;

import at.mci.bugtracker.model.BugStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BugStatusStateMachineTest {

    private final BugStatusStateMachine sm = new BugStatusStateMachine();

    @Test
    void selfTransitionIsAlwaysAllowed() {
        for (BugStatus s : BugStatus.values()) {
            assertThat(sm.canTransition(s, s)).as("self-transition %s", s).isTrue();
        }
    }

    @Test
    void neuCanGoToInBearbeitungOrArchiviert() {
        assertThat(sm.canTransition(BugStatus.NEU, BugStatus.IN_BEARBEITUNG)).isTrue();
        assertThat(sm.canTransition(BugStatus.NEU, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(sm.canTransition(BugStatus.NEU, BugStatus.IM_REVIEW)).isFalse();
        assertThat(sm.canTransition(BugStatus.NEU, BugStatus.ERLEDIGT)).isFalse();
    }

    @Test
    void inBearbeitungCanGoToImReviewOrArchiviert() {
        assertThat(sm.canTransition(BugStatus.IN_BEARBEITUNG, BugStatus.IM_REVIEW)).isTrue();
        assertThat(sm.canTransition(BugStatus.IN_BEARBEITUNG, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(sm.canTransition(BugStatus.IN_BEARBEITUNG, BugStatus.NEU)).isFalse();
        assertThat(sm.canTransition(BugStatus.IN_BEARBEITUNG, BugStatus.ERLEDIGT)).isFalse();
    }

    @Test
    void imReviewCanGoToErledigtAbgelehntOrArchiviert() {
        assertThat(sm.canTransition(BugStatus.IM_REVIEW, BugStatus.ERLEDIGT)).isTrue();
        assertThat(sm.canTransition(BugStatus.IM_REVIEW, BugStatus.ABGELEHNT)).isTrue();
        assertThat(sm.canTransition(BugStatus.IM_REVIEW, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(sm.canTransition(BugStatus.IM_REVIEW, BugStatus.IN_BEARBEITUNG)).isFalse();
    }

    @Test
    void erledigtAndAbgelehntCanOnlyArchive() {
        assertThat(sm.canTransition(BugStatus.ERLEDIGT, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(sm.canTransition(BugStatus.ERLEDIGT, BugStatus.NEU)).isFalse();
        assertThat(sm.canTransition(BugStatus.ABGELEHNT, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(sm.canTransition(BugStatus.ABGELEHNT, BugStatus.NEU)).isFalse();
    }

    @Test
    void archiviertIsTerminal() {
        for (BugStatus s : BugStatus.values()) {
            if (s == BugStatus.ARCHIVIERT) continue;
            assertThat(sm.canTransition(BugStatus.ARCHIVIERT, s))
                    .as("ARCHIVIERT → %s should be blocked", s)
                    .isFalse();
        }
    }
}
