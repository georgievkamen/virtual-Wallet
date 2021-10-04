package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.InsufficientBalanceException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.PaymentMethod;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.team9.virtualwallet.Helpers.*;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTests {

    @Mock
    WalletRepository mockRepository;

    @Mock
    PaymentMethodRepository paymentMethodRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    WalletServiceImpl service;

    @Test
    public void getById_Should_ReturnWallet_When_MatchExist() {
        // Arrange
        var mockUser = createMockEmployee();
        var mockWallet = createMockWallet(mockUser);
        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);
        // Act
        var result = service.getById(mockUser, 1);

        // Assert
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(mockWallet.getUser(), result.getUser());
    }

    @Test
    public void getById_Should_Throw_When_UnauthorizedUser() {

        var mockUser = createMockEmployee();
        mockUser.setId(2);
        var mockWallet = createMockWallet(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        // Arrange
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getById(createMockCustomer(), 2));
    }

    @Test
    public void getAll_Should_ReturnEmptyList_When_RepositoryEmpty() {
        // Arrange
        List<Wallet> list = new ArrayList<>();

        Mockito.when(mockRepository.getAll(Mockito.any(User.class)))
                .thenReturn(list);
        // Act
        List<Wallet> result = service.getAll(createMockEmployee());

        // Assert
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void Create_Should_Throw_When_DuplicateWallet() {

        var mockUser = createMockEmployee();
        var mockWallet = createMockWallet(mockUser);


        Mockito.when(mockRepository.isDuplicate(mockUser, mockWallet))
                .thenReturn(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.create(mockUser, mockWallet));
    }

    @Test
    public void Create_Should_Call_Repository_When_WalletIsValid() {

        var mockUser = createMockEmployee();
        var mockWallet = createMockWallet(mockUser);

        Mockito.when(mockRepository.isDuplicate(mockUser, mockWallet))
                .thenReturn(false);

        Mockito.doNothing().when(paymentMethodRepository).create(Mockito.any(PaymentMethod.class));
        Mockito.doNothing().when(userRepository).update(Mockito.any(User.class));

        // Act
        service.create(mockUser, mockWallet);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(Wallet.class));
    }

    @Test
    public void Update_Should_Throw_When_UserNotOwner() {

        var mockUser = createMockEmployee();
        var mockUser1 = createMockEmployee();
        mockUser.setId(2);
        var mockWallet = createMockWallet(mockUser);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(mockUser1, mockWallet));
    }

    @Test
    public void Update_Should_Throw_When_DuplicateExits() {

        var mockCustomer = createMockCustomer();
        var mockWallet = createMockWallet(mockCustomer);
        var mockWallet2 = createMockWallet(mockCustomer);
        mockWallet.setName("wallet");

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        Mockito.when(mockRepository.isDuplicate(Mockito.any(User.class), Mockito.any(Wallet.class)))
                .thenReturn(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.update(mockCustomer, mockWallet2));
    }

    @Test
    public void Update_Should_Call_Repository_When_WalletValid() {

        var mockUser = createMockEmployee();
        var mockWallet = createMockWallet(mockUser);

        Mockito.when(mockRepository.isDuplicate(mockUser, mockWallet))
                .thenReturn(false);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        // Act
        service.update(mockUser, mockWallet);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(Wallet.class));
    }

    @Test
    public void VerifyWalletOwnerShip_Should_Throw_When_UserNotOwner() {
        var mockTransaction = createMockTransaction();
        var mockRecipient = createMockCustomer();
        mockRecipient.setId(5);
        mockTransaction.setRecipient(mockRecipient);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.verifyWalletOwnership(mockTransaction, createMockWallet(createMockCustomer())));
    }

    @Test
    public void Delete_Should_Throw_When_UserNotOwner() {

        var mockUser = createMockEmployee();
        var mockUser1 = createMockEmployee();
        mockUser1.setId(4);
        var mockWallet = createMockWallet(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(mockUser1, 1));
    }

    @Test
    public void Delete_Should_Throw_When_DefaultWallet() {

        var mockUser = createMockEmployee();
        var mockWallet = createMockWallet(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.delete(mockUser, 1));
    }

    @Test
    public void Delete_Should_Throw_When_WalletHasMoney() {

        var mockUser = createMockEmployee();
        var defaultWallet = createMockWallet(mockUser);
        defaultWallet.setId(5);
        mockUser.setDefaultWallet(defaultWallet);
        var mockWallet = createMockWallet(mockUser);
        mockWallet.setBalance(BigDecimal.valueOf(500));

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.delete(mockUser, 1));
    }

    @Test
    public void Delete_Should_Call_Repository_When_WalletValid() {

        var mockUser = createMockEmployee();
        var defaultWallet = createMockWallet(mockUser);
        defaultWallet.setId(5);
        mockUser.setDefaultWallet(defaultWallet);
        var mockWallet = createMockWallet(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        service.delete(mockUser, 1);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .delete(Mockito.any(Wallet.class));

    }

    @Test
    public void Deposit_Should_Call_Repository() {

        var mockUser = createMockEmployee();
        var defaultWallet = createMockWallet(mockUser);


        service.depositBalance(defaultWallet, BigDecimal.valueOf(500));

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(Wallet.class));

    }

    @Test
    public void Withdraw_Should_Call_Repository_When_EnoughBalance() {

        var mockUser = createMockEmployee();
        var defaultWallet = createMockWallet(mockUser);
        defaultWallet.setBalance(BigDecimal.valueOf(600));

        service.withdrawBalance(defaultWallet, BigDecimal.valueOf(500));

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(Wallet.class));

    }

    @Test
    public void Withdraw_Should_Throw_When_NotEnoughBalance() {

        var mockUser = createMockEmployee();
        var defaultWallet = createMockWallet(mockUser);
        defaultWallet.setBalance(BigDecimal.valueOf(300));


        Assertions.assertThrows(InsufficientBalanceException.class,
                () -> service.withdrawBalance(defaultWallet, BigDecimal.valueOf(500)));

    }

    @Test
    public void SetDefaultWallet_Should_Call_Repository() {

        var mockUser = createMockEmployee();
        var defaultWallet = createMockWallet(mockUser);

        service.setDefaultWallet(mockUser, defaultWallet);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1))
                .update(Mockito.any(User.class));

    }

    @Test
    public void GetTotalBalance_Should_Call_Repository() {

        var mockUser = createMockEmployee();

        service.getTotalBalanceByUser(mockUser);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .getTotalBalanceByUser(Mockito.any(User.class));

    }

    @Test
    public void VerifyWalletsOwnership_Should_Throw_When_UserNotOwner() {

        var mockTransaction = createMockTransaction();
        var mockWallet = createMockWallet(createMockCustomer());
        var mockEmployee = createMockEmployee();
        mockEmployee.setId(5);
        var walletToMoveTo = createMockWallet(mockEmployee);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.verifyWalletsOwnership(mockTransaction, mockWallet, walletToMoveTo));

    }

    @Test
    public void CreateDefaultWallet_Should_ReturnWallet() {

        var mockUser = createMockEmployee();
        var mockWallet = createMockWallet(mockUser);
        mockWallet.setName("wallet");

        Mockito.doNothing().when(mockRepository).create(Mockito.any(Wallet.class));
        var result = service.createDefaultWallet(mockUser);


        // Assert
        Assertions.assertEquals(result.getUser(), mockUser);

    }

}
