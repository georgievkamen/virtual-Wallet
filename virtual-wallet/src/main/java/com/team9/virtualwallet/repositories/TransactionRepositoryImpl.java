package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Transaction;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.TransactionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepositoryImpl extends BaseRepositoryImpl<Transaction> implements TransactionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Transaction.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Transaction> getAll(User user) {
        try (Session session = sessionFactory.openSession()) {
            //TODO Check if it works correctly
            Query<Transaction> query = session.createQuery("from Transaction where sender.id = :id or recipient.id = :id", Transaction.class);
            query.setParameter("id", user.getId());
            return query.list();
        }
    }
}
