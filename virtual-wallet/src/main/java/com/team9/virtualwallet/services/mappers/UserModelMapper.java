package com.team9.virtualwallet.services.mappers;

import com.team9.virtualwallet.models.Role;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.RegisterDto;
import com.team9.virtualwallet.models.dtos.UserDto;
import com.team9.virtualwallet.repositories.contracts.RoleRepository;
import com.team9.virtualwallet.repositories.contracts.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserModelMapper {

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserModelMapper(UserRepository repository, RoleRepository roleRepository) {
        this.repository = repository;
        this.roleRepository = roleRepository;
    }

    public User fromRegisterDto(RegisterDto registerDto) {
        User user = new User();

        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        HashSet<Role> roles = new HashSet<>();
        user.setRoles(roles);
        user.addRole(roleRepository.getById(1));
        user.setEmailVerified(false);
        user.setIdVerified(false);

        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());

        return userDto;
    }

    public User fromUserDto(int id, UserDto userDto) {
        User user = repository.getById(id);

        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return user;
    }

}
