package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.CategoryDto;
import com.team9.virtualwallet.services.contracts.CategoryService;
import com.team9.virtualwallet.services.mappers.CategoryModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.team9.virtualwallet.config.RestResponseEntityExceptionHandler.checkFields;

@RestController
@RequestMapping("/api/users/categories")
public class CategoryRestController {

    private final CategoryService service;
    private final AuthenticationHelper authenticationHelper;
    private final CategoryModelMapper modelMapper;

    @Autowired
    public CategoryRestController(CategoryService service,
                                  AuthenticationHelper authenticationHelper,
                                  CategoryModelMapper modelMapper) {
        this.service = service;
        this.authenticationHelper = authenticationHelper;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<Category> getAll(@RequestHeader HttpHeaders headers) {
        User user = authenticationHelper.tryGetUser(headers);

        return service.getAll(user);
    }

    @GetMapping("/{id}")
    public Category getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);

        return service.getById(user, id);
    }

    @PostMapping
    public Category create(@RequestHeader HttpHeaders headers, @RequestBody @Valid CategoryDto categoryDto, BindingResult result) {
        checkFields(result);

        User user = authenticationHelper.tryGetUser(headers);

        Category category = modelMapper.fromDto(user, categoryDto);
        service.create(user, category);

        return category;
    }

    @PostMapping("/{id}")
    public Category update(@RequestHeader HttpHeaders headers, @RequestBody @Valid CategoryDto categoryDto, @PathVariable int id, BindingResult result) {
        checkFields(result);

        User user = authenticationHelper.tryGetUser(headers);

        Category category = modelMapper.fromDto(categoryDto, id);
        service.update(user, category);
        return category;
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);

        service.delete(user, id);
    }

}
