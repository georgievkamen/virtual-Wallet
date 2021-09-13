package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.PaymentMethod;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentMethodRepositoryImpl extends BaseRepositoryImpl<PaymentMethod> implements PaymentMethodRepository {

    private final SessionFactory sessionFactory;

    public PaymentMethodRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, PaymentMethod.class);
        this.sessionFactory = sessionFactory;
    }
}
