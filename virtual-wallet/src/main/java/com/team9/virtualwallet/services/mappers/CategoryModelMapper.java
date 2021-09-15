package com.team9.virtualwallet.services.mappers;

import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.CategoryDto;
import com.team9.virtualwallet.repositories.contracts.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryModelMapper {

    private final CategoryRepository repository;

    public CategoryModelMapper(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category fromDto(User user, CategoryDto categoryDto) {
        Category category = new Category();

        category.setName(categoryDto.getName());
        category.setUser(user);

        return category;
    }

    public Category fromDto(CategoryDto categoryDto, int id) {
        Category category = repository.getById(id);

        category.setName(categoryDto.getName());

        return category;

    }

}
