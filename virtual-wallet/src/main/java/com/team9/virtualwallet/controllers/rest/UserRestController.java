package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService service;

    @Autowired
    public UserRestController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public void create() {
        service.test();
    }
}
