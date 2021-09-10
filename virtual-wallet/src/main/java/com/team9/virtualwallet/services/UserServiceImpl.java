package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team9.virtualwallet.services.utils.MessageConstants.UNAUTHORIZED_ACTION;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @Override
    public List<User> getAll(User user) {
        if (!user.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "view all", "users"));
        }
        return repository.getAll();
    }

    @Override
    public User getById(User user, int id) {
        if (!user.isEmployee() && user.getId() != id) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "view", "users"));
        }
        return repository.getById(id);
    }

    @Override
    public void create(User user) {
        verifyNotDuplicate(user);

        repository.create(user);
    }


    @Override
    public void update(User userExecuting, User user, int id) {
        if (!userExecuting.isEmployee() && userExecuting.getId() != id) {
            throw new UnauthorizedOperationException("Users can only modify their own credentials!");
        }
        User userToUpdate = repository.getById(user.getId());

        if (!userToUpdate.getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("You cannot modify username");
        }
        verifyNotDuplicate(user);
        repository.update(user);
    }

    //TODO HANDLE SQL EXCEPTIONS
    @Override
    public void delete(User userExecuting, int id) {
        User userToDelete = repository.getById(id);

        if (!userExecuting.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "delete", "users"));
        }

        repository.delete(userToDelete);
    }

    @Override
    public List<User> filter(User user,
                             Optional<String> userName,
                             Optional<String> phoneNumber,
                             Optional<String> email) {

        return repository.filter(userName, phoneNumber, email)
                .stream()
                .filter(u -> u.getId() != user.getId())
                .collect(Collectors.toList());

    }

    public void verifyNotDuplicate(User user) {
        List<User> usersByUserName = repository.getByFieldList("username", user.getUsername());
        List<User> usersByEmail = repository.getByFieldList("email", user.getEmail());
        List<User> usersByPhoneNumber = repository.getByFieldList("phoneNumber", user.getPhoneNumber());

        if (!usersByUserName.isEmpty() && usersByUserName.get(0).getId() != user.getId()) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }
        if (!usersByEmail.isEmpty() && usersByEmail.get(0).getId() != user.getId()) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
        if (!usersByPhoneNumber.isEmpty() && usersByPhoneNumber.get(0).getId() != user.getId()) {
            throw new DuplicateEntityException("User", "phoneNumber", user.getEmail());
        }
    }

}

