package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.repositories.ConfirmationTokenRepositoryImpl;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.services.emails.SendEmailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.team9.virtualwallet.Helpers.createMockCustomer;
import static com.team9.virtualwallet.Helpers.createMockEmployee;
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

/*    @Test
    public void getAll_Should_ReturnEmptyList_When_RepositoryEmpty() {
        // Arrange
        List<User> list = new ArrayList<>();

        Mockito.when(mockRepository.getAll())
                .thenReturn(list);
        // Act
        List<User> result = service.getAll(createMockEmployee());

        // Assert
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getAll_Should_Throw_When_UnauthorizedUser() {
        // Arrange
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getAll(createMockCustomer()));
    }*/

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

/*    @Test
    public void Create_Should_Throw_When_DuplicateUser() {

        List<User> users = new ArrayList<>();
        var mockCustomer = createMockCustomer();
        mockCustomer.setId(2);
        users.add(mockCustomer);

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(users);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.create(createMockCustomer()));
    }*/

/*    @Test
    public void Create_Should_Call_Repository_When_UserIsValid() {

        var user = createMockEmployee();

        List<User> users = new ArrayList<>();
        users.add(createMockCustomer());

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(users);

        Mockito.doNothing().when(sendEmailService).sendEmailConfirmation(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(confirmationTokenRepository).create(Mockito.any());
        Mockito.doNothing().when(walletService).create(Mockito.any(), Mockito.any());

        // Act
        service.create(user);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(User.class));
    }*/

/*
    @Test
    public void Update_Should_Throw_When_UserNotEmployeeOrNotOwner() {

        var mockCustomer = createMockCustomer();
        var mockEmployee = createMockEmployee();
        mockEmployee.setId(3);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(mockCustomer, mockEmployee, 3));
    }

    @Test
    public void Update_Should_Throw_When_UserTryToChangeUsername() {

        var mockCustomer = createMockCustomer();
        var mockCustomer2 = createMockCustomer();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCustomer);
        mockCustomer.setUsername("mocky");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.update(mockCustomer, mockCustomer2, 1));
    }
*/

/*    @Test
    public void Update_Should_Throw_When_DuplicateExits() {

        var mockCustomer = createMockCustomer();
        var mockEmployee = createMockEmployee();

        List<User> users = new ArrayList<>();
        var mockCustomer1 = createMockCustomer();
        mockCustomer.setId(2);
        users.add(mockCustomer1);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCustomer);

        Mockito.when(mockRepository.getByFieldList("username", "mockUser"))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getByFieldList("phoneNumber", "0888888888"))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getByFieldList("email", "mock@user.com"))
                .thenReturn(users);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCustomer);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.update(mockEmployee, mockCustomer, 1));
    }*/

/*
    @Test
    public void Update_Should_Call_Repository_When_UserValid() {

        var mockCustomer = createMockCustomer();
        var mockEmployee = createMockEmployee();

        Mockito.when(mockRepository.getByFieldList(anyString(), anyString()))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCustomer);

        service.update(mockEmployee, mockCustomer, 1);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(User.class));
    }
*/

/*    @Test
    public void Delete_Should_Throw_When_UserNotEmployee() {

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(createMockCustomer(), 1));
    }

    @Test
    public void Delete_Should_Call_Repository_When_UserValid() {

        var user = createMockEmployee();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(user);

        service.delete(user, 1);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .delete(Mockito.any(User.class));

    }*/

/*    @Test
    public void Filter_Should_Call_Repository() {

        var mockEmployee = createMockEmployee();

        service.filter(mockEmployee, Optional.empty(), Optional.empty(), Optional.empty());

        Mockito.verify(mockRepository, Mockito.times(1))
                .filter(Optional.empty(), Optional.empty(), Optional.empty());

    }*/

/*    @Test
    public void ConfirmUser_Should_Throw_When_UserEmailVerified() {

        var customer = createMockCustomer();
        var token = createMockConfirmationToken(customer);

        Mockito.when(confirmationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(token);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.confirmUser("12"));
    }

    @Test
    public void ConfirmUser_Should_Call_Repository_When_UserEmailUnverified() {

        var customer = createMockCustomer();
        customer.setEmailVerified(false);
        var token = createMockConfirmationToken(customer);

        Mockito.when(confirmationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(token);

        service.confirmUser("12");

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(customer);
    }*/

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

/*    @Test
    public void VerifyNotDuplicate_Should_Throw_When_UserDuplicate() {

        var mockCustomer = createMockCustomer();

        List<User> users = new ArrayList<>();
        var mockCustomer1 = createMockCustomer();
        mockCustomer.setId(2);
        users.add(mockCustomer1);

        Mockito.when(mockRepository.getByFieldList("username", "mockUser"))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getByFieldList("email", "mock@user.com"))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockRepository.getByFieldList("phoneNumber", "0888888888"))
                .thenReturn(users);


        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.verifyNotDuplicate(mockCustomer));
    }*/

}
