package com.team9.virtualwallet.services;

import com.team9.virtualwallet.models.Role;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import com.team9.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.repository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void test() {
        User user = new User();
        Role role = roleRepository.getById(1);
        user.setEmail("asd");
        user.setUserPhoto("asd");
        user.setUsername("asd");
        user.setPassword("passwor");
        user.setPhoneNumber("0887");
        Set<Role> roles = new HashSet<>();
        user.setRoles(roles);
        user.addRole(role);
        repository.create(user);
    }
}
