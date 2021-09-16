package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.CategoryRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepositoryImpl extends BaseRepositoryImpl<Category> implements CategoryRepository {

    //TODO Override delete
    private final SessionFactory sessionFactory;

    @Autowired
    public CategoryRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Category.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Category> getAll(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Category> query = session.createQuery("from Category where user.id = :id", Category.class);
            query.setParameter("id", user.getId());
            return query.list();
        }
    }

    @Override
    public boolean isDuplicate(User user, String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Category> query = session.createQuery("from Category where user.id = :id and name like :name", Category.class);
            query.setParameter("id", user.getId());
            query.setParameter("name", name);
            List<Category> result = query.list();
            return result.size() > 0;
        }
    }
}
