package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WalletRepositoryImpl extends BaseRepositoryImpl<Wallet> implements WalletRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public WalletRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Wallet.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Wallet> getAll(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("from Wallet where user.id = :id", Wallet.class);
            query.setParameter("id", user.getId());
            return query.list();
        }
    }
}
