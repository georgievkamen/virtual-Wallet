package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.CardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardRepositoryImpl extends BaseRepositoryImpl<Card> implements CardRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CardRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Card.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Card> getAll(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Card> query = session.createQuery("from Card where user.id = :id", Card.class);
            query.setParameter("id", user.getId());
            return query.list();
        }
    }
}
