package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;

import java.util.List;

public interface CategoryService {
    List<Category> getAll(User user);

    Category getById(User user, int id);

    void create(User user, Category category);

    void update(User user, Category category);

    void delete(User user, int id);
}
