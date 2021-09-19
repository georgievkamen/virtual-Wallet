package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.CategoryRepository;
import com.team9.virtualwallet.services.contracts.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;


    @Autowired
    public CategoryServiceImpl(CategoryRepository repository) {

        this.repository = repository;
    }

    @Override
    public List<Category> getAll(User user) {
        return repository.getAll(user);
    }

    @Override
    public Category getById(User user, int id) {
        Category category = repository.getById(id);

        if (!user.isEmployee() && category.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException("You are not the owner of this category!");
        }

        return repository.getById(id);
    }

    @Override
    public void create(User user, Category category) {
        verifyNotDuplicate(user, category);

        repository.create(category);
    }

    @Override
    public void update(User user, Category category) {
        verifyOwnership(user, category, "You can only update your own categories!");
        verifyNotDuplicateUpdate(user, category);

        repository.update(category);
    }

    @Override
    public void delete(User user, int id) {
        Category category = repository.getById(id);
        verifyOwnership(user, category, "You can only delete your own categories!");

        if (!category.getTransactions().isEmpty()) {
            category.deleteTransactions();
        }

        repository.delete(category);
    }

    private void verifyNotDuplicate(User user, Category category) {
        if (repository.isDuplicate(user, category.getName())) {
            throw new DuplicateEntityException("Category", "name", category.getName());
        }
    }

    private void verifyNotDuplicateUpdate(User user, Category category) {
        Category categoryToEdit = repository.getById(category.getId());

        if (repository.isDuplicate(user, category.getName()) && !categoryToEdit.getName().equals(category.getName())) {
            throw new DuplicateEntityException("Category", "name", category.getName());
        }
    }

    private void verifyOwnership(User user, Category category, String message) {
        if (category.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException(message);
        }
    }


}
