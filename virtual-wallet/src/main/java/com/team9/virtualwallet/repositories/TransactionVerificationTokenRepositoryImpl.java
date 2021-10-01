package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.TransactionVerificationToken;
import com.team9.virtualwallet.repositories.contracts.TransactionVerificationTokenRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionVerificationTokenRepositoryImpl extends BaseRepositoryImpl<TransactionVerificationToken> implements TransactionVerificationTokenRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionVerificationTokenRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, TransactionVerificationToken.class);
        this.sessionFactory = sessionFactory;
    }
}
