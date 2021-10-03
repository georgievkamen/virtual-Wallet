package com.team9.virtualwallet.controllers.mvc;


import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.models.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

import static com.team9.virtualwallet.configs.ApplicationConstants.CURRENT_USER_SESSION_KEY;

public abstract class BaseAuthenticationController {

    private final AuthenticationHelper authenticationHelper;

    protected BaseAuthenticationController(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute(CURRENT_USER_SESSION_KEY) != null;
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

}
