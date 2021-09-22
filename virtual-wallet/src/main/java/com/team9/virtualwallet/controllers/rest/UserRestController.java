package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.team9.virtualwallet.controllers.utils.PagingHelper.getPage;


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
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int size) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        return getPage(service.getAll(userExecuting), page, size);
    }

    @DeleteMapping
    public void delete(@RequestHeader HttpHeaders headers) {
        User userExecuting = authenticationHelper.tryGetUser(headers);

        service.delete(userExecuting);
    }

    @GetMapping("/search")
    public List<User> search(@RequestHeader HttpHeaders headers,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int size,
                             @RequestParam String searchTerm) {

        User user = authenticationHelper.tryGetUser(headers);

        return getPage(service.search(user, searchTerm), page, size);
    }

    @GetMapping("/filter")
    public List<User> filter(@RequestHeader HttpHeaders headers,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int size,
                             @RequestParam(required = false)
                                     Optional<String> username,
                             Optional<String> phoneNumber,
                             Optional<String> email) {

        User user = authenticationHelper.tryGetUser(headers);

        return getPage(service.filter(user, username, phoneNumber, email), page, size);
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
