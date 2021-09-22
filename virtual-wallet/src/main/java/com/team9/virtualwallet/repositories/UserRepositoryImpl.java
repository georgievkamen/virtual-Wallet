package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.exceptions.EntityNotFoundException;
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
    public List<User> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where isDeleted = false ", User.class);
            return query.list();
        }
    }

    @Override
    public User getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null || user.isDeleted()) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public void delete(User user) {
        user.setDeleted(true);
        user.setEmail("0");
        user.setPhoneNumber("0");
        user.setBlocked(true);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery(" delete from contact_list where user_id or contact_id = :id ")
                    .setParameter("id", user.getId());
            session.update(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<User> filter(Optional<String> userName,
                             Optional<String> phoneNumber,
                             Optional<String> email) {

        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select u from User u where u.isDeleted = false ";
            List<String> filters = new ArrayList<>();

            if (userName.isPresent()) {
                filters.add(" u.userName like :userName");
            }

            if (phoneNumber.isPresent()) {
                filters.add(" u.phoneNumber like :phoneNumber");
            }

            if (email.isPresent()) {
                filters.add(" u.email like :email");
            }

            if (!filters.isEmpty()) {
                baseQuery += " and " + String.join(" and ", filters);
            }

            Query<User> query = session.createQuery(baseQuery, User.class);

            userName.ifPresent(s -> query.setParameter("userName", s));
            phoneNumber.ifPresent(s -> query.setParameter("phoneNumber", s));
            email.ifPresent(s -> query.setParameter("email", s));

            return query.list();
        }

    }
}