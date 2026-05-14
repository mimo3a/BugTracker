package at.mci.bugtracker.service;

import at.mci.bugtracker.model.BugStatus;
import org.springframework.stereotype.Component;

// Regelt erlaubte Status-Wechsel (FA-06).
//
// Aktuelle Policy (gelockert nach Integration-Review 2026-05-14):
//   • Zwischen allen aktiven Status (NEU, IN_BEARBEITUNG, IM_REVIEW, ERLEDIGT, ABGELEHNT)
//     sind Wechsel in beide Richtungen frei. Begründung: Fehlklicks beim Inline-Edit
//     müssen ohne Admin-Eingriff korrigierbar sein.
//   • ARCHIVIERT ist über die State-Machine ein einseitiger Endzustand.
//     Re-Aktivierung läuft NICHT hier durch, sondern über den dedizierten
//     PATCH /api/bugs/{id}/restore-Endpoint (T035).
//   • Self-Transition (status == status) ist als No-Op erlaubt.
@Component
public class BugStatusStateMachine {

    public boolean canTransition(BugStatus from, BugStatus to) {
        if (from == to) return true;
        if (from == BugStatus.ARCHIVIERT) return false;
        return true;
    }
}
