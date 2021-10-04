package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.CardExpiredException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.contracts.*;
import com.team9.virtualwallet.services.contracts.CardService;
import com.team9.virtualwallet.services.contracts.CategoryService;
import com.team9.virtualwallet.services.contracts.UserService;
import com.team9.virtualwallet.services.contracts.WalletService;
import com.team9.virtualwallet.services.emails.SendEmailService;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.team9.virtualwallet.Helpers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTests {

    @Mock
    TransactionRepository mockRepository;

    @Mock
    WalletRepository walletRepository;

    @Mock
    CardService cardService;

    @Mock
    CardRepository cardRepository;

    @Mock
    WalletService walletService;

    @Mock
    CategoryService categoryService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @Mock
    TransactionVerificationTokenRepository transactionVerificationTokenRepository;

    @Mock
    SendEmailService sendEmailService;

    @InjectMocks
    TransactionServiceImpl service;

    @Test
    public void getById_Should_ReturnTransaction_When_MatchExist() {
        // Arrange
        var mockUser = createMockEmployee();
        var mockTransaction = createMockTransaction();

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockTransaction);
        // Act
        var result = service.getById(mockUser, 1);

        // Assert
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(mockTransaction.getAmount(), result.getAmount());
    }

    @Test
    public void getById_Should_Throw_When_UnauthorizedUser() {
        var mockTransaction = createMockTransaction();
        var recipient = createMockCustomer();
        var sender = createMockCustomer();
        recipient.setId(16);
        sender.setId(20);
        mockTransaction.setRecipient(recipient);
        mockTransaction.setSender(sender);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockTransaction);
        // Arrange
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getById(createMockCustomer(), 1));
    }

    @Test
    public void getAll_Should_ReturnEmptyList_When_RepositoryEmpty() {
        var mockEmployee = createMockEmployee();
        Pageable pageable = PageRequest.of(1, 1);

        List<Transaction> transactions = new ArrayList<>();
        Pages<Transaction> list = new Pages<>(transactions, 1, pageable);

        Mockito.when(mockRepository.getAll(mockEmployee, pageable))
                .thenReturn(list);
        // Act
        Pages<Transaction> result = service.getAll(mockEmployee, pageable);

        // Assert
        Assertions.assertEquals(1, result.getTotal());
    }

    @Test
    public void getLastTransactions_Should_Call_Repository() {
        var transactions = new ArrayList<Transaction>();
        var mockUser = createMockEmployee();

        Mockito.when(mockRepository.getLastTransactions(mockUser, 6))
                .thenReturn(transactions);

        var result = service.getLastTransactions(mockUser, 6);

        Assertions.assertEquals(transactions.size(), result.size());
    }

    @Test
    public void Create_Throws_When_Users_Sends_To_Himself() {
        var mockUser = createMockCustomer();
        var mockTransaction = createMockTransaction();
        mockTransaction.setRecipient(mockUser);
        mockTransaction.setSender(mockUser);


        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(mockTransaction, Optional.empty()));
    }

    @Test
    public void Create_Throws_When_Users_Cannot_Make_Transactions_Blocked() {
        var mockUser = createMockCustomer();
        var mockRecipient = createMockCustomer();
        mockRecipient.setId(6);
        var mockTransaction = createMockTransaction();
        mockUser.setBlocked(true);
        mockTransaction.setRecipient(mockRecipient);
        mockTransaction.setSender(mockUser);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(mockTransaction, Optional.empty()));
    }

    @Test
    public void Create_Throws_When_Users_Cannot_Make_Transactions_IdUnverified() {
        var mockUser = createMockCustomer();
        var mockRecipient = createMockCustomer();
        mockRecipient.setId(5);
        var mockTransaction = createMockTransaction();
        mockUser.setIdVerified(false);
        mockTransaction.setSender(mockUser);
        mockTransaction.setRecipient(mockRecipient);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(mockTransaction, Optional.empty()));
    }

    @Test
    public void Create_Throws_When_Users_Cannot_Make_Transactions_EmailUnverified() {
        var mockUser = createMockCustomer();
        var mockRecipient = createMockCustomer();
        mockRecipient.setId(5);
        var mockTransaction = createMockTransaction();
        mockUser.setEmailVerified(false);
        mockTransaction.setSender(mockUser);
        mockTransaction.setRecipient(mockRecipient);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(mockTransaction, Optional.empty()));
    }


    @Test
    public void Create_Should_Call_Repository_When_Users_Can_Make_Transactions_Large() {
        var mockUser = createMockCustomer();
        var mockEmployee = createMockEmployee();
        var mockTransaction = createMockTransaction();
        var mockWallet = createMockWallet(mockUser);
        mockTransaction.setAmount(BigDecimal.valueOf(1200000));
        mockEmployee.setId(5);
        mockTransaction.setSender(mockUser);
        mockTransaction.setRecipient(mockEmployee);

        Mockito.when(categoryService.getById(mockTransaction.getSender(), 1))
                .thenReturn(createMockCategory(mockTransaction.getSender()));

        Mockito.when(walletRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        service.create(mockTransaction, Optional.of(1));

        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(Transaction.class), Mockito.any(Wallet.class), Mockito.any(Wallet.class));

        Mockito.verify(sendEmailService, Mockito.times(1))
                .sendEmailTransactionVerification(Mockito.any(Transaction.class));
    }

    @Test
    public void Create_Should_Call_Repository_When_Users_Can_Make_Transactions_Small() {
        var mockUser = createMockCustomer();
        var mockEmployee = createMockEmployee();
        var mockTransaction = createMockTransaction();
        var mockWallet = createMockWallet(mockUser);
        mockEmployee.setId(5);
        mockTransaction.setSender(mockUser);
        mockTransaction.setRecipient(mockEmployee);

        Mockito.when(categoryService.getById(mockTransaction.getSender(), 1))
                .thenReturn(createMockCategory(mockTransaction.getSender()));

        Mockito.when(walletRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        service.create(mockTransaction, Optional.of(1));

        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(Transaction.class), Mockito.any(Wallet.class), Mockito.any(Wallet.class));

    }

    @Test
    public void ConfirmLargeTransaction_Throws_When_Not_Own() {
        var mockUser = createMockCustomer();
        var mockEmployee = createMockEmployee();
        var mockTransaction = createMockTransaction();
        var mockToken = createMockTransactionVerificationToken(mockTransaction);
        mockUser.setId(6);
        mockTransaction.setSender(mockEmployee);

        Mockito.when(transactionVerificationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(mockToken);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.confirmLargeTransaction(mockUser, "token"));
    }

    @Test
    public void ConfirmLargeTransaction_Throws_When_TokenExpired() {
        var mockUser = createMockCustomer();
        var mockTransaction = createMockTransaction();
        mockTransaction.setSender(mockUser);
        var mockToken = createMockTransactionVerificationToken(mockTransaction);
        mockToken.setExpirationDate(Timestamp.valueOf(LocalDateTime.now().minusDays(2)));

        Mockito.when(transactionVerificationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(mockToken);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.confirmLargeTransaction(mockUser, "token"));
    }

    @Test
    public void ConfirmLargeTransaction_Calls_Repository_When_TokenValid() {
        var mockUser = createMockCustomer();
        var mockTransaction = createMockTransaction();
        var mockWallet = createMockWallet(mockUser);
        mockTransaction.setSender(mockUser);
        var mockToken = createMockTransactionVerificationToken(mockTransaction);
        mockToken.setExpirationDate(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));

        Mockito.when(transactionVerificationTokenRepository.getByField(anyString(), anyString()))
                .thenReturn(mockToken);

        Mockito.when(walletRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        service.confirmLargeTransaction(mockUser, mockToken.toString());

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(Transaction.class), Mockito.any(Wallet.class), Mockito.any(Wallet.class));

    }

    @Test
    public void CreateWalletToWallet_Throws_When_Wallet_Is_Same() {
        var mockUser = createMockCustomer();
        var mockWallet = createMockWallet(mockUser);
        var mockTransaction = createMockTransaction();
        mockTransaction.setSender(mockUser);
        mockTransaction.setRecipient(mockUser);

        Mockito.when(walletRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.createWalletToWallet(mockTransaction));
    }

    @Test
    public void CreateWalletToWallet_Should_Call_Repositorory_When_TransactionValid() {
        var mockUser = createMockCustomer();
        var mockWalletToMoveFrom = createMockWallet(mockUser);
        var mockWalletToMoveTo = createMockWallet(mockUser);
        var mockTransaction = createMockTransaction();
        mockWalletToMoveFrom.setId(1);
        mockWalletToMoveTo.setId(2);
        mockTransaction.setSender(mockUser);
        mockTransaction.setRecipient(mockUser);

        Mockito.when(walletRepository.getById(1))
                .thenReturn(mockWalletToMoveFrom);

        Mockito.when(walletRepository.getById(2))
                .thenReturn(mockWalletToMoveTo);

        service.createWalletToWallet(mockTransaction);

        Mockito.verify(mockRepository, Mockito.times(1))
                .create(mockTransaction, mockWalletToMoveTo, mockWalletToMoveFrom);

    }

    @Test
    public void CreateExternalDeposit_Should_Throw_When_Card_Expired() {
        var mockUser = createMockCustomer();
        var mockCard = createMockCard(mockUser);
        var mockTransaction = createMockTransaction();
        mockCard.setExpirationDate(LocalDate.now().minusDays(2));

        Mockito.when(cardRepository.getById(anyInt()))
                .thenReturn(mockCard);

        Assertions.assertThrows(CardExpiredException.class,
                () -> service.createExternalDeposit(mockTransaction));
    }

    @Test
    public void CreateExternalDeposit_Should_Call_Repository_When_TransactionValid() {
        var mockUser = createMockCustomer();
        var mockCard = createMockCard(mockUser);
        var mockWallet = createMockWallet(mockUser);
        var mockTransaction = createMockTransaction();

        Mockito.when(cardRepository.getById(anyInt()))
                .thenReturn(mockCard);

        Mockito.when(walletRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        service.createExternalDeposit(mockTransaction);

        Mockito.verify(mockRepository, Mockito.times(1))
                .createExternal(mockTransaction, mockWallet);
    }

    @Test
    public void CreateExternalWithdraw_Should_Call_Repository_When_TransactionValid() {
        var mockUser = createMockCustomer();
        var mockCard = createMockCard(mockUser);
        var mockWallet = createMockWallet(mockUser);
        var mockTransaction = createMockTransaction();

        Mockito.when(cardRepository.getById(anyInt()))
                .thenReturn(mockCard);

        Mockito.when(walletRepository.getById(anyInt()))
                .thenReturn(mockWallet);

        service.createExternalWithdraw(mockTransaction);

        Mockito.verify(mockRepository, Mockito.times(1))
                .createExternal(mockTransaction, mockWallet);
    }

    @Test
    public void Filter_Should_Call_Repository() {
        var mockEmployee = createMockEmployee();
        Pageable pageable = PageRequest.of(1, 1);

        Mockito.when(userRepository.getByField("username", "test"))
                .thenReturn(mockEmployee);
        // Act
        service.filter(mockEmployee,
                Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of("test"), Optional.empty(), Optional.empty(), pageable);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .filter(mockEmployee.getId(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(1),
                        Optional.empty(),
                        Optional.empty(),
                        pageable);
    }

    @Test
    public void EmployeeFilter_Should_Call_Repository() {
        var mockEmployee = createMockEmployee();
        Pageable pageable = PageRequest.of(1, 1);

        Mockito.when(userRepository.getByField(anyString(), anyString()))
                .thenReturn(mockEmployee);
        // Act
        service.employeeFilter(mockEmployee, "test", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                pageable);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .filter(mockEmployee.getId(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(),
                        pageable);
    }

    @Test
    public void EmployeeFilter_Should_Throw_WhenUserNotEmployee() {
        var mockCustomer = createMockCustomer();
        Pageable pageable = PageRequest.of(1, 1);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.employeeFilter(mockCustomer, "test", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        pageable));

    }
}
