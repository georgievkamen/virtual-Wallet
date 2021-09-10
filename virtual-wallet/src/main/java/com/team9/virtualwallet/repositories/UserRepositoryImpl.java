package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getByUserName(String userName) {
        return getByFieldList("userName", userName);
    }

    @Override
    public List<User> getByEmail(String email) {
        return getByFieldList("email", email);
    }

    @Override
    public List<User> getByPhoneNumber(String phoneNumber) {
        return getByFieldList("phoneNumber", phoneNumber);
    }

    @Override
    public List<User> filter(Optional<String> userName,
                             Optional<String> phoneNumber,
                             Optional<String> email) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select u from User u ";
            List<String> filters = new ArrayList<>();

            if (userName.isPresent()) {
                filters.add(" u.userName like :userName");
            }

            if (phoneNumber.isPresent()) {
                filters.add(" u.phoneNumber like :phoneNumber");
            }

            if (email.isPresent()) {
                filters.add(" u.email like concat('%',:email,'%') ");
            }

            if (!filters.isEmpty()) {
                baseQuery += " where " + String.join(" and ", filters);
            }

            Query<User> query = session.createQuery(baseQuery, User.class);

            userName.ifPresent(s -> query.setParameter("userName", s));
            phoneNumber.ifPresent(s -> query.setParameter("phoneNumber", s));
            email.ifPresent(s -> query.setParameter("email", s));

            return query.list();
        }

    }
}