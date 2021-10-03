package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;
import com.team9.virtualwallet.repositories.contracts.WalletRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
            Query<Wallet> query = session.createQuery("from Wallet where user.id = :id and isDeleted = false ", Wallet.class);
            query.setParameter("id", user.getId());
            return query.list();
        }
    }

    @Override
    public Wallet getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Wallet wallet = session.get(Wallet.class, id);
            if (wallet == null) {
                throw new EntityNotFoundException("Wallet", id);
            }
            return wallet;
        }
    }

    @Override
    public void delete(Wallet wallet) {
        wallet.setDeleted(true);
        super.update(wallet);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getTotalBalanceByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery("select sum(balance) from Wallet where user.id = :id");
            query.setParameter("id", user.getId());
            return query.getSingleResult();
        }
    }

    public boolean isDuplicate(User user, Wallet wallet) {
        try (Session session = sessionFactory.openSession()) {
            Query<Wallet> query = session.createQuery("from Wallet where name = :name and user.id = :user and isDeleted = false ", Wallet.class);
            query.setParameter("user", user.getId());
            query.setParameter("name", wallet.getName());
            List<Wallet> result = query.list();
            return result.size() > 0;
        }
    }

}
