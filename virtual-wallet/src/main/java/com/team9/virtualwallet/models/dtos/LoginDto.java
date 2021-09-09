package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginDto {

    @NotBlank(message = "Username can't be blank!")
    @Size(min = 2, max = 20, message = "Username length must be between 2 and 20 symbols!")
    private String username;

    //TODO ADD REGEX
    @NotBlank(message = "Password can't be blank!")
    @Size(min = 2, max = 20, message = "Password length must be between 2 and 20 symbols!")
    private String password;
}
