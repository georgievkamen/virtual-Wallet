package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.ConfirmationTokenRepository;
import com.team9.virtualwallet.services.contracts.ConfirmationTokenService;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository repository;

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConfirmationToken create(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        repository.create(confirmationToken);
        return confirmationToken;
    }
}
