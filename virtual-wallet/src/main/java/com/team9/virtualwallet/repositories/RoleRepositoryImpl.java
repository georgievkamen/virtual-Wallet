package com.team9.virtualwallet.repositories;

import com.team9.virtualwallet.models.Role;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl extends BaseRepositoryImpl<Role> implements RoleRepository {

    private final SessionFactory sessionFactory;

    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Role.class);
        this.sessionFactory = sessionFactory;
    }

}
