package com.team9.virtualwallet.services.contracts;

import com.team9.virtualwallet.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User getById(User user, int id);

    List<User> getAll(User user);

    User getByUsername(String username);

    void create(User user);

    void update(User userExecuting, User user, int id);

    void delete(User user);

    void confirmUser(String confirmationToken);

    void addContact(User userExecuting, int contactId);

    void removeContact(User userExecuting, int contactId);

    List<User> getContacts(User user);

    void blockUser(User userExecuting, int id);

    void unblockUser(User userExecuting, int id);

    List<User> filter(User user,
                      Optional<String> userName,
                      Optional<String> phoneNumber,
                      Optional<String> email);
}
