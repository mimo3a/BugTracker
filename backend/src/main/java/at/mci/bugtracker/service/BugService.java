package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BugService {

    private final BugDao bugDao;

    public BugService(BugDao bugDao) {
        this.bugDao = bugDao;
    }

    public Bug createBug(CreateBugRequest request, long reporterId) {
        BugPriority priority = request.priority() != null ? request.priority() : BugPriority.MITTEL;

        Bug bug = new Bug(
                null,
                request.title(),
                request.description(),
                BugStatus.NEU,
                priority,
                reporterId,
                null,
                null,
                null,
                request.tagIds(),
                List.of(),
                false,
                null,
                null
        );
        return bugDao.save(bug);
    }
}
