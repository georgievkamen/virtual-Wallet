package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.User;

import java.util.List;

public interface UserService {
    User getById(User user, int id);

    public List<User> getAll(User user);

    void create(User user);
}
