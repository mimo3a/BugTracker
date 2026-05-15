package at.mci.bugtracker.service;

import at.mci.bugtracker.model.BugStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

// Definiert erlaubte Status-Wechsel laut FA-06. Rückwärts-Wechsel sind im MVP
// nicht erlaubt (außer ARCHIVIERT → via T035 Restore zurück auf vorigen Status,
// das läuft NICHT durch diese State-Machine, sondern direkt im Soft-Delete-Pfad).
// Self-Transition (status == status) ist erlaubt — no-op, kein Fehler.
@Component
public class BugStatusStateMachine {

    private static final Map<BugStatus, Set<BugStatus>> TRANSITIONS;

    static {
        Map<BugStatus, Set<BugStatus>> t = new EnumMap<>(BugStatus.class);
        t.put(BugStatus.NEU, EnumSet.of(BugStatus.IN_BEARBEITUNG, BugStatus.ARCHIVIERT));
        t.put(BugStatus.IN_BEARBEITUNG, EnumSet.of(BugStatus.IM_REVIEW, BugStatus.ARCHIVIERT));
        t.put(BugStatus.IM_REVIEW, EnumSet.of(BugStatus.ERLEDIGT, BugStatus.ABGELEHNT, BugStatus.ARCHIVIERT));
        t.put(BugStatus.ERLEDIGT, EnumSet.of(BugStatus.ARCHIVIERT));
        t.put(BugStatus.ABGELEHNT, EnumSet.of(BugStatus.ARCHIVIERT));
        t.put(BugStatus.ARCHIVIERT, EnumSet.noneOf(BugStatus.class));
        TRANSITIONS = Map.copyOf(t);
    }

    public boolean canTransition(BugStatus from, BugStatus to) {
        if (from == to) return true;
        return TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }
}
