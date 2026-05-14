package at.mci.bugtracker.service;

import at.mci.bugtracker.model.BugStatus;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BugStatusStateMachineTest {

    private final BugStatusStateMachine sm = new BugStatusStateMachine();

    private static final Set<BugStatus> ACTIVE = EnumSet.complementOf(EnumSet.of(BugStatus.ARCHIVIERT));

    @Test
    void selfTransitionIsAlwaysAllowed() {
        for (BugStatus s : BugStatus.values()) {
            assertThat(sm.canTransition(s, s)).as("self-transition %s", s).isTrue();
        }
    }

    @Test
    void allActiveStatusesCanTransitionToEachOther() {
        for (BugStatus from : ACTIVE) {
            for (BugStatus to : ACTIVE) {
                assertThat(sm.canTransition(from, to))
                        .as("%s → %s should be allowed", from, to)
                        .isTrue();
            }
        }
    }

    @Test
    void anyActiveStatusCanBeArchived() {
        for (BugStatus from : ACTIVE) {
            assertThat(sm.canTransition(from, BugStatus.ARCHIVIERT))
                    .as("%s → ARCHIVIERT should be allowed", from)
                    .isTrue();
        }
    }

    @Test
    void archiviertIsTerminalViaStateMachine() {
        // Re-activation läuft über PATCH /restore (T035), nicht über diese State-Machine.
        for (BugStatus to : ACTIVE) {
            assertThat(sm.canTransition(BugStatus.ARCHIVIERT, to))
                    .as("ARCHIVIERT → %s should be blocked (use /restore)", to)
                    .isFalse();
        }
    }
}
