package at.mci.bugtracker.controller;

import at.mci.bugtracker.controller.dto.ActivityResponse;
import at.mci.bugtracker.model.Activity;
import at.mci.bugtracker.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bugs/{bugId}/activities")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getBugActivities(@PathVariable Long bugId) {
        List<ActivityResponse> response = activityService.getBugActivities(bugId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private ActivityResponse toResponse(Activity activity) {
        return new ActivityResponse(
                activity.id(),
                activity.bugId(),
                activity.userId(),
                activity.userName(),
                activity.action(),
                activity.field(),
                activity.oldValue(),
                activity.newValue(),
                activity.createdAt()
        );
    }
}
