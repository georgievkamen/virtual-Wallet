package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public abstract class BaseTransactionDto {

    @NotNull(message = "You must provide a wallet!")
    private Integer selectedWalletId;

    @NotNull(message = "You must provide an amount!")
    @DecimalMin(value = "0.01", message = "Amount must be more than 0.01!")
    @DecimalMax(value = "1000000000000", message = "Amount must be less than 1 000 000 000 000!")
    private BigDecimal amount;

    @NotNull(message = "You must provide a description!")
    @Size(min = 2, max = 30, message = "Description must be between 2 and 30 characters long!")
    private String description;

    public BaseTransactionDto() {
    }

    public Integer getSelectedWalletId() {
        return selectedWalletId;
    }

    public void setSelectedWalletId(Integer selectedWalletId) {
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
