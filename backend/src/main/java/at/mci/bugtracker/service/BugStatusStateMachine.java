package at.mci.bugtracker.service;

import at.mci.bugtracker.model.BugStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class BugStatusStateMachine {
    private final Map<BugStatus, Set<BugStatus>> transitions = new EnumMap<>(BugStatus.class);

    public BugStatusStateMachine() {
        transitions.put(BugStatus.NEU, EnumSet.of(BugStatus.IN_BEARBEITUNG, BugStatus.ARCHIVIERT));
        transitions.put(BugStatus.IN_BEARBEITUNG, EnumSet.of(BugStatus.IM_REVIEW, BugStatus.ARCHIVIERT));
        transitions.put(BugStatus.IM_REVIEW, EnumSet.of(BugStatus.ERLEDIGT, BugStatus.ABGELEHNT, BugStatus.ARCHIVIERT));
        transitions.put(BugStatus.ERLEDIGT, EnumSet.of(BugStatus.ARCHIVIERT));
        transitions.put(BugStatus.ABGELEHNT, EnumSet.of(BugStatus.ARCHIVIERT));
        transitions.put(BugStatus.ARCHIVIERT, EnumSet.noneOf(BugStatus.class));
    }

    public boolean canTransition(BugStatus from, BugStatus to) {
        if (from == to) {
            return true;
        }
        return transitions.getOrDefault(from, Set.of()).contains(to);
    }
}
