package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegisterDto extends LoginDto {

    //TODO Remove the down 2
    @Email
    @NotBlank(message = "Email can't be blank!")
    @Size(min = 2, max = 100, message = "Email length must be between 2 and 100 symbols!")
    private String email;

    @NotBlank(message = "Confirm Password can't be blank!")
    private String passwordConfirm;

    @NotBlank(message = "Phone number can't be blank!")
    @Pattern(regexp = "08[789]\\d{7}", message = "Phone number must be 08xxxxxxxx!")
    private String phoneNumber;

    public RegisterDto() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
