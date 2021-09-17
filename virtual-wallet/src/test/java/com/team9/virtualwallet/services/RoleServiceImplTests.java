package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.models.Role;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.team9.virtualwallet.Helpers.createMockRole;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTests {

    @Mock
    RoleRepository mockRepository;

    @InjectMocks
    RoleServiceImpl service;

    @Test
    public void Create_Should_Throw_When_DuplicateRole() {

        var mockRole = createMockRole();

        Mockito.when(mockRepository.isDuplicate(anyString()))
                .thenReturn(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.create(mockRole));
    }

    @Test
    public void Create_Should_Call_Repository_When_RoleValid() {

        var mockRole = createMockRole();

        Mockito.when(mockRepository.isDuplicate(anyString()))
                .thenReturn(false);

        // Act
        service.create(mockRole);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(Role.class));
    }

}
