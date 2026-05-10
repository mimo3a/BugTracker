package at.mci.bugtracker.service;

import at.mci.bugtracker.model.BugStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BugStatusStateMachineTest {
    private final BugStatusStateMachine stateMachine = new BugStatusStateMachine();

    @Test
    void allowsDefinedWorkflowTransitionsAndArchivingFromEveryOpenStatus() {
        assertThat(stateMachine.canTransition(BugStatus.NEU, BugStatus.IN_BEARBEITUNG)).isTrue();
        assertThat(stateMachine.canTransition(BugStatus.IN_BEARBEITUNG, BugStatus.IM_REVIEW)).isTrue();
        assertThat(stateMachine.canTransition(BugStatus.IM_REVIEW, BugStatus.ERLEDIGT)).isTrue();
        assertThat(stateMachine.canTransition(BugStatus.IM_REVIEW, BugStatus.ABGELEHNT)).isTrue();

        assertThat(stateMachine.canTransition(BugStatus.NEU, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(stateMachine.canTransition(BugStatus.IN_BEARBEITUNG, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(stateMachine.canTransition(BugStatus.IM_REVIEW, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(stateMachine.canTransition(BugStatus.ERLEDIGT, BugStatus.ARCHIVIERT)).isTrue();
        assertThat(stateMachine.canTransition(BugStatus.ABGELEHNT, BugStatus.ARCHIVIERT)).isTrue();
    }

    @Test
    void rejectsUndefinedBackwardTransitions() {
        assertThat(stateMachine.canTransition(BugStatus.ERLEDIGT, BugStatus.NEU)).isFalse();
        assertThat(stateMachine.canTransition(BugStatus.NEU, BugStatus.IM_REVIEW)).isFalse();
        assertThat(stateMachine.canTransition(BugStatus.ARCHIVIERT, BugStatus.NEU)).isFalse();
    }
}
