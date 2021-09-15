package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.controllers.utils.AuthenticationHelper;
import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.CategoryDto;
import com.team9.virtualwallet.services.contracts.CategoryService;
import com.team9.virtualwallet.services.mappers.CategoryModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @PostMapping
    public Category create(@RequestHeader HttpHeaders headers, @RequestBody @Valid CategoryDto categoryDto) {
        User user = authenticationHelper.tryGetUser(headers);

        Category category = modelMapper.fromDto(user, categoryDto);
        service.create(user, category);

        return category;
    }

    @PostMapping("/{id}")
    public Category update(@RequestHeader HttpHeaders headers, @RequestBody @Valid CategoryDto categoryDto, @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);

        Category category = modelMapper.fromDto(categoryDto, id);
        service.update(user, category);
        return category;
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

}
