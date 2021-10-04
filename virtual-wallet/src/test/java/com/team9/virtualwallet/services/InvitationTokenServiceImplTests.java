package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.InvitationToken;
import com.team9.virtualwallet.repositories.contracts.InvitationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.team9.virtualwallet.Helpers.createMockEmployee;

@ExtendWith(MockitoExtension.class)
public class InvitationTokenServiceImplTests {

    @Mock
    InvitationTokenRepository mockRepository;

    @InjectMocks
    InvitationTokenServiceImpl service;

    @Test
    public void Create_Should_Call_Repository() {

        var mockUser = createMockEmployee();

        // Act
        service.create(mockUser, "email@abv.bg");

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(InvitationToken.class));
    }
}
