package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService service;

    @Autowired
    public UserRestController(AuthenticationHelper authenticationHelper, UserService service) {
        this.authenticationHelper = authenticationHelper;
        this.service = service;
    }

    @GetMapping("/{id}/block")
    public User blockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        service.blockUser(userExecuting, id);
        return service.getById(userExecuting, id);
    }

    @GetMapping("/{id}/unblock")
    public User unblockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        service.unblockUser(userExecuting, id);
        return service.getById(userExecuting, id);
    }
}
