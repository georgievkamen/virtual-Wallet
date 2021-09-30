package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.InvitationToken;
import com.team9.virtualwallet.repositories.contracts.InvitationTokenRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InvitationTokenRepositoryImpl extends BaseRepositoryImpl<InvitationToken> implements InvitationTokenRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public InvitationTokenRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, InvitationToken.class);
        this.sessionFactory = sessionFactory;
    }
}
