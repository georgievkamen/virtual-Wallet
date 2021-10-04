package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.repositories.contracts.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.team9.virtualwallet.Helpers.createMockEmployee;

@ExtendWith(MockitoExtension.class)
public class ConfirmationTokenServiceImplTests {

    @Mock
    ConfirmationTokenRepository mockRepository;

    @InjectMocks
    ConfirmationTokenServiceImpl service;

    @Test
    public void Create_Should_Call_Repository() {

        var mockUser = createMockEmployee();

        // Act
        service.create(mockUser);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(ConfirmationToken.class));
    }
}
