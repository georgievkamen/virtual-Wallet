package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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

    @GetMapping()
    public List<User> getAll(@RequestHeader HttpHeaders headers,
                             @PageableDefault(page = 1) Pageable pageable) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        return service.getAll(userExecuting, pageable);
    }

    @DeleteMapping
    public void delete(@RequestHeader HttpHeaders headers) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        service.delete(userExecuting);
    }

    @GetMapping("/search/field")
    public User find(@RequestHeader HttpHeaders headers,
                     @RequestParam String fieldName,
                     @RequestParam String searchTerm) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        return service.getByField(userExecuting, fieldName, searchTerm);
    }

    @GetMapping("/filter")
    public List<User> filter(@RequestHeader HttpHeaders headers,
                             @PageableDefault(page = 1) Pageable pageable,
                             @RequestParam(required = false)
                                     Optional<String> username,
                             Optional<String> phoneNumber,
                             Optional<String> email) {

        User user = authenticationHelper.tryGetUser(headers);

        return service.filter(user, username, phoneNumber, email, pageable);
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

    @GetMapping("/contacts")
    public List<User> getContacts(@RequestHeader HttpHeaders headers) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        return service.getContacts(userExecuting);
    }

    @DeleteMapping("/contacts")
    public void removeContact(@RequestHeader HttpHeaders headers, @RequestParam int contactId) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        service.removeContact(userExecuting, contactId);
    }

    @PostMapping("/contacts")
    public void addContact(@RequestHeader HttpHeaders headers, @RequestParam int contactId) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        service.addContact(userExecuting, contactId);
    }

}
