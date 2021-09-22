package com.team9.virtualwallet.controllers.utils;

import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

import static com.team9.virtualwallet.config.ApplicationConstants.CURRENT_USER_SESSION_KEY;

@Component
public class AuthenticationHelper {

    public static final String AUTHORIZATION_HEADER_USERNAME = "Username";
    public static final String AUTHORIZATION_HEADER_PASSWORD = "Password";
    public static final String AUTHENTICATION_FAILURE_MESSAGE = "Wrong username or password.";

    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_USERNAME) || !headers.containsKey(AUTHORIZATION_HEADER_PASSWORD)) {
            throw new UnauthorizedOperationException("The requested resource requires authentication!");
        }

        String username = headers.getFirst(AUTHORIZATION_HEADER_USERNAME);
        String password = headers.getFirst(AUTHORIZATION_HEADER_PASSWORD);
        User user = userService.getByUsername(username);

        if (!user.getPassword().equals(password)) {
            throw new AuthenticationFailureException(AUTHENTICATION_FAILURE_MESSAGE);
        }

        if (user.isDeleted()) {
            throw new UnauthorizedOperationException("You have deleted your account!");
        }

        return user;
    }

    public User tryGetUser(HttpSession session) {
        if (session.getAttribute(CURRENT_USER_SESSION_KEY) == null) {
            throw new AuthenticationFailureException("No user logged in!");
        }

        try {
            String currentUser = (String) session.getAttribute("currentUser");
            return userService.getByUsername(currentUser);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException("No such user.");
        }
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = userService.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new AuthenticationFailureException(AUTHENTICATION_FAILURE_MESSAGE);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException(AUTHENTICATION_FAILURE_MESSAGE);

        }
    }
}
