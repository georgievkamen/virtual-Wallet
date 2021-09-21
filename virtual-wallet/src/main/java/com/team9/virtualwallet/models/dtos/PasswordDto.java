package com.team9.virtualwallet.models.dtos;

import com.team9.virtualwallet.utils.FieldsValueMatch;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@FieldsValueMatch(field = "newPassword", fieldMatch = "confirmNewPassword", message = "Passwords do not match!")
public class PasswordDto {

    @NotNull(message = "You must provide your old password!")
    @NotBlank(message = "Current password can't be blank!")
    private String oldPassword;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and should contain capital letter, digit and special symbol (+, -, *, &, ^, …)")
    @NotNull(message = "You must provide your new password!")
    private String newPassword;

    @NotNull(message = "You must provide your new password confirmation!")
    private String confirmNewPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
