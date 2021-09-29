package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/panel/admin/users")
public class UserMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService service;

    public UserMvcController(AuthenticationHelper authenticationHelper, UserService service) {
        this.authenticationHelper = authenticationHelper;
        this.service = service;
    }

    @ModelAttribute("currentLoggedUser")
    public String populateCurrentLoggedUser(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("currentLoggedUser", user);
            return "";
        } catch (AuthenticationFailureException e) {
            return "/auth/login";
        }
    }

    @GetMapping
    public String showUsersPage(HttpSession session, Model model,
                                @PageableDefault(page = 1) Pageable pageable,
                                @RequestParam(name = "username", required = false) Optional<String> username,
                                @RequestParam(name = "phoneNumber", required = false) Optional<String> phoneNumber,
                                @RequestParam(name = "email", required = false) Optional<String> email) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Pages<User> users;
            if (username.isEmpty() && phoneNumber.isEmpty() && email.isEmpty()) {
                users = service.getAll(user, pageable);
            } else {
                users = service.filter(user, username, phoneNumber, email, pageable);
            }
            model.addAttribute("users", users.getContent());
            model.addAttribute("usersExist", !users.getContent().isEmpty());
            model.addAttribute("pagination", users);
            return "users";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/{id}/employee")
    public String makeEmployee(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.makeEmployee(user, id);
            return "redirect:/panel/admin/users";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/{id}/employee/remove")
    public String removeEmployee(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.removeEmployee(user, id);
            return "redirect:/panel/admin/users";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/{id}/unblock")
    public String unblockUser(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.unblockUser(user, id);
            return "redirect:/panel/admin/users";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel";
        }
    }

    @GetMapping("/{id}/block")
    public String blockUser(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.blockUser(user, id);
            return "redirect:/panel/admin/users";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel";
        }
    }

}
