package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Card;
import com.team9.virtualwallet.repositories.contracts.CardRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CardRepositoryImpl extends BaseRepositoryImpl<Card> implements CardRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CardRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Card.class);
        this.sessionFactory = sessionFactory;
    }


}
