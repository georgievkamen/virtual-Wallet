package com.team9.virtualwallet.controllers.mvc;

import com.team9.virtualwallet.controllers.AuthenticationHelper;
import com.team9.virtualwallet.exceptions.AuthenticationFailureException;
import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.CategoryDto;
import com.team9.virtualwallet.services.contracts.CategoryService;
import com.team9.virtualwallet.services.contracts.UserService;
import com.team9.virtualwallet.services.mappers.CategoryModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/panel/categories")
public class CategoriesMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final CategoryService service;
    private final CategoryModelMapper modelMapper;

    public CategoriesMvcController(AuthenticationHelper authenticationHelper,
                                   UserService userService,
                                   CategoryService service,
                                   CategoryModelMapper modelMapper) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.service = service;
        this.modelMapper = modelMapper;
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
    public String showCategoriesPage(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            List<Category> categories = service.getAll(user);
            model.addAttribute("categories", categories);
            model.addAttribute("categoriesExist", !categories.isEmpty());
            return "categories";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/add")
    public String showAddCategoryPage(HttpSession session, Model model) {
        try {
            authenticationHelper.tryGetUser(session);
            model.addAttribute("category", new CategoryDto());
            return "category-add";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute("category") @Valid CategoryDto categoryDto,
                              BindingResult result,
                              HttpSession session, Model model) {
        if (result.hasErrors()) {
            return "category-add";
        }

        try {
            User user = authenticationHelper.tryGetUser(session);
            Category category = modelMapper.fromDto(user, categoryDto);
            service.create(user, category);
            return "redirect:/panel/categories";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (DuplicateEntityException e) {
            result.rejectValue("name", "duplicate_name", e.getMessage());
            return "category-add";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditCategoryPage(HttpSession session, Model model, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            Category category = service.getById(user, id);
            CategoryDto categoryDto = modelMapper.toDto(category);
            model.addAttribute("categoryId", id);
            model.addAttribute("category", categoryDto);
            return "category-update";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/categories";
        }
    }

    @PostMapping("/{id}/update")
    public String editCategory(@Valid @ModelAttribute("category")
                                     CategoryDto categoryDto,
                             BindingResult result,
                             HttpSession session, Model model, @PathVariable int id) {
        if (result.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "category-update";
        }

        try {
            User user = authenticationHelper.tryGetUser(session);
            Category category = modelMapper.fromDto(categoryDto, id);
            service.update(user, category);
            return "redirect:/panel/categories";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/categories";
        } catch (DuplicateEntityException e) {
            result.rejectValue("name", "duplicate_name", e.getMessage());
            return "category-update";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteCategory(HttpSession session, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            service.delete(user, id);
            return "redirect:/panel/categories";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        } catch (UnauthorizedOperationException e) {
            return "redirect:/panel/wallets";
        }
    }

    @GetMapping("/reports")
    public String showSpendingsPage(HttpSession session, Model model,
                                    @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> startDate,
                                    @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> endDate) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            List<Category> categories = service.getAll(user);
            if (startDate.isPresent()) {
                model.addAttribute("startDate", startDate.get());
            }
            if (endDate.isPresent()) {
                model.addAttribute("endDate", endDate.get());
            }
            model.addAttribute("service", service);
            model.addAttribute("categories", categories);
            model.addAttribute("categoriesExist", !categories.isEmpty());
            return "categories-reports";
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
    }


}