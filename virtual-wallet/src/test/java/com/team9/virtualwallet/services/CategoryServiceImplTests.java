package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.team9.virtualwallet.Helpers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTests {

    @Mock
    CategoryRepository mockRepository;

    @InjectMocks
    CategoryServiceImpl service;

    @Test
    public void getById_Should_ReturnCategory_When_MatchExist() {
        // Arrange
        var mockUser = createMockEmployee();
        var mockCategory = createMockCategory(mockUser);
        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCategory);
        // Act
        var result = service.getById(mockUser, 1);

        // Assert
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(mockCategory.getName(), result.getName());
    }

    @Test
    public void getById_Should_Throw_When_UnauthorizedUser() {

        var mockUser = createMockEmployee();
        mockUser.setId(2);
        var mockCategory = createMockCategory(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCategory);

        // Arrange
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.getById(createMockCustomer(), 2));
    }

    @Test
    public void getAll_Should_ReturnEmptyList_When_RepositoryEmpty() {
        // Arrange
        List<Category> list = new ArrayList<>();

        Mockito.when(mockRepository.getAll(Mockito.any(User.class)))
                .thenReturn(list);
        // Act
        List<Category> result = service.getAll(createMockEmployee());

        // Assert
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void Create_Should_Throw_When_DuplicateCategory() {

        var mockUser = createMockEmployee();
        var mockCategory = createMockCategory(mockUser);


        Mockito.when(mockRepository.isDuplicate(Mockito.any(User.class), anyString()))
                .thenReturn(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.create(mockUser, mockCategory));
    }

    @Test
    public void Create_Should_Call_Repository_When_CategoryIsValid() {

        var mockUser = createMockEmployee();
        var mockCategory = createMockCategory(mockUser);

        Mockito.when(mockRepository.isDuplicate(Mockito.any(User.class), anyString()))
                .thenReturn(false);

        // Act
        service.create(mockUser, mockCategory);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(Category.class));
    }

    @Test
    public void Update_Should_Throw_When_UserNotOwner() {

        var mockUser = createMockEmployee();
        var mockUser1 = createMockEmployee();
        mockUser.setId(2);
        var mockCategory = createMockCategory(mockUser);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(mockUser1, mockCategory));
    }

    @Test
    public void Update_Should_Throw_When_DuplicateExits() {

        var mockCustomer = createMockCustomer();
        var mockCategory = createMockCategory(mockCustomer);
        var mockCategory1 = createMockCategory(mockCustomer);
        mockCategory.setName("Food");

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCategory);

        Mockito.when(mockRepository.isDuplicate(Mockito.any(User.class), anyString()))
                .thenReturn(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.update(mockCustomer, mockCategory1));
    }

    @Test
    public void Update_Should_Call_Repository_When_CardValid() {

        var mockCustomer = createMockCustomer();
        var mockCategory = createMockCategory(mockCustomer);

        Mockito.when(mockRepository.isDuplicate(Mockito.any(User.class), anyString()))
                .thenReturn(false);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCategory);

        // Act
        service.update(mockCustomer, mockCategory);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .update(Mockito.any(Category.class));
    }

    @Test
    public void Delete_Should_Throw_When_UserNotOwner() {

        var mockUser = createMockEmployee();
        var mockUser1 = createMockEmployee();
        mockUser1.setId(4);
        var mockCategory = createMockCategory(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCategory);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(mockUser1, 1));
    }

    @Test
    public void Delete_Should_Call_Repository_When_CategoryValid() {

        var mockUser = createMockEmployee();
        var mockCategory = createMockCategory(mockUser);

        Mockito.when(mockRepository.getById(anyInt()))
                .thenReturn(mockCategory);

        service.delete(mockUser, 1);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .delete(Mockito.any(Category.class));

    }
}
