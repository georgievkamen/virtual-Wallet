package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class RegisterDto extends LoginDto {

    //TODO Remove the down 2
    @Email(message = "Email must be valid!")
    private String email;

    private String passwordConfirm;

    @Pattern(regexp = "08\\d{8}", message = "Phone number must be 08XXXXXXXX!")
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
