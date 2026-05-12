package at.mci.bugtracker.service;

import at.mci.bugtracker.dao.ActivityDao;
import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.model.Activity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityDao activityDao;
    private final BugDao bugDao;

    public ActivityService(ActivityDao activityDao, BugDao bugDao) {
        this.activityDao = activityDao;
        this.bugDao = bugDao;
    }

    public List<Activity> getBugActivities(long bugId) {
        // Existenz-Check vorab — sonst würde der Endpoint bei nicht-existentem
        // Bug einfach eine leere Liste liefern, was 404-nicht-unterscheidbar wäre.
        if (bugDao.findById(bugId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug nicht gefunden");
        }
        return activityDao.findByBugId(bugId);
    }
}
