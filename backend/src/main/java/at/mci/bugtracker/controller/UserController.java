package at.mci.bugtracker.controller;

import at.mci.bugtracker.auth.CurrentSession;
import at.mci.bugtracker.auth.SessionStore;
import at.mci.bugtracker.controller.dto.UpdateUserRequest;
import at.mci.bugtracker.model.UserWithoutHash;
import at.mci.bugtracker.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserWithoutHash> listUsers() {
        CurrentSession.require();
        return userService.listUsers();
    }

    @PatchMapping("/{id}")
    public UserWithoutHash updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        SessionStore.Session session = CurrentSession.require();
        return userService.updateUser(id, request, session.userId());
    }
}
