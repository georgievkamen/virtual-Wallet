package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public static final String UNAUTHORIZED_ACTION = "Only %s can %s %s!";

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.repository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<User> getAll(User user) {
        if (!user.isEmployee(user)) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "view all", "users"));
        }
        return repository.getAll();
    }

    @Override
    public User getById(User user, int id) {
        if (!user.isEmployee(user) && user.getId() != id) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "view", "users"));
        }
        return repository.getById(id);
    }

    @Override
    public void create(User user) {
        repository.verifyNotDuplicate(user);

        repository.create(user);
    }


}
