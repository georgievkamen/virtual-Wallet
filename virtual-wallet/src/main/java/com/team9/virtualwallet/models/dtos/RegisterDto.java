package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegisterDto extends LoginDto {

    //TODO Remove the down 2
    @Email(message = "Email must be valid!")
    private String email;

    private String passwordConfirm;

    @Pattern(regexp = "08\\d{8}", message = "Phone number must be 08XXXXXXXX!")
    private String phoneNumber;

    @Size(min = 2, message = "First name must be at least 2 characters long!")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters long!")
    private String lastName;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}