package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.InvitationToken;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.InvitationTokenRepository;
import com.team9.virtualwallet.services.contracts.InvitationTokenService;
import org.springframework.stereotype.Service;

@Service
public class InvitationTokenServiceImpl implements InvitationTokenService {

    private final InvitationTokenRepository repository;

    public InvitationTokenServiceImpl(InvitationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public InvitationToken create(User user, String email) {
        InvitationToken invitationToken = new InvitationToken(user, email);
        repository.create(invitationToken);
        return invitationToken;
    }
}
