package com.team9.virtualwallet.models.dtos;

import java.math.BigDecimal;

public class ExternalTransactionDto {

    private int selectedCardId;

    private int selectedWalletId;

    private BigDecimal amount;

    public ExternalTransactionDto() {
    }

    public int getSelectedWalletId() {
        return selectedWalletId;
    }

    public void setSelectedWalletId(int selectedWalletId) {
        this.selectedWalletId = selectedWalletId;
    }

    public int getSelectedCardId() {
        return selectedCardId;
    }

    public void setSelectedCardId(int selectedCardId) {
        this.selectedCardId = selectedCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
