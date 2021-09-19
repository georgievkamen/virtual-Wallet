package com.team9.virtualwallet.controllers.rest;

import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.dtos.RegisterDto;
import com.team9.virtualwallet.services.contracts.UserService;
import com.team9.virtualwallet.services.mappers.UserModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.team9.virtualwallet.config.RestResponseEntityExceptionHandler.checkFields;

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
    public String register(@RequestBody @Valid RegisterDto registerDto, BindingResult result) {
        checkFields(result);

        User user = modelMapper.fromRegisterDto(registerDto);
        service.create(user);
        return String.format("We have send an activation code to your E-Mail: %s!", user.getEmail());
    }

    @GetMapping("/verify-email")
    public String confirmAccount(@RequestParam("token") String token) {
        service.confirmUser(token);
        return "Your account has been verified!";
    }


}
