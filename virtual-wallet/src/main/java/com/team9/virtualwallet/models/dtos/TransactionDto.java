package com.team9.virtualwallet.models.dtos;

import java.math.BigDecimal;

public class TransactionDto {

    private int selectedWalletId;

    private int recipientId;

    private BigDecimal amount;

    public TransactionDto() {
    }

    public int getSelectedWalletId() {
        return selectedWalletId;
    }

    public void setSelectedWalletId(int selectedWalletId) {
        this.selectedWalletId = selectedWalletId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
