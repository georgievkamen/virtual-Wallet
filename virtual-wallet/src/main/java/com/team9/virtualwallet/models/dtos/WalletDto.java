package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WalletDto {

    @NotNull(message = "You must provide a wallet name!")
    @Size(min = 2, max = 30, message = "Wallet name must be between 2 and 30 characters long!")
    private String name;

    public WalletDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
