package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public abstract class BaseTransactionDto {

    @NotNull
    private int selectedWalletId;

    @DecimalMin(value = "0.1")
    private BigDecimal amount;

    @NotBlank
    private String description;

    public BaseTransactionDto() {
    }

    public int getSelectedWalletId() {
        return selectedWalletId;
    }

    public void setSelectedWalletId(int selectedWalletId) {
        this.selectedWalletId = selectedWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
