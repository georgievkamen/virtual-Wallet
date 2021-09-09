package com.team9.virtualwallet.services;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.models.Role;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import com.team9.virtualwallet.services.contracts.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    @Autowired
    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(Role role) {
        if (repository.isDuplicate(role.getName())) {
            throw new DuplicateEntityException("Role", "name", role.getName());
        }
        repository.create(role);
    }

}
