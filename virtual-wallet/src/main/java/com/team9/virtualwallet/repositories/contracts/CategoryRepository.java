package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;

import java.util.List;

public interface CategoryRepository extends BaseRepository<Category> {

    List<Category> getAll(User user);

    boolean isDuplicate(User user, String name);

}
