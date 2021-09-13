package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.repositories.contracts.ConfirmationTokenRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ConfirmationTokenRepositoryImpl extends BaseRepositoryImpl<ConfirmationToken> implements ConfirmationTokenRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ConfirmationTokenRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, ConfirmationToken.class);
        this.sessionFactory = sessionFactory;
    }
}
