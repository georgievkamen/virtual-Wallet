package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends BaseRepository<Category> {

    List<Category> getAll(User user);

    boolean isDuplicate(User user, String name);

    Object calculateSpendings(Category category, Optional<Date> startDate, Optional<Date> endDate);

}