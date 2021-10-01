package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.*;
import com.team9.virtualwallet.repositories.contracts.ConfirmationTokenRepository;
import com.team9.virtualwallet.repositories.contracts.InvitationTokenRepository;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.services.contracts.UserService;
import com.team9.virtualwallet.services.contracts.WalletService;
import com.team9.virtualwallet.services.emails.SendEmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.team9.virtualwallet.configs.ApplicationConstants.FREE_BONUS_AMOUNT;
import static com.team9.virtualwallet.configs.ApplicationConstants.MAX_ALLOWED_REFERRALS;
import static com.team9.virtualwallet.services.utils.MessageConstants.UNAUTHORIZED_ACTION;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final InvitationTokenRepository invitationTokenRepository;
    private final WalletService walletService;
    private final RoleRepository roleRepository;
    private final SendEmailServiceImpl sendEmailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository, InvitationTokenRepository invitationTokenRepository, WalletService walletService, RoleRepository roleRepository, SendEmailServiceImpl sendEmailService) {
        this.repository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.invitationTokenRepository = invitationTokenRepository;
        this.walletService = walletService;
        this.roleRepository = roleRepository;
        this.sendEmailService = sendEmailService;
    }

    @Override
    public Pages<User> getAll(User user, Pageable pageable) {
        if (!user.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "view all", "users"));
        }
        return repository.getAll(user, pageable);
    }

    @Override
    public Pages<User> getAllUnverified(User user, Pageable pageable) {
        if (!user.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "view all", "users"));
        }
        return repository.getAllUnverified(pageable);
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
    public void create(User user, Optional<String> invitationTokenUUID) {
        verifyNotDuplicate(user);
        repository.create(user);
        Wallet wallet = walletService.createDefaultWallet(user);
        user.setDefaultWallet(wallet);
        repository.update(user);
        sendEmailService.sendEmailConfirmation(user, invitationTokenUUID);
    }

    @Override
    public void update(User userExecuting, User user, int id) {
        if (!userExecuting.isEmployee() && userExecuting.getId() != id) {
            throw new UnauthorizedOperationException("Users can only modify their own credentials!");
        }

        verifyNotDuplicate(user);
        repository.update(user);
    }


    @Override
    public void updateProfilePhoto(User user, MultipartFile multipartFile) {
        repository.updateProfilePhoto(user, multipartFile);
    }

    @Override
    public void updateIdAndSelfiePhoto(User user, MultipartFile idPhoto, MultipartFile selfiePhoto) {
        if (user.isIdVerified()) {
            throw new IllegalArgumentException("You have already been verified!");
        }
        repository.updateIdAndSelfiePhoto(user, idPhoto, selfiePhoto);
    }

    @Override
    public void removeProfilePhoto(User user) {
        user.setUserPhoto(null);
        repository.update(user);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }

    @Override
    public Pages<User> filter(User user,
                              Optional<String> userName,
                              Optional<String> phoneNumber,
                              Optional<String> email,
                              Pageable pageable) {

        if (!user.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "filter", "users"));
        }

        return repository.filter(verifyOptionalNotEmpty(userName), verifyOptionalNotEmpty(phoneNumber), verifyOptionalNotEmpty(email), pageable);

    }

    @Override
    public User getByField(User user, String fieldName, String searchTerm) {

        if (searchTerm.isEmpty()) {
            throw new EntityNotFoundException(String.format("Please input a correct %s", fieldName));
        }
        return repository.getByFieldNotDeleted(fieldName, searchTerm, user.getId());
    }

    @Override
    public void addContact(User userExecuting, int contactId) {
        if (userExecuting.getId() == contactId) {
            throw new IllegalArgumentException("You cannot add yourself to contacts!");
        }

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
    public void confirmUser(String confirmationTokenUUID, Optional<String> invitationTokenUUID) {
        ConfirmationToken token = confirmationTokenRepository.getByField("confirmationToken", confirmationTokenUUID);
        User user = token.getUser();

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("You have already verified your Email!");
        } else {
            user.setEmailVerified(true);
            repository.update(user);
            if (invitationTokenUUID.isPresent()) {
                InvitationToken invitationToken = invitationTokenRepository.getByField("invitationToken", invitationTokenUUID.get());
                if (!invitationToken.isUsed() && Timestamp.valueOf(LocalDateTime.now()).before(invitationToken.getExpirationDate())) {
                    invitationToken.setUsed(true);
                    invitationTokenRepository.update(invitationToken);
                    User invitingUser = invitationToken.getInvitingUser();
                    invitingUser.setInvitedUsers(user.getInvitedUsers() + 1);
                    repository.update(invitingUser);
                    if (invitingUser.getInvitedUsers() < MAX_ALLOWED_REFERRALS) {
                        walletService.depositBalance(invitingUser.getDefaultWallet(), BigDecimal.valueOf(FREE_BONUS_AMOUNT));
                        walletService.depositBalance(user.getDefaultWallet(), BigDecimal.valueOf(FREE_BONUS_AMOUNT));
                    }
                }
            }
        }

    }

    @Override
    public void verifyUser(User userExecuting, int userId) {
        if (!userExecuting.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "verify", "users"));
        }
        User user = repository.getById(userId);
        user.setIdVerified(true);
        repository.update(user);
    }

    @Override
    public void makeEmployee(User userExecuting, int userId) {
        if (!userExecuting.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "make", "employee"));
        }
        User user = repository.getById(userId);
        user.addRole(roleRepository.getById(2));
        repository.update(user);
    }

    @Override
    public void removeEmployee(User userExecuting, int userId) {
        if (!userExecuting.isEmployee()) {
            throw new UnauthorizedOperationException(String.format(UNAUTHORIZED_ACTION, "employees", "remove", "employee"));
        }
        User user = repository.getById(userId);
        user.removeRole(roleRepository.getById(2));
        repository.update(user);
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

    @Override
    public void inviteFriend(User user, String email) {
        verifyEmailNotRegistered(email);
        sendEmailService.sendEmailInvitation(user, email);
    }

    private Optional<String> verifyOptionalNotEmpty(Optional<String> optional) {
        if (optional.isPresent() && !optional.get().isEmpty()) {
            return optional;
        }
        return Optional.empty();
    }


    private void verifyNotDuplicate(User user) {
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

    private void verifyEmailNotRegistered(String email) {
        List<User> usersByEmail = repository.getByFieldList("email", email);

        if (!usersByEmail.isEmpty()) {
            throw new DuplicateEntityException("User", "email", email);
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

}

