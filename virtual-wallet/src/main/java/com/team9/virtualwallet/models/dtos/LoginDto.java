package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class LoginDto {

    @NotNull(message = "You must provide an username!")
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters!")
    private String username;

    @NotNull(message = "You must provide a password!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 8 symbols and should contain capital letter, digit and special symbol (+, -, *, &, ^, …)")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
