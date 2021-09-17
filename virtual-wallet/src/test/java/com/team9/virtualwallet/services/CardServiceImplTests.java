package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.PaymentMethod;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.CardRepository;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.team9.virtualwallet.Helpers.*;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTests {

    @Mock
    CardRepository mockRepository;

    @Mock
    PaymentMethodRepository paymentMethodRepository;
    @InjectMocks
    CardServiceImpl service;

    @Test
    public void getById_Should_ReturnCard_When_MatchExist() {
        // Arrange
        var mockUser = createMockEmployee();
        var mockCard = createMockCard(mockUser);
        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCard);
        // Act
        var result = service.getById(mockUser, 1);

        // Assert
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(mockCard.getCardHolder(), result.getCardHolder());
    }

    @Test
    public void getById_Should_Throw_When_UnauthorizedUser() {

        var mockUser = createMockEmployee();
        mockUser.setId(2);
        var mockCard = createMockCard(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCard);

        // Arrange
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getById(createMockCustomer(), 2));
    }

    @Test
    public void getAll_Should_ReturnEmptyList_When_RepositoryEmpty() {
        // Arrange
        List<Card> list = new ArrayList<>();

        Mockito.when(mockRepository.getAll(Mockito.any(User.class)))
                .thenReturn(list);
        // Act
        List<Card> result = service.getAll(createMockEmployee());

        // Assert
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void Create_Should_Throw_When_DuplicateCard() {

        var mockUser = createMockEmployee();
        var mockCard = createMockCard(mockUser);


        Mockito.when(mockRepository.isDuplicate(mockCard))
                .thenReturn(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.create(mockCard));
    }

    @Test
    public void Create_Should_Throw_When_ExpiredCard() {

        var mockUser = createMockEmployee();
        var mockCard = createMockCard(mockUser);
        mockCard.setExpirationDate(LocalDate.now().minusDays(2));
        Mockito.when(mockRepository.isDuplicate(mockCard))
                .thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(mockCard));
    }

    @Test
    public void Create_Should_Call_Repository_When_CardIsValid() {

        var mockUser = createMockEmployee();
        var mockCard = createMockCard(mockUser);

        Mockito.when(mockRepository.isDuplicate(mockCard))
                .thenReturn(false);

        Mockito.doNothing().when(paymentMethodRepository).create(Mockito.any(PaymentMethod.class));

        // Act
        service.create(mockCard);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(Card.class));
    }

    @Test
    public void Update_Should_Throw_When_UserNotOwner() {

        var mockUser = createMockEmployee();
        var mockUser1 = createMockEmployee();
        mockUser.setId(2);
        var mockCard = createMockCard(mockUser);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(mockUser1, mockCard));
    }

    @Test
    public void Update_Should_Throw_When_DuplicateExits() {

        var mockCustomer = createMockCustomer();
        var mockCard = createMockCard(mockCustomer);
        var mockCard1 = createMockCard(mockCustomer);
        mockCard.setCardNumber("1234");

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCard);

        Mockito.when(mockRepository.isDuplicate(Mockito.any(Card.class)))
                .thenReturn(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.update(mockCustomer, mockCard1));
    }

    @Test
    public void Update_Should_Call_Repository_When_CardValid() {

        var mockUser = createMockEmployee();
        var mockCard = createMockCard(mockUser);

        Mockito.when(mockRepository.isDuplicate(mockCard))
                .thenReturn(false);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCard);

        // Act
        service.update(mockUser, mockCard);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(Card.class));
    }

    @Test
    public void Delete_Should_Throw_When_UserNotOwner() {

        var mockUser = createMockEmployee();
        var mockUser1 = createMockEmployee();
        mockUser1.setId(4);
        var mockCard = createMockCard(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCard);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(mockUser1, 1));
    }

    @Test
    public void Delete_Should_Call_Repository_When_CardValid() {

        var mockUser = createMockEmployee();
        var mockCard = createMockCard(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCard);

        service.delete(mockUser, 1);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .delete(Mockito.any(Card.class));

    }
}
