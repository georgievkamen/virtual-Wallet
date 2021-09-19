package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.contracts.ConfirmationTokenRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.services.contracts.UserService;
import com.team9.virtualwallet.services.contracts.WalletService;
import com.team9.virtualwallet.services.emails.SendEmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team9.virtualwallet.services.utils.MessageConstants.UNAUTHORIZED_ACTION;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final WalletService walletService;
    private final SendEmailServiceImpl sendEmailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository, WalletService walletService, SendEmailServiceImpl sendEmailService) {
        this.repository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.walletService = walletService;
        this.sendEmailService = sendEmailService;
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
    public User getByUsername(String username) {
        return repository.getByField("username", username);
    }

    @Override
    public void create(User user) {
        verifyNotDuplicate(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        sendEmailService.sendEmailConfirmation(user, confirmationToken);

        repository.create(user);
        confirmationTokenRepository.create(confirmationToken);
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

    @Override
    public void addContact(User userExecuting, int contactId) {

        User contactToAdd = repository.getById(contactId);
        verifyContactNotAdded(userExecuting, contactToAdd);
        userExecuting.addContact(contactToAdd);

        repository.update(userExecuting);
    }

    @Override
    public void removeContact(User userExecuting, int contactId) {

        User contactToDelete = repository.getById(contactId);
        verifyContactExists(userExecuting, contactToDelete);
        userExecuting.removeContact(contactToDelete.getUsername());

        repository.update(userExecuting);
    }

    @Override
    public List<User> getContacts(User user) {
        return new ArrayList<>(user.getContacts());
    }

    @Override
    public void confirmUser(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.getByField("confirmationToken", confirmationToken);
        User user = token.getUser();

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("You have already verified your Email!");
        }

        user.setEmailVerified(true);
        repository.update(user);

        createDefaultWallet(user);
    }

    @Override
    public void blockUser(User userExecuting, int id) {
        if (!userExecuting.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "block", "users"));
        }

        User user = repository.getById(id);
        user.setBlocked(true);

        repository.update(user);
    }

    @Override
    public void unblockUser(User userExecuting, int id) {
        if (!userExecuting.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "unblock", "users"));
        }

        User user = repository.getById(id);
        user.setBlocked(false);

        repository.update(user);
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
            throw new DuplicateEntityException("User", "phone number", user.getPhoneNumber());
        }
    }

    private void verifyContactNotAdded(User userExecuting, User contactToAdd) {
        if (userExecuting.getContacts().stream().anyMatch(user -> user.getUsername().equals(contactToAdd.getUsername()))) {
            throw new DuplicateEntityException(String.format("User with username %s is already in your contact list", contactToAdd.getUsername()));
        }
    }

    private void verifyContactExists(User userExecuting, User contactToDelete) {
        if (userExecuting.getContacts().stream().noneMatch(user -> user.getUsername().equals(contactToDelete.getUsername()))) {
            throw new EntityNotFoundException(String.format("Contact with username %s is not in your contact list", contactToDelete.getUsername()));
        }
    }

    private void createDefaultWallet(User user) {
        Wallet defaultWallet = new Wallet();
        defaultWallet.setName("Default Wallet");
        defaultWallet.setBalance(BigDecimal.valueOf(0));
        defaultWallet.setUser(user);
        walletService.create(user, defaultWallet);
    }

}

