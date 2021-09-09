package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WalletRepositoryImpl extends BaseRepositoryImpl<Wallet> implements WalletRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public WalletRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Wallet.class);
        this.sessionFactory = sessionFactory;
    }
}
