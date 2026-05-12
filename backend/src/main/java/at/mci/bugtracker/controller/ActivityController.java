package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.controller.dto.ActivityResponse;
import at.mci.bugtracker.model.Activity;
import at.mci.bugtracker.service.ActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/api/bugs/{bugId}/activities")
    public List<ActivityResponse> getBugActivities(@PathVariable long bugId) {
        CurrentSession.require();
        return activityService.getBugActivities(bugId).stream()
                .map(ActivityController::toResponse)
                .toList();
    }

    private static ActivityResponse toResponse(Activity a) {
        return new ActivityResponse(
                a.id(),
                a.bugId(),
                a.userId(),
                a.userName(),
                a.action(),
                a.field(),
                a.oldValue(),
                a.newValue(),
                a.createdAt()
        );
    }
}
