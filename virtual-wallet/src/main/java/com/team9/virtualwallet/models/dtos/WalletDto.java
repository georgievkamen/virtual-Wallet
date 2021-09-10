package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.Size;

public class WalletDto {

    @Size(min = 2, max = 30, message = "Wallet name should be between 2 and 30 symbols long!")
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
