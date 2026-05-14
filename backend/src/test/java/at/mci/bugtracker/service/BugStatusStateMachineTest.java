package at.mci.bugtracker.service;

import at.mci.bugtracker.model.BugStatus;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BugStatusStateMachineTest {

    private final BugStatusStateMachine sm = new BugStatusStateMachine();

    @Test
    void allTransitionsMatchDocumentedPolicy() {
        for (BugStatus from : BugStatus.values()) {
            for (BugStatus to : BugStatus.values()) {
                boolean expected = from == to || allowedTargets(from).contains(to);

                assertThat(sm.canTransition(from, to))
                        .as("%s -> %s", from, to)
                        .isEqualTo(expected);
            }
        }
    }

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

    private static Set<BugStatus> allowedTargets(BugStatus from) {
        return switch (from) {
            case NEU -> EnumSet.of(BugStatus.IN_BEARBEITUNG, BugStatus.ARCHIVIERT);
            case IN_BEARBEITUNG -> EnumSet.of(BugStatus.IM_REVIEW, BugStatus.ARCHIVIERT);
            case IM_REVIEW -> EnumSet.of(BugStatus.ERLEDIGT, BugStatus.ABGELEHNT, BugStatus.ARCHIVIERT);
            case ERLEDIGT, ABGELEHNT -> EnumSet.of(BugStatus.ARCHIVIERT);
            case ARCHIVIERT -> EnumSet.noneOf(BugStatus.class);
        };
    }
}
