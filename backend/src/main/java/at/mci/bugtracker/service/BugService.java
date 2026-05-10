package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.CreateBugRequest;
import at.mci.bugtracker.dao.ActivityDao;
import at.mci.bugtracker.dao.BugDao;
import at.mci.bugtracker.exception.EntityNotFoundException;
import at.mci.bugtracker.exception.InvalidStatusTransitionException;
import at.mci.bugtracker.model.Bug;
import at.mci.bugtracker.model.BugFilter;
import at.mci.bugtracker.model.BugPriority;
import at.mci.bugtracker.model.BugStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BugService {

    private final BugDao bugDao;
    private final ActivityDao activityDao;
    private final BugStatusStateMachine statusStateMachine;

    public BugService(BugDao bugDao, ActivityDao activityDao, BugStatusStateMachine statusStateMachine) {
        this.bugDao = bugDao;
        this.activityDao = activityDao;
        this.statusStateMachine = statusStateMachine;
    }

    public List<Bug> findAll(BugFilter filter, int page) {
        return bugDao.findAll(filter, page);
    }

    public long count(BugFilter filter) {
        return bugDao.count(filter);
    }

    public Bug findById(Long id) {
        return bugDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Bug not found"));
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

    @Transactional
    public Bug updateStatus(Long id, BugStatus targetStatus, long userId) {
        Bug current = findById(id);
        BugStatus currentStatus = current.status();

        if (!statusStateMachine.canTransition(currentStatus, targetStatus)) {
            throw new InvalidStatusTransitionException();
        }

        if (currentStatus == targetStatus) {
            return current;
        }

        Bug updated = bugDao.updateStatus(id, targetStatus)
                .orElseThrow(() -> new EntityNotFoundException("Bug not found"));
        activityDao.insertStatusChanged(id, userId, currentStatus.name(), targetStatus.name());
        return updated;
    }
}
