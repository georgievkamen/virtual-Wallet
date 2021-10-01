package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.TransactionVerificationToken;
import com.team9.virtualwallet.repositories.contracts.TransactionVerificationTokenRepository;
import com.team9.virtualwallet.services.contracts.TransactionVerificationTokenService;
import org.springframework.stereotype.Service;

@Service
public class TransactionVerificationTokenServiceImpl implements TransactionVerificationTokenService {

    private final TransactionVerificationTokenRepository repository;

    public TransactionVerificationTokenServiceImpl(TransactionVerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public TransactionVerificationToken create(Transaction transaction) {
        TransactionVerificationToken transactionVerificationToken = new TransactionVerificationToken(transaction);
        repository.create(transactionVerificationToken);
        return transactionVerificationToken;
    }
}
