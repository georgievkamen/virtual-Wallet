package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.FailedToUploadFileException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.PasswordDto;
import com.team9.virtualwallet.models.dtos.UserDto;
import com.team9.virtualwallet.services.contracts.UserService;
import com.team9.virtualwallet.services.mappers.UserModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.team9.virtualwallet.configs.ApplicationConstants.CURRENT_USER_SESSION_KEY;

@Controller
public class ProfileMvcController {

    private final UserService service;
    private final UserModelMapper modelMapper;
    private final AuthenticationHelper authenticationHelper;

    public ProfileMvcController(UserService service, UserModelMapper modelMapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @ModelAttribute("currentLoggedUser")
    public String populateCurrentLoggedUser(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("currentLoggedUser", user);
            return "";
        } catch (AuthenticationFailureException e) {
            return "";
        }
    }

    @GetMapping("/panel/account-profile")
    public String showUserProfile(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            UserDto userDto = modelMapper.toUserDto(user);
            model.addAttribute("user", userDto);
            model.addAttribute("userId", user.getId());
            return "account-profile";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/panel/account-profile")
    public String updateProfile(@Valid @ModelAttribute("user") UserDto userDto,
                                BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "account-profile";
        }

        try {
            User userExecuting = authenticationHelper.tryGetUser(session);
            User user = modelMapper.fromUserDto(userExecuting.getId(), userDto);

            service.update(userExecuting, user, user.getId());
            return "redirect:/panel/account-profile";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (DuplicateEntityException e) {
            String field = e.getMessage().split(" ")[2];
            if (field.equalsIgnoreCase("username")) {
                result.rejectValue("username", "duplicate_username", e.getMessage());
            }
            if (field.equalsIgnoreCase("email")) {
                result.rejectValue("email", "duplicate_email", e.getMessage());
            }
            if (field.equalsIgnoreCase("phone")) {
                result.rejectValue("phoneNumber", "duplicate_phoneNumber", e.getMessage());
            }
            return "account-profile";
        }
    }

    @PostMapping("/panel/account-profile/image")
    public String updateProfilePhoto(@RequestParam(value = "fileImage", required = false) MultipartFile multipartFile, HttpSession session) {
        try {
            User userExecuting = authenticationHelper.tryGetUser(session);
            if (multipartFile.isEmpty()) {
                service.removeProfilePhoto(userExecuting);
            } else {
                service.updateProfilePhoto(userExecuting, multipartFile);
            }
            return "redirect:/panel/account-profile";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (FailedToUploadFileException e) {
            return "account-profile";
        }
    }


    @GetMapping("/panel/account-security")
    public String showUserSecurity(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
            model.addAttribute("userPassword", new PasswordDto());
            return "account-security";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/panel/account/delete")
    public String deleteAccount(HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);

            service.delete(user);
            session.removeAttribute(CURRENT_USER_SESSION_KEY);
            return "redirect:/";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }
}
