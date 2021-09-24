package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;

public class TransactionDto extends BaseTransactionDto {

    @NotNull(message = "You must provide a recipient!")
    private Integer recipientId;

    public TransactionDto() {
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }
}
