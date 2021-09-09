package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Role;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl extends BaseRepositoryImpl<Role> implements RoleRepository {

    private final SessionFactory sessionFactory;

    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Role.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean isDuplicate(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Role> query = session.createQuery("from Role where role.role_name = :name", Role.class);
            query.setParameter("name", name);
            List<Role> result = query.list();
            return result.size() > 0;
        }
    }

}
