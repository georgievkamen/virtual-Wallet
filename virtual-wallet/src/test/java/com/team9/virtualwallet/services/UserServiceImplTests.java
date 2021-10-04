package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.InvitationToken;
import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.ConfirmationTokenRepositoryImpl;
import com.team9.virtualwallet.repositories.InvitationTokenRepositoryImpl;
import com.team9.virtualwallet.repositories.RoleRepositoryImpl;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.services.emails.SendEmailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.team9.virtualwallet.Helpers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    UserRepository mockRepository;

    @Mock
    SendEmailServiceImpl sendEmailService;

    @Mock
    WalletServiceImpl walletService;

    @Mock
    ConfirmationTokenRepositoryImpl confirmationTokenRepository;

    @Mock
    InvitationTokenRepositoryImpl invitationTokenRepository;

    @Mock
    RoleRepositoryImpl roleRepository;

    @InjectMocks
    UserServiceImpl service;


    @Test
    public void getById_Should_ReturnUser_When_MatchExist() {
        // Arrange
        var mockUser = createMockEmployee();
        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockUser);
        // Act
        var result = service.getById(createMockEmployee(), 1);

        // Assert
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(mockUser.getUsername(), result.getUsername());
    }

    @Test
    public void getById_Should_Throw_When_UnauthorizedUser() {
        // Arrange
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getById(createMockCustomer(), 2));
    }

    @Test
    public void getAll_Should_ReturnEmptyList_When_RepositoryEmpty() {
        var mockEmployee = createMockEmployee();
        Pageable pageable = PageRequest.of(1, 1);

        List<User> users = new ArrayList<>();
        Pages<User> list = new Pages<>(users, 1, pageable);

        Mockito.when(mockRepository.getAll(mockEmployee, pageable))
                .thenReturn(list);
        // Act
        Pages<User> result = service.getAll(mockEmployee, pageable);

        // Assert
        Assertions.assertEquals(1, result.getTotal());
    }


    @Test
    public void getAll_Should_Throw_When_UnauthorizedUser() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 1);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getAll(createMockCustomer(), pageable));
    }

    @Test
    public void getAllUnverified_Should_ReturnEmptyList_When_RepositoryEmpty() {
        var mockEmployee = createMockEmployee();
        Pageable pageable = PageRequest.of(1, 1);

        List<User> users = new ArrayList<>();
        Pages<User> list = new Pages<>(users, 1, pageable);

        Mockito.when(mockRepository.getAllUnverified(pageable))
                .thenReturn(list);
        // Act
        Pages<User> result = service.getAllUnverified(mockEmployee, pageable);

        // Assert
        Assertions.assertEquals(1, result.getTotal());
    }


    @Test
    public void getAllUnverified_Should_Throw_When_UnauthorizedUser() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 1);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getAllUnverified(createMockCustomer(), pageable));
    }

    @Test
    public void getByUserName_Should_ReturnUser_When_MatchExist() {
        // Arrange

        var mockUser = createMockEmployee();

        Mockito.when(mockRepository.getByField(anyString(), anyString()))
                .thenReturn(mockUser);
        // Act
        var result = service.getByUsername("userName");

        // Assert
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(mockUser.getUsername(), result.getUsername());
    }

    @Test
    public void Create_Should_Throw_When_DuplicateUser() {

        List<User> users = new ArrayList<>();
        var mockCustomer = createMockCustomer();
        mockCustomer.setId(2);
        users.add(mockCustomer);

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(users);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.create(createMockCustomer(), Optional.empty()));
    }

    @Test
    public void Create_Should_Call_Repository_When_UserIsValid() {

        var user = createMockEmployee();

        List<User> users = new ArrayList<>();
        users.add(createMockCustomer());

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(users);

        // Act
        service.create(user, Optional.empty());

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(User.class));
    }

    @Test
    public void Update_Should_Throw_When_UserNotEmployeeOrNotOwner() {

        var mockCustomer = createMockCustomer();
        var mockEmployee = createMockEmployee();
        mockEmployee.setId(3);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(mockCustomer, mockEmployee, 3));
    }


    @Test
    public void Update_Should_Throw_When_DuplicateExits() {

        var mockCustomer = createMockCustomer();
        var mockEmployee = createMockEmployee();

        List<User> users = new ArrayList<>();
        var mockCustomer1 = createMockCustomer();
        mockCustomer.setId(2);
        users.add(mockCustomer1);

        Mockito.when(mockRepository.getByFieldList("username", "mockUser"))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getByFieldList("phoneNumber", "0888888888"))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getByFieldList("email", "mock@user.com"))
                .thenReturn(users);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.update(mockEmployee, mockCustomer, 1));
    }

    @Test
    public void Update_Should_Throw_When_DuplicateExitsPhoneNumber() {

        var mockCustomer = createMockCustomer();
        var mockEmployee = createMockEmployee();

        List<User> users = new ArrayList<>();
        var mockCustomer1 = createMockCustomer();
        mockCustomer.setId(2);
        users.add(mockCustomer1);

        Mockito.when(mockRepository.getByFieldList("username", "mockUser"))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getByFieldList("phoneNumber", "0888888888"))
                .thenReturn(users);

        Mockito.when(mockRepository.getByFieldList("email", "mock@user.com"))
                .thenReturn(new ArrayList<>());

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.update(mockEmployee, mockCustomer, 1));
    }

    @Test
    public void Update_Should_Call_Repository_When_UserValid() {

        var mockCustomer = createMockCustomer();
        var mockEmployee = createMockEmployee();

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(new ArrayList<>());


        service.update(mockEmployee, mockCustomer, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(User.class));
    }


    @Test
    public void Delete_Should_Call_Repository_When_UserValid() {

        var user = createMockEmployee();

        service.delete(user);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .delete(Mockito.any(User.class));

    }

    @Test
    public void UpdateProfilePhoto_Should_Call_Repository_When_UserValid() {

        var user = createMockEmployee();
        var mockFile = createMockFile();

        service.updateProfilePhoto(user, mockFile);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .updateProfilePhoto(user, mockFile);

    }

    @Test
    public void UpdateIdAndSelfie_Should_Call_Repository_When_UserValid() {

        var user = createMockEmployee();
        user.setIdVerified(false);
        var mockId = createMockFile();
        var mockSelfie = createMockFile();


        service.updateIdAndSelfiePhoto(user, mockId, mockSelfie);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .updateIdAndSelfiePhoto(user, mockId, mockSelfie);

    }

    @Test
    public void UpdateIdAndSelfie_Should_Call_Throw_When_UserVerified() {

        var user = createMockEmployee();
        var mockId = createMockFile();
        var mockSelfie = createMockFile();

        // Assert
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.updateIdAndSelfiePhoto(user, mockId, mockSelfie));

    }

    @Test
    public void RemoveProfilePhoto_Should_Call_Repository_When_UserValid() {

        var user = createMockEmployee();

        service.removeProfilePhoto(user);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(user);

    }

    @Test
    public void Filter_Should_Call_Repository() {

        var mockEmployee = createMockEmployee();
        Pageable pageable = PageRequest.of(1, 1);

        service.filter(mockEmployee, Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        Mockito.verify(mockRepository, Mockito.times(1))
                .filter(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    }

    @Test
    public void Filter_Should_Call_Repository_With_Not_EmptyOptional() {

        var mockEmployee = createMockEmployee();
        Pageable pageable = PageRequest.of(1, 1);

        service.filter(mockEmployee, Optional.of("username"), Optional.empty(), Optional.empty(), pageable);

        Mockito.verify(mockRepository, Mockito.times(1))
                .filter(Optional.of("username"), Optional.empty(), Optional.empty(), pageable);

    }

    @Test
    public void Filter_Should_Throw_WhenUserNotAuthorised() {

        var mockCustomer = createMockCustomer();
        Pageable pageable = PageRequest.of(1, 1);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.filter(mockCustomer, Optional.empty()
                        , Optional.empty(), Optional.empty(), pageable));

    }

    @Test
    public void GetByField_Should_Throw_WhenSearchTermEmpty() {

        var mockCustomer = createMockCustomer();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getByField(mockCustomer, "field", ""));

    }

    @Test
    public void GetByField_Should_Call_Repository_When_SearchTerm_Not_Empty() {

        var mockCustomer = createMockCustomer();

        service.getByField(mockCustomer, "field", "term");

        Mockito.verify(mockRepository, Mockito.times(1))
                .getByFieldNotDeleted("field", "term", mockCustomer.getId());

    }


    @Test
    public void ConfirmUser_Should_Throw_When_UserEmailVerified() {

        var customer = createMockCustomer();
        var token = createMockConfirmationToken(customer);

        Mockito.when(confirmationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(token);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.confirmUser("12", Optional.empty()));
    }

    @Test
    public void ConfirmUser_Should_Call_Repository_When_UserEmailUnverified() {

        var customer = createMockCustomer();
        customer.setEmailVerified(false);
        var token = createMockConfirmationToken(customer);

        Mockito.when(confirmationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(token);

        service.confirmUser("12", Optional.empty());

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(customer);
    }

    @Test
    public void ConfirmUser_Should_Call_Repository_When_UserEmailUnverified_InvitationTokenPresent() {

        var customer = createMockCustomer();
        customer.setEmailVerified(false);
        var confirmationToken = createMockConfirmationToken(customer);
        var invitationToken = createMockInvitationToken(customer, "email");

        Mockito.when(invitationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(invitationToken);

        Mockito.doNothing().when(invitationTokenRepository).update(Mockito.any(InvitationToken.class));

        Mockito.doNothing().when(walletService).depositBalance(Mockito.any(Wallet.class), Mockito.any(BigDecimal.class));

        Mockito.when(confirmationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(confirmationToken);

        service.confirmUser("12", Optional.of("token"));

        Mockito.verify(mockRepository, Mockito.times(2))
                .update(customer);
    }

    @Test
    public void BlockUser_Should_Throw_When_UserNotEmployee() {

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.blockUser(createMockCustomer(), 1));
    }

    @Test
    public void BlockUser_Should_Call_Repository_When_UserEmployee() {

        var employee = createMockEmployee();
        var customer = createMockCustomer();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(customer);

        service.blockUser(employee, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(customer);

    }

    @Test
    public void UnblockUser_Should_Throw_When_UserNotEmployee() {

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.unblockUser(createMockCustomer(), 1));
    }

    @Test
    public void UnblockUser_Should_Call_Repository_When_UserEmployee() {

        var employee = createMockEmployee();
        var customer = createMockCustomer();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(customer);

        service.unblockUser(employee, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(customer);

    }

    @Test
    public void AddContact_Should_Throw_When_User_AddsHimself() {
        var mockUser = createMockCustomer();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.addContact(mockUser, mockUser.getId()));

    }

    @Test
    public void AddContact_Should_Throw_When_User_AlreadyAdded() {
        var mockUser = createMockCustomer();
        var mockContact = createMockEmployee();
        mockContact.setId(2);
        mockUser.setContacts(new HashSet<>());
        mockUser.addContact(mockContact);

        Mockito.when(mockRepository.getById(2)).thenReturn(mockContact);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.addContact(mockUser, 2));

    }

    @Test
    public void AddContact_Should_Call_Repository_When_Contact_NotAdded() {
        var mockUser = createMockCustomer();
        var mockContact = createMockEmployee();
        mockContact.setId(2);
        mockUser.setContacts(new HashSet<>());

        Mockito.when(mockRepository.getById(2)).thenReturn(mockContact);

        service.addContact(mockUser, 2);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockUser);

    }

    @Test
    public void RemoveContact_Should_Throw_When_Contact_NotExists() {
        var mockUser = createMockCustomer();
        var mockContact = createMockEmployee();
        mockContact.setId(2);
        mockUser.setContacts(new HashSet<>());

        Mockito.when(mockRepository.getById(2)).thenReturn(mockContact);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.removeContact(mockUser, 2));

    }

    @Test
    public void RemoveContact_Should_Call_Repository_When_Contact_Exists() {
        var mockUser = createMockCustomer();
        var mockContact = createMockEmployee();
        mockContact.setId(2);
        mockUser.setContacts(new HashSet<>());
        mockUser.addContact(mockContact);

        Mockito.when(mockRepository.getById(2)).thenReturn(mockContact);

        service.removeContact(mockUser, 2);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockUser);

    }

    @Test
    public void GetContacts_Should_Return_User_Contacts() {
        var mockUser = createMockCustomer();
        var mockContact = createMockEmployee();
        mockContact.setId(2);
        mockUser.setContacts(new HashSet<>());
        mockUser.addContact(mockContact);

        var result = service.getContacts(mockUser);

        Assertions.assertEquals(result.get(0).getId(), mockContact.getId());
    }

    @Test
    public void VerifyUser_Throws_When_User_Not_Employee() {
        var mockCustomer = createMockCustomer();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.verifyUser(mockCustomer, 2));
    }

    @Test
    public void VerifyUser_Should_Call_Repository_When_User_Unverified() {
        var mockEmployee = createMockEmployee();
        var mockCustomer = createMockCustomer();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCustomer);

        service.verifyUser(mockEmployee, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockCustomer);

    }

    @Test
    public void MakeEmployee_Throws_When_User_Not_Employee() {
        var mockCustomer = createMockCustomer();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.makeEmployee(mockCustomer, 2));
    }

    @Test
    public void MakeEmployee_Should_Call_Repository_When_User_Employee() {
        var mockEmployee = createMockEmployee();
        var mockCustomer = createMockCustomer();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCustomer);

        Mockito.when(roleRepository.getById(anyInt()))
                .thenReturn(createMockRole());

        service.makeEmployee(mockEmployee, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockCustomer);

    }

    @Test
    public void RemoveEmployee_Throws_When_User_Not_Employee() {
        var mockCustomer = createMockCustomer();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.removeEmployee(mockCustomer, 2));
    }

    @Test
    public void RemoveEmployee_Should_Call_Repository_When_User_Employee() {
        var mockEmployee = createMockEmployee();
        var mockCustomer = createMockCustomer();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCustomer);

        Mockito.when(roleRepository.getById(anyInt()))
                .thenReturn(createMockRole());

        service.removeEmployee(mockEmployee, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockCustomer);

    }

    @Test
    public void InviteFriend_Throws_When_User_Registered() {
        var mockCustomer = createMockCustomer();

        List<User> users = new ArrayList<>();
        users.add(mockCustomer);

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(users);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.inviteFriend(mockCustomer, "email"));
    }

    @Test
    public void InviteFriend_Should_Call_Repository_When_User_NotRegistered() {
        var mockCustomer = createMockCustomer();

        List<User> users = new ArrayList<>();

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(users);

        service.inviteFriend(mockCustomer, "email");

        Mockito.verify(sendEmailService, Mockito.times(1))
                .sendEmailInvitation(mockCustomer, "email");

    }

}
