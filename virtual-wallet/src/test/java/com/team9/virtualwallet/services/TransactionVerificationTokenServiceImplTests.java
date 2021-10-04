package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.TransactionVerificationToken;
import com.team9.virtualwallet.models.enums.TransactionType;
import com.team9.virtualwallet.repositories.contracts.TransactionVerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.team9.virtualwallet.Helpers.createMockTransaction;

@ExtendWith(MockitoExtension.class)
public class TransactionVerificationTokenServiceImplTests {

    @Mock
    TransactionVerificationTokenRepository mockRepository;

    @InjectMocks
    TransactionVerificationTokenServiceImpl service;

    @Test
    public void Create_Should_Call_Repository() {

        var mockTransaction = createMockTransaction();
        mockTransaction.setTransactionType(TransactionType.LARGE_UNVERIFIED);

        // Act
        service.create(mockTransaction);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1))
                .create(Mockito.any(TransactionVerificationToken.class));
    }
}
