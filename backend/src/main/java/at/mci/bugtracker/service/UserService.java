package at.mci.bugtracker.service;

import at.mci.bugtracker.controller.dto.UpdateUserRequest;
import at.mci.bugtracker.dao.UserDao;
import at.mci.bugtracker.model.User;
import at.mci.bugtracker.model.UserRole;
import at.mci.bugtracker.model.UserWithoutHash;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<UserWithoutHash> listUsers() {
        return userDao.findAll().stream().map(UserService::toUserWithoutHash).toList();
    }

    @Transactional
    public UserWithoutHash updateUser(long targetId, UpdateUserRequest request, long actorId) {
        if (request == null || (request.role() == null && request.active() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mindestens 'role' oder 'active' muss gesetzt sein");
        }

        User actor = userDao.findById(actorId);
        if (actor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session ungültig");
        }
        if (actor.role() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nur ADMIN darf User verwalten");
        }

        User target = userDao.findById(targetId);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User nicht gefunden");
        }

        // FA-15 Lock-out-Schutz: ADMIN darf weder die eigene Rolle ändern noch
        // sich selbst deaktivieren — sonst gäbe es u.U. gar keinen ADMIN mehr.
        if (target.id() == actor.id()) {
            if (request.role() != null && request.role() != actor.role()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ADMIN darf eigene Rolle nicht ändern");
            }
            if (request.active() != null && !request.active()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ADMIN darf sich nicht selbst deaktivieren");
            }
        }

        if (request.role() != null) {
            userDao.updateRole(target.id(), request.role());
        }
        if (request.active() != null) {
            userDao.updateActive(target.id(), request.active());
        }

        User updated = userDao.findById(target.id());
        return toUserWithoutHash(updated);
    }

    private static UserWithoutHash toUserWithoutHash(User u) {
        return new UserWithoutHash(u.id(), u.username(), u.email(), u.role(), u.active(), u.createdAt());
    }
}
