package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;

public class TransactionDto extends BaseTransactionDto {

    @NotNull(message = "You must provide a recipient!")
    private Integer recipientId;

    public TransactionDto() {
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

}
