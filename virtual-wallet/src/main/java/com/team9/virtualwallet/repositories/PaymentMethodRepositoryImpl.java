package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.models.PaymentMethod;
import com.team9.virtualwallet.repositories.contracts.PaymentMethodRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentMethodRepositoryImpl extends BaseRepositoryImpl<PaymentMethod> implements PaymentMethodRepository {

    private final SessionFactory sessionFactory;

    public PaymentMethodRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, PaymentMethod.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public PaymentMethod getById(int id, String paymentMethod) {
        try (Session session = sessionFactory.openSession()) {
            PaymentMethod obj = session.get(PaymentMethod.class, id);
            if (obj == null) {
                throw new EntityNotFoundException(paymentMethod, id);
            }
            return obj;
        }
    }
}
