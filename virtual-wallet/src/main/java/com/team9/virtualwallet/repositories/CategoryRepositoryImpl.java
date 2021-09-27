package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Category;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.CategoryRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class CategoryRepositoryImpl extends BaseRepositoryImpl<Category> implements CategoryRepository {

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

    @SuppressWarnings("unchecked")
    @Override
    public Object calculateSpendings(Category category, Optional<Date> startDate, Optional<Date> endDate) {
        try (Session session = sessionFactory.openSession()) {
            var baseQuery = "select sum(amount) from Transaction where category.id = :categoryId";
            List<String> dates = new ArrayList<>();

            if (startDate.isPresent()) {
                dates.add(" timestamp > :startDate");
            }

            if (endDate.isPresent()) {
                dates.add(" timestamp < :endDate");
            }

            if (!dates.isEmpty()) {
                baseQuery += " and " + String.join(" and ", dates);
            }

            Query<BigDecimal> query = session.createQuery(baseQuery);

            query.setParameter("categoryId", category.getId());
            startDate.ifPresent(value -> query.setParameter("startDate", value));
            endDate.ifPresent(value -> query.setParameter("endDate", value));

            BigDecimal balance = query.getSingleResult();
            return Objects.requireNonNullElseGet(balance, () -> BigDecimal.valueOf(0));
        }
    }
}
