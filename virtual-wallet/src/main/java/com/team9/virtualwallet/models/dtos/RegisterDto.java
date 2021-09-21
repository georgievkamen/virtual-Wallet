package com.team9.virtualwallet.models.dtos;

import com.team9.virtualwallet.utils.FieldsValueMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@FieldsValueMatch(field = "password", fieldMatch = "passwordConfirm", message = "Passwords do not match!")
public class RegisterDto extends LoginDto {

    @NotNull(message = "You must provide an email!")
    @Email(message = "Email must be valid!")
    private String email;

    @NotNull(message = "You must provide a password confirmation!")
    private String passwordConfirm;

    @NotNull(message = "You must provide a phone number!")
    @Pattern(regexp = "08\\d{8}", message = "Phone number must be 08XXXXXXXX!")
    private String phoneNumber;

    @NotNull(message = "You must provide a first name!")
    @Size(min = 2, max = 50, message = "First name must be at least 2 characters long!")
    private String firstName;

    @NotNull(message = "You must provide a last name!")
    @Size(min = 2, max = 50, message = "Last name must be at least 2 characters long!")
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