package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.RegisterDto;
import com.team9.virtualwallet.services.contracts.UserService;
import com.team9.virtualwallet.services.mappers.UserModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationRestController {

    private final UserService service;
    private final UserModelMapper modelMapper;

    @Autowired
    public AuthenticationRestController(UserService service, UserModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    public User register(@RequestBody @Valid RegisterDto registerDto) {

        //TODO Ask how to fix @Valid not showing text because of @ControllerAdvice

        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passwords should match!");
        }

        User user = modelMapper.fromRegisterDto(registerDto);
        service.create(user);
        return user;
    }


}
